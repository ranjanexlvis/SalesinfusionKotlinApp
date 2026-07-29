package com.example.kmmexlvistech.presentation.ui.calls.viewmodel


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CallFilterDto
import com.example.kmmexlvistech.business.datasource.dto.CallSortDto
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateCallRequestDto
import com.example.kmmexlvistech.business.repos.CallsRepository
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class CallsViewModel(
    private val callsRepository: CallsRepository
) : ViewModel() {
    val state: MutableState<CallsState> = mutableStateOf(CallsState())

    fun onEvent(event: CallsEvent) {
        when (event) {
            is CallsEvent.GetCalls -> {
                state.value = state.value.copy(page = 1, calls = emptyList())
                fetchCalls(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is CallsEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                val nextPage = state.value.page + 1
                fetchCalls(token = event.token, userCode = event.userCode, page = nextPage, append = true)
            }
            is CallsEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, calls = emptyList())
                fetchCalls(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is CallsEvent.StatusFilterChanged -> {
                state.value = state.value.copy(selectedStatusName = event.statusName, page = 1, calls = emptyList())
                fetchCalls(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is CallsEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is CallsEvent.OnRemoveHeadFromQueue -> removeHeadMessage()

            is CallsEvent.SubjectChanged -> state.value = state.value.copy(subject = event.value)
            is CallsEvent.DescriptionChanged -> state.value = state.value.copy(description = event.value)
            is CallsEvent.RelatedRecordTypeChanged -> state.value = state.value.copy(
                relatedRecordType = event.type, selectedLead = null, leadSearchResults = emptyList()
            )
            is CallsEvent.LeadSearchQueryChanged -> {
                state.value = state.value.copy(leadSearchQuery = event.query)
                searchLeads(event.token, event.query)
            }
            is CallsEvent.LeadSelected -> state.value = state.value.copy(
                selectedLead = event.lead, leadSearchResults = emptyList(), leadSearchQuery = event.lead.firstName
            )
            is CallsEvent.CallPriorityChanged -> state.value = state.value.copy(priorityId = event.id)
            is CallsEvent.CallStatusChanged -> state.value = state.value.copy(statusId = event.id)
            is CallsEvent.CallAssignedToChanged -> state.value = state.value.copy(assignedToUserId = event.userId)
            is CallsEvent.StartDateTimeChanged -> state.value = state.value.copy(startDateTimeMillis = event.millis)
            is CallsEvent.EndDateTimeChanged -> state.value = state.value.copy(endDateTimeMillis = event.millis)
            is CallsEvent.ReminderDateTimeChanged -> state.value = state.value.copy(reminderDateTimeMillis = event.millis)
            is CallsEvent.LoadCreateCallLookups -> loadCreateCallLookups(event.token)
            is CallsEvent.SaveCall -> saveCall(event.token)

            is CallsEvent.ResetCreateCallForm -> state.value = state.value.copy(
                subject = "", description = "", relatedRecordType = "LEAD",
                leadSearchQuery = "", leadSearchResults = emptyList(), selectedLead = null,
                priorityId = null, statusId = null, assignedToUserId = null,
                startDateTimeMillis = null, endDateTimeMillis = null, reminderDateTimeMillis = null,
                isSavingCall = false, saveCallError = null, callSaved = false,
            )
            is CallsEvent.SelectCall -> state.value = state.value.copy(selectedCallId = event.callId)
            is CallsEvent.LoadCallDetails -> loadCallDetails(event.token, event.callId)
            is CallsEvent.DeleteCall -> deleteCall(event.token, event.callId)
            is CallsEvent.ResetDeleteCallFlag -> state.value = state.value.copy(callDeleted = false)
        }
    }

    private fun loadCallDetails(token: String, callId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingDetails = true, detailsError = null)

            callsRepository.getCallDetails(token, callId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isLoadingDetails = false, detailsError = "Failed to load call details")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(isLoadingDetails = false, call = dataState.data.data, detailsError = null)
                        } else {
                            state.value = state.value.copy(isLoadingDetails = false, detailsError = dataState.data?.message ?: "Something went wrong")
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isLoadingDetails = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun deleteCall(token: String, callId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isDeletingCall = true, deleteCallError = null)

            callsRepository.deleteCall(token, callId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isDeletingCall = false, deleteCallError = "Failed to delete call")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isDeletingCall = false,
                                callDeleted = true,
                                calls = state.value.calls.filterNot { it.id == callId },
                            )
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Call deleted successfully"))
                        } else {
                            state.value = state.value.copy(isDeletingCall = false, deleteCallError = dataState.data?.message ?: "Something went wrong")
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isDeletingCall = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun searchLeads(token: String, query: String) {
        if (query.isBlank()) {
            state.value = state.value.copy(leadSearchResults = emptyList())
            return
        }
        viewModelScope.launch {
            callsRepository.searchLeadsForCall(token, query).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(leadSearchResults = dataState.data.data)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadCreateCallLookups(token: String) {
        viewModelScope.launch {
            callsRepository.getCallPriorities(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(
                        priorityOptions = dataState.data.data.map {
                            DropdownOption(
                                it.priorityId,
                                it.name
                            )
                        }
                    )
                }
            }.launchIn(viewModelScope)

            callsRepository.getCallStatuses(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(
                        statusOptions = dataState.data.data.map {
                            DropdownOption(
                                it.statusId,
                                it.name
                            )
                        }
                    )
                }
            }.launchIn(viewModelScope)
            callsRepository.getUserList(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(
                        userOptions = dataState.data.data.map {
                            DropdownOption(
                                it.userId,
                                it.fullName
                            )
                        }
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun saveCall(token: String) {
        val s = state.value

        if (s.subject.isBlank() || s.selectedLead == null || s.priorityId == null ||
            s.statusId == null || s.assignedToUserId == null ||
            s.startDateTimeMillis == null || s.endDateTimeMillis == null
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }
        viewModelScope.launch {
            state.value = state.value.copy(isSavingCall = true, saveCallError = null)

            val requestDto = CreateCallRequestDto(
                subject = s.subject,
                description = s.description.ifBlank { null },
                priorityId = s.priorityId,
                statusId = s.statusId,
                startDateTime = millisToIsoString(s.startDateTimeMillis),
                endDateTime = millisToIsoString(s.endDateTimeMillis),
                reminderDateTime = s.reminderDateTimeMillis?.let { millisToIsoString(it) },
                assignedTo = s.assignedToUserId,
                leadId = if (s.relatedRecordType == "LEAD") s.selectedLead.id else null,
                accountId = null,
                contactId = null,
            )
            callsRepository.createCall(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isSavingCall = false,
                            saveCallError = "Failed to save call"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }

                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(isSavingCall = false, callSaved = true)
                            appendToMessageQueue(
                                UIComponent.ToastSimple(
                                    dataState.data.message ?: "Call created successfully"
                                )
                            )
                        } else {
                            state.value = state.value.copy(
                                isSavingCall = false,
                                saveCallError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(isSavingCall = dataState.progressBarState == ProgressBarState.Loading)
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

            val requestDto = CallsRequestDto(
                page = page,
                pageSize = state.value.pageSize,
                filters = CallFilterDto(
                    search = state.value.searchQuery.ifBlank { "" },
                    priorityName = "",
                    statusName = state.value.selectedStatusName ?: "",
                    assignedTo = userCode,
                    leadName = "",
                    accountName = "",
                    contactName = "",
                    isActive = true,
                ),
                sort = CallSortDto(field = "startDateTime", direction = "asc"),
            )

            callsRepository.getCalls(
                systemToken = token,
                callsRequestDto = requestDto,
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> onEvent(CallsEvent.Error(dataState.uiComponent))
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                            isLoadingMore = false,
                        )
                        if (dataState.data?.status == true) {
                            val incoming = dataState.data.data
                            state.value = state.value.copy(
                                calls = if (append) state.value.calls + incoming else incoming,
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

fun millisToIsoString(millis: Long): String {
    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
    val dt = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    val month = dt.monthNumber.toString().padStart(2, '0')
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')
    return "${dt.year}-$month-${day}T$hour:$minute:00"
}

fun millisToDisplayDate(millis: Long?): String {
    if (millis == null) return ""
    val dt = kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "${dt.dayOfMonth} $month ${dt.year}"
}

fun millisToDisplayTime(millis: Long?): String {
    if (millis == null) return ""
    val dt = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    val hour = if (dt.hour % 12 == 0) 12 else dt.hour % 12
    val amPm = if (dt.hour < 12) "AM" else "PM"
    val hourStr = hour.toString().padStart(2, '0')
    val minuteStr = dt.minute.toString().padStart(2, '0')
    return "$hourStr:$minuteStr $amPm"
}