package com.example.kmmexlvistech.presentation.ui.leads.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadsFilterDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsSortDto
import com.example.kmmexlvistech.business.repos.LeadsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class LeadsViewModel(
    private val leadsRepository: LeadsRepository
) : ViewModel() {
    val state: MutableState<LeadsState> = mutableStateOf(LeadsState())

    fun onEvent(event: LeadsEvent) {
        when (event) {
            is LeadsEvent.GetLeads -> {
                state.value = state.value.copy(page = 1, leads = emptyList())
                fetchLeads(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is LeadsEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                val nextPage = state.value.page + 1
                fetchLeads(token = event.token, userCode = event.userCode, page = nextPage, append = true)
            }
            is LeadsEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, leads = emptyList())
                fetchLeads(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is LeadsEvent.StatusFilterChanged -> {
                state.value = state.value.copy(selectedStatusId = event.statusId, page = 1, leads = emptyList())
                fetchLeads(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is LeadsEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is LeadsEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
            is LeadsEvent.LoadLeadDetails -> loadLeadDetails(event.token, event.leadId)
            is LeadsEvent.SelectLead -> state.value = state.value.copy(selectedLeadId = event.leadId)

        }
    }

    private fun fetchLeads(token: String, userCode: Int, page: Int, append: Boolean) {
        viewModelScope.launch {
            state.value = if (append) state.value.copy(isLoadingMore = true)
            else state.value.copy(progressBarState = ProgressBarState.Loading)

            val requestDto = LeadsRequestDto(
                page = page,
                pageSize = state.value.pageSize,
                filters = LeadsFilterDto(
                    search = state.value.searchQuery.ifBlank { null },
                    assignedTo = userCode,
                    statusId = state.value.selectedStatusId,
                ),
                sort = LeadsSortDto(),
            )

            leadsRepository.getLeads(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> onEvent(LeadsEvent.Error(dataState.uiComponent))
                    is DataState.Data -> {
                        state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                        if (dataState.data?.status == true) {
                            val incoming = dataState.data.data
                            state.value = state.value.copy(
                                leads = if (append) state.value.leads + incoming else incoming,
                                page = page,
                                totalRecords = dataState.data.totalRecords,
                            )
                        } else {
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data?.message.orEmpty()))
                        }
                    }
                    is DataState.Loading -> {
                        if (!append) state.value = state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadLeadDetails(token: String, leadId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingDetails = true, detailsError = null)

            leadsRepository.getLeadsDetails(token, leadId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isLoadingDetails = false,
                            detailsError = "Failed to load lead details"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                lead = dataState.data.data,
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
                        state.value = state.value.copy(isLoadingDetails = dataState.progressBarState == ProgressBarState.Loading)
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