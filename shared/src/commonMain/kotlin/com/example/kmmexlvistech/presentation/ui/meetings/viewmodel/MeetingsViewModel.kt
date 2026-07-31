package com.example.kmmexlvistech.presentation.ui.meetings.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CallFilterDto
import com.example.kmmexlvistech.business.datasource.dto.CallSortDto
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateMeetingRequestDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsFilterDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsSortDto
import com.example.kmmexlvistech.business.repos.CallsRepository
import com.example.kmmexlvistech.business.repos.MeetingsRepository
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.millisToIsoString
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MeetingsViewModel(private val meetingsRepository: MeetingsRepository) : ViewModel() {
    val state: MutableState<MeetingsState> = mutableStateOf(MeetingsState())

    fun onEvent(event: MeetingsEvent) {
        when (event) {
            is MeetingsEvent.GetMeetingss -> {
                state.value = state.value.copy(page = 1, meetings = emptyList())
                fetchCalls(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is MeetingsEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                val nextPage = state.value.page + 1
                fetchCalls(token = event.token, userCode = event.userCode, page = nextPage, append = true)
            }
            is MeetingsEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, meetings = emptyList())
                fetchCalls(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is MeetingsEvent.StatusFilterChanged -> {
                state.value = state.value.copy(selectedStatusName = event.statusName, page = 1, meetings = emptyList())
                fetchCalls(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is MeetingsEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is MeetingsEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
            is MeetingsEvent.SelectMeeting -> state.value = state.value.copy(selectedMeetingId = event.meetingId)
            is MeetingsEvent.LoadMeetingDetails -> loadMeetingDetails(event.token, event.meetingId)

            is MeetingsEvent.SubjectChanged -> state.value = state.value.copy(subject = event.value)
            is MeetingsEvent.DescriptionChanged -> state.value = state.value.copy(description = event.value)
            is MeetingsEvent.RelatedRecordTypeChanged -> state.value = state.value.copy(
                relatedRecordType = event.type, selectedLead = null, leadSearchResults = emptyList()
            )
            is MeetingsEvent.LeadSearchQueryChanged -> {
                state.value = state.value.copy(leadSearchQuery = event.query)
                searchLeads(event.token, event.query)
            }
            is MeetingsEvent.LeadSelected -> state.value = state.value.copy(
                selectedLead = event.lead, leadSearchResults = emptyList(), leadSearchQuery = event.lead.firstName
            )
            is MeetingsEvent.MeetingTypeChanged -> state.value = state.value.copy(meetingType = event.type)
            is MeetingsEvent.LocationChanged -> state.value = state.value.copy(location = event.value)
            is MeetingsEvent.MeetingLinkChanged -> state.value = state.value.copy(meetingLink = event.value)
            is MeetingsEvent.MeetingPriorityChanged -> state.value = state.value.copy(priorityId = event.id)
            is MeetingsEvent.MeetingStatusChanged -> state.value = state.value.copy(statusId = event.id)
            is MeetingsEvent.MeetingAssignedToChanged -> state.value = state.value.copy(assignedToUserId = event.userId)
            is MeetingsEvent.StartDateTimeChanged -> state.value = state.value.copy(startDateTimeMillis = event.millis)

            is MeetingsEvent.EndDateTimeChanged -> state.value = state.value.copy(endDateTimeMillis = event.millis)
            is MeetingsEvent.ReminderDateTimeChanged -> state.value = state.value.copy(reminderDateTimeMillis = event.millis)
            is MeetingsEvent.LoadCreateMeetingLookups -> loadCreateMeetingLookups(event.token)
            is MeetingsEvent.SaveMeeting -> saveMeeting(event.token)
            is MeetingsEvent.ResetCreateMeetingForm -> state.value = state.value.copy(
                subject = "", description = "", relatedRecordType = "LEAD",
                leadSearchQuery = "", leadSearchResults = emptyList(), selectedLead = null,
                meetingType = "OFFLINE", location = "", meetingLink = "",
                priorityId = null, statusId = null, assignedToUserId = null,
                startDateTimeMillis = null, endDateTimeMillis = null, reminderDateTimeMillis = null,
                isSavingMeeting = false, saveMeetingError = null, meetingSaved = false,
            )
            is MeetingsEvent.DeleteMeeting -> deleteMeeting(event.token, event.meetingId)
            is MeetingsEvent.ResetDeleteMeetingFlag -> state.value = state.value.copy(meetingDeleted = false)
        }
    }

    private fun deleteMeeting(token: String, meetingId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isDeletingMeeting = true, deleteMeetingError = null)

            meetingsRepository.deleteMeeting(token, meetingId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isDeletingMeeting = false, deleteMeetingError = "Failed to delete meeting")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isDeletingMeeting = false,
                                meetingDeleted = true,
                                meetings = state.value.meetings.filterNot { it.id == meetingId },
                            )
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Meeting deleted successfully"))
                        } else {
                            state.value = state.value.copy(
                                isDeletingMeeting = false,
                                deleteMeetingError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isDeletingMeeting = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun searchLeads(token: String, query: String) {
        if (query.isBlank()) {
            state.value = state.value.copy(leadSearchResults = emptyList())
            return
        }
        viewModelScope.launch {
            meetingsRepository.searchLeadsForMeeting(token, query).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(leadSearchResults = dataState.data.data)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun loadCreateMeetingLookups(token: String) {
        viewModelScope.launch {
            meetingsRepository.getMeetingPriorities(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(priorityOptions = dataState.data.data.map { DropdownOption(it.priorityId, it.name) })
                }
            }.launchIn(viewModelScope)

            meetingsRepository.getMeetingStatuses(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(statusOptions = dataState.data.data.map { DropdownOption(it.statusId, it.name) })
                }
            }.launchIn(viewModelScope)

            meetingsRepository.getUserList(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(userOptions = dataState.data.data.map { DropdownOption(it.userId, it.fullName) })
                }
            }.launchIn(viewModelScope)
        }
    }

    fun saveMeeting(token: String) {
        val s = state.value

        if (s.subject.isBlank() || s.selectedLead == null || s.priorityId == null ||
            s.statusId == null || s.assignedToUserId == null ||
            s.startDateTimeMillis == null || s.endDateTimeMillis == null ||
            (s.meetingType == "OFFLINE" && s.location.isBlank())
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }
        viewModelScope.launch {
            state.value = state.value.copy(isSavingMeeting = true, saveMeetingError = null)

            val requestDto = CreateMeetingRequestDto(
                subject = s.subject,
                description = s.description.ifBlank { null },
                priorityId = s.priorityId,
                statusId = s.statusId,
                startDateTime = millisToIsoString(s.startDateTimeMillis),
                endDateTime = millisToIsoString(s.endDateTimeMillis),
                reminderDateTime = s.reminderDateTimeMillis?.let { millisToIsoString(it) },
                meetingLocation = s.location.ifBlank { null },
                meetingType = s.meetingType,
                meetingLink = s.meetingLink.ifBlank { null },
                assignedTo = s.assignedToUserId,
                leadId = if (s.relatedRecordType == "LEAD") s.selectedLead.id else null,
                accountId = null,
                contactId = null,
            )

            meetingsRepository.createMeeting(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isSavingMeeting = false,
                            saveMeetingError = "Failed to save meeting"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }

                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value =
                                state.value.copy(isSavingMeeting = false, meetingSaved = true)
                            appendToMessageQueue(
                                UIComponent.ToastSimple(
                                    dataState.data.message ?: "Meeting created successfully"
                                )
                            )
                        } else {
                            state.value = state.value.copy(
                                isSavingMeeting = false,
                                saveMeetingError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(isSavingMeeting = dataState.progressBarState == ProgressBarState.Loading)
                    }

                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadMeetingDetails(token: String, meetingId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingDetails = true, detailsError = null)

            meetingsRepository.getMeetingDetails(token, meetingId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isLoadingDetails = false,
                            detailsError = "Failed to load meeting details"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }

                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                meeting = dataState.data.data,
                                detailsError = null
                            )
                        } else {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                detailsError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(isLoadingDetails = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun fetchCalls(token: String, userCode: Int, page: Int, append: Boolean) {
        viewModelScope.launch {
            state.value = if (append) {
                state.value.copy(isLoadingMore = true)
            } else {
                state.value.copy(progressBarState = ProgressBarState.Loading)
            }

            val requestDto = MeetingsRequestDto(
                page = page,
                pageSize = state.value.pageSize,
                filters = MeetingsFilterDto(
                    search = state.value.searchQuery.ifBlank { null },
                    assignedTo = userCode,
                    statusName = state.value.selectedStatusName,
                ),
                sort = MeetingsSortDto(),
            )

            meetingsRepository.getMeetings(
                systemToken = token,
                meetingsRequestDto = requestDto,
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> onEvent(MeetingsEvent.Error(dataState.uiComponent))
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                            isLoadingMore = false,
                        )
                        if (dataState.data?.status == true) {
                            val incoming = dataState.data.data
                            state.value = state.value.copy(
                                meetings = if (append) state.value.meetings + incoming else incoming,
                                page = page,
                                totalRecords = dataState.data.totalRecords,
                            )
                        } else {
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data?.message.orEmpty()))
                        }
                    }
                    is DataState.Loading -> {
                        if (!append) {
                            state.value = state.value.copy(progressBarState = dataState.progressBarState)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) return
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove()
            state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("AppDebug: removeHeadMessage: Nothing to remove")
        }
    }
}