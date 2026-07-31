package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmFilterDto
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmSortDto
import com.example.kmmexlvistech.business.datasource.dto.CreateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateTaskResponse
import com.example.kmmexlvistech.business.datasource.dto.DeleteTaskResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsFilterDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsSortDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TaskListResponse
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateTaskResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityPrioritiesResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityStatusesResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.models.TaskDetailResponse
import com.example.kmmexlvistech.business.datasource.models.UserListRequestDto
import com.example.kmmexlvistech.business.datasource.models.UserListResponse
import com.example.kmmexlvistech.business.datasource.services.CentralCrmApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.TaskApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class TaskRepository(
    private val service: TaskApiServices,
    private val centralCrmApiServices: CentralCrmApiServices,
        ) {
            fun getTask(systemToken:String, taskReqDto: TaskRequestDto
            ): Flow<DataState<TaskListResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    val response = service.getTaskList(systemToken, taskListResponse = taskReqDto)
                        emit(DataState.Data(response, response.status.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }

            fun getTaskDetails(systemToken:String, id: String
            ): Flow<DataState<TaskDetailResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    val response = service.getTaskDetails(systemToken, id = id)
                    emit(DataState.Data(response, response.status.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }

             fun createTask(systemToken: String, requestDto: CreateTaskRequestDto): Flow<DataState<CreateTaskResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try { emit(DataState.Data(service.createTask(systemToken, requestDto))) }
                catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

            fun getActivityStatuses(systemToken: String): Flow<DataState<ActivityStatusesResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    val requestDto = CentralCrmRequestDto(
                        filters = CentralCrmFilterDto(),
                        sort = CentralCrmSortDto(field = "sortOrder")
                    )
                    // activityTypeId is mandatory — 1 assumed for Task; confirm against real API if tasks use a different type id
                    val body = buildJsonObject {
                        put("page", 1); put("pageSize", 20)
                        put("filters", buildJsonObject {
                            put("activityTypeId", 1); put("search", ""); put("isActive", true)
                        })
                        put("sort", buildJsonObject { put("field", "sortOrder"); put("direction", "asc") })
                    }
                    emit(DataState.Data(centralCrmApiServices.getActivityStatuses(systemToken, requestDto)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

    fun getActivityPriorities(systemToken: String): Flow<DataState<ActivityPrioritiesResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try {
            val requestDto = CentralCrmRequestDto(sort = CentralCrmSortDto(field = "sortOrder"))
            emit(DataState.Data(centralCrmApiServices.getActivityPriorities(systemToken, requestDto)))
        } catch (e: Exception) {
            emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
        }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

     fun getUserList(systemToken: String): Flow<DataState<UserListResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try {
            emit(DataState.Data(centralCrmApiServices.getUserList(systemToken, UserListRequestDto())))
        } catch (e: Exception) {
            emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
        }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

    fun searchLeadsForTask(systemToken: String, search: String): Flow<DataState<LeadsListResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try {
            val requestDto = LeadsRequestDto(
                page = 1, pageSize = 20,
                filters = LeadsFilterDto(search = search.ifBlank { null }, 1012),
                sort = LeadsSortDto(),
            )
            emit(DataState.Data(service.searchLeads(systemToken, requestDto)))
        } catch (e: Exception) {
            emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
        }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

     fun deleteTask(systemToken: String, taskId: String): Flow<DataState<DeleteTaskResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try {
            emit(DataState.Data(service.deleteTask(systemToken, taskId)))
        } catch (e: Exception) {
            emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
        }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

     fun updateTask(systemToken: String, requestDto: UpdateTaskRequestDto): Flow<DataState<UpdateTaskResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try {
            emit(DataState.Data(service.updateTask(systemToken, requestDto)))
        } catch (e: Exception) {
            emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
        }
        emit(DataState.Loading(ProgressBarState.Idle))
    }
}