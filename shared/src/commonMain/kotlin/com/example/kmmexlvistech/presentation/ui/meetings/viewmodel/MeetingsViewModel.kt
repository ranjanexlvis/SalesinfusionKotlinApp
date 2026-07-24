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
import com.example.kmmexlvistech.business.datasource.dto.MeetingsFilterDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsSortDto
import com.example.kmmexlvistech.business.repos.CallsRepository
import com.example.kmmexlvistech.business.repos.MeetingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MeetingsViewModel(
    private val meetingsRepository: MeetingsRepository
) : ViewModel() {
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