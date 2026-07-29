package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.CreateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateTaskResponse
import com.example.kmmexlvistech.business.datasource.dto.DeleteTaskResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.TaskListResponse
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.models.TaskDetailResponse
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.TaskApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class TaskServiceImpl(private val httpClient: HttpClient) : TaskApiServices {

    override suspend fun getTaskList(token: String, taskRequestDto: TaskRequestDto): TaskListResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += TaskApiServices.GET_TASK_LIST }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(taskRequestDto)
        }.body()
    }

    override suspend fun getTaskDetails(token: String, id: String): TaskDetailResponse {
        return httpClient.get {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += "${TaskApiServices.GET_TASK_DETAILS}/$id" }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun createTask(token: String, requestDto: CreateTaskRequestDto): CreateTaskResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += TaskApiServices.CREATE_TASK }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun searchLeads(token: String, requestDto: LeadsRequestDto): LeadsListResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += TaskApiServices.SEARCH_LEADS }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun deleteTask(token: String, id: String): DeleteTaskResponse {
        return httpClient.delete {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${TaskApiServices.DELETE_TASK}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }
}