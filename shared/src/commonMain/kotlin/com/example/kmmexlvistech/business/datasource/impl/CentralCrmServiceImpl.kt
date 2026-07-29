package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadSourcesResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadStatusesResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityPrioritiesResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityStatusesResponse
import com.example.kmmexlvistech.business.datasource.models.UserListRequestDto
import com.example.kmmexlvistech.business.datasource.models.UserListResponse
import com.example.kmmexlvistech.business.datasource.services.CentralCrmApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class CentralCrmServiceImpl(private val httpClient: HttpClient) : CentralCrmApiServices {


    override suspend fun getActivityStatuses(token: String, requestDto: CentralCrmRequestDto): ActivityStatusesResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += CentralCrmApiServices.GET_ACTIVITY_STATUSES
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun getActivityPriorities(token: String, requestDto: CentralCrmRequestDto): ActivityPrioritiesResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += CentralCrmApiServices.GET_ACTIVITY_PRIORITIES
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun getUserList(token: String, requestDto: UserListRequestDto): UserListResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += CentralCrmApiServices.GET_USER_LIST
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun getLeadStatuses(token: String, requestDto: CentralCrmRequestDto): LeadStatusesResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += CentralCrmApiServices.GET_LEAD_STATUSES
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun getLeadSources(token: String, requestDto: CentralCrmRequestDto): LeadSourcesResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += CentralCrmApiServices.GET_LEAD_SOURCES
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }
}