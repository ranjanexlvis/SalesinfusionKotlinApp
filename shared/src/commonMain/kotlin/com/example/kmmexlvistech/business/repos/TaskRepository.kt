package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TaskListResponse
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.models.TaskDetailResponse
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.TaskApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class TaskRepository(
            private val service: TaskApiServices,
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



        }