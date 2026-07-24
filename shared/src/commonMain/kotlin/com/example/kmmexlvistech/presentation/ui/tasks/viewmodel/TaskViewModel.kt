package com.example.kmmexlvistech.presentation.ui.tasks.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.TaskFilterDto
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.TaskSortDto
import com.example.kmmexlvistech.business.repos.TaskRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    val state: MutableState<TaskState> = mutableStateOf(TaskState())

    fun onEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.GetTasks -> {
                state.value = state.value.copy(page = 1, tasks = emptyList())
                fetchTasks(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is TasksEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                val nextPage = state.value.page + 1
                fetchTasks(token = event.token, userCode = event.userCode, page = nextPage, append = true)
            }
            is TasksEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, tasks = emptyList())
                fetchTasks(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is TasksEvent.PriorityFilterChanged -> {
                state.value = state.value.copy(selectedPriorityName = event.priorityName, page = 1, tasks = emptyList())
                fetchTasks(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is TasksEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is TasksEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
        }
    }

    private fun fetchTasks(token: String, userCode: Int, page: Int, append: Boolean) {
        viewModelScope.launch {
            state.value = if (append) {
                state.value.copy(isLoadingMore = true)
            } else {
                state.value.copy(progressBarState = ProgressBarState.Loading)
            }

            val requestDto = TaskRequestDto(
                page = page,
                pageSize = state.value.pageSize,
                filters = TaskFilterDto(
                    search = state.value.searchQuery.ifBlank { null },
                    assignedTo = 1021,
                    priorityName = state.value.selectedPriorityName,
                ),
                sort = TaskSortDto(),
            )

            taskRepository.getTask(
                systemToken = token,
                taskReqDto = requestDto,
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> onEvent(TasksEvent.Error(dataState.uiComponent))
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                            isLoadingMore = false,
                        )
                        if (dataState.data?.status == true) {
                            val incoming = dataState.data.data
                            state.value = state.value.copy(
                                tasks = if (append) state.value.tasks + incoming else incoming,
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