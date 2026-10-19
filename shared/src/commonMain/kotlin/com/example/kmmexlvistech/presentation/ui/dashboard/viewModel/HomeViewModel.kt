package com.example.kmmexlvistech.presentation.ui.dashboard.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO

import com.example.kmmexlvistech.business.repos.HomeRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {
    val state: MutableState<HomeState> = mutableStateOf(
        HomeState()
    )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.GetWidgetWithActivities-> {
                getWidgetActivity(event.token)
            }

            is HomeEvent.GetDashboard-> {
                getDashboard(event.token)
            }

            is HomeEvent.GetQuickActionMenu-> {
                getQuickActionMenu(event.token)
            }

            is HomeEvent.ToggleQuickActionSheet -> {
                state.value = state.value.copy(isSheetOpen = !state.value.isSheetOpen)
            }


            is HomeEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }
            else -> {}
        }
    }

    private fun getDashboard(systemToken: String) {
        viewModelScope.launch {
            state.value = state.value.copy(progressBarState = ProgressBarState.Loading)


            homeRepository.getDashboard(
                systemToken = systemToken

            ).onEach { dataState ->
                when (dataState) {


                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onEvent(HomeEvent.Error(dataState.uiComponent))
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                        )
                        if (dataState.data?.status == true) {


                            state.value = state.value.copy(
                                getDashboard =  dataState.data,
                            )
                        } else {
                            appendToMessageQueue(
                                UIComponent.ToastSimple(dataState.data?.message.orEmpty())
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getWidgetActivity(systemToken: String) {
        viewModelScope.launch {
            state.value = state.value.copy(progressBarState = ProgressBarState.Loading)


            homeRepository.getWidgetWithActivities(
                systemToken = systemToken

            ).onEach { dataState ->
                when (dataState) {


                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onEvent(HomeEvent.Error(dataState.uiComponent))
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                        )
                        if (dataState.data?.status == true) {


                            state.value = state.value.copy(
                                getWidgetActivityResponse =  dataState.data,
                            )
                        } else {
                            appendToMessageQueue(
                                UIComponent.ToastSimple(dataState.data?.message.orEmpty())
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getQuickActionMenu(systemToken: String) {
       val quickActionDTO =  QuickActionDto( moduleCode = "DASHBOARD")
        viewModelScope.launch {
            state.value = state.value.copy(progressBarState = ProgressBarState.Loading)


            homeRepository.getQuickActionMenu(
                systemToken = systemToken,quickActionDTO

            ).onEach { dataState ->
                when (dataState) {


                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onEvent(HomeEvent.Error(dataState.uiComponent))
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                        )
                        if (dataState.data?.status == true) {


                            state.value = state.value.copy(
                                getQuickActionResponse =  dataState.data,
                            )
                        } else {
                            appendToMessageQueue(
                                UIComponent.ToastSimple(dataState.data?.message.orEmpty())
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }



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