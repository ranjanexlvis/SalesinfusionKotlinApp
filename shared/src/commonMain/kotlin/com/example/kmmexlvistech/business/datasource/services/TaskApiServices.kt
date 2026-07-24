package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TaskListResponse
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
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
    }

    suspend fun getTaskList(token: String,taskListResponse: TaskRequestDto): TaskListResponse

    suspend fun getTaskDetails(token: String, id: String): TaskDetailResponse





}