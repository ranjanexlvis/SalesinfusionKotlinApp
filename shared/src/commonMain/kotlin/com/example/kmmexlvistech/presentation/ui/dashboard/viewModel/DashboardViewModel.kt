package com.example.kmmexlvistech.presentation.ui.dashboard.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.repos.DashboardRepository

import com.example.kmmexlvistech.business.repos.HomeRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class DashboardViewModel(
    private val homeRepository: DashboardRepository
) : ViewModel() {
    val state: MutableState<DashboardState> = mutableStateOf(DashboardState())

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.GetDashboardMenu -> getDashboardMenu(event.token)
            is DashboardEvent.Error -> appendToMessageQueue(event.uiComponent) // adjust field name if different
            is DashboardEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
            else -> Unit
        }
    }

    private fun getDashboardMenu(systemToken: String) {
        homeRepository.getDashboardMenu(systemToken = systemToken)
            .onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> Unit
                    is DataState.Response -> {
                        state.value = state.value.copy(progressBarState = ProgressBarState.Idle)
                        onEvent(DashboardEvent.Error(dataState.uiComponent))
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(progressBarState = ProgressBarState.Idle)
                        val data = dataState.data
                        if (data?.status == true) {
                            state.value = state.value.copy(getDashboard = data)
                        } else {
                            appendToMessageQueue(UIComponent.ToastSimple(data?.message.orEmpty()))
                        }
                    }
                    is DataState.Loading ->
                        state.value = state.value.copy(progressBarState = dataState.progressBarState)
                }
            }.launchIn(viewModelScope)
    }

    // appendToMessageQueue and removeHeadMessage stay as they are




    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) {
            println("CUSTOM_TAG: onEvent: ${uiComponent.message}")
            return
        }
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
            println("AppDebug: removeHeadMessage: Nothing to remove from DialogQueue")
        }
    }
}