package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class TaskServiceImpl(private val httpClient: HttpClient): TaskApiServices {

    override suspend fun getTaskList(token: String,taskRequestDto: TaskRequestDto): TaskListResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += TaskApiServices.GET_TASK_LIST
            }
            header("Authorization","Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(taskRequestDto)
        }.body()
    }

    override suspend fun getTaskDetails(token: String, id: String): TaskDetailResponse {
        return httpClient.get {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${TaskApiServices.GET_TASK_DETAILS}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }
}