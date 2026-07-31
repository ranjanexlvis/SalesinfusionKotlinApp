package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.CreateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateTaskResponse
import com.example.kmmexlvistech.business.datasource.dto.DeleteTaskResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TaskListResponse
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.UpdateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateTaskResponse
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.models.TaskDetailResponse


interface TaskApiServices {

    companion object{

        const val GET_TASK_LIST = "Task/List"
        const val GET_TASK_DETAILS = "Task"
        const val CREATE_TASK = "Task"
        const val UPDATE_TASK = "Task"
        const val SEARCH_LEADS = "Lead/GetList"
        const val DELETE_TASK = "Task/Delete"
    }

    suspend fun getTaskList(token: String,taskListResponse: TaskRequestDto): TaskListResponse

    suspend fun getTaskDetails(token: String, id: String): TaskDetailResponse
    suspend fun updateTask(token: String, requestDto: UpdateTaskRequestDto): UpdateTaskResponse

    suspend fun createTask(token: String, requestDto: CreateTaskRequestDto): CreateTaskResponse
    suspend fun searchLeads(token: String, requestDto: LeadsRequestDto): LeadsListResponse
    suspend fun deleteTask(token: String, id: String): DeleteTaskResponse


}