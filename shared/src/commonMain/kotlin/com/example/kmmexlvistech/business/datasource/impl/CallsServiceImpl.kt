package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateCallRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateCallResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.models.CallDetailResponse
import com.example.kmmexlvistech.business.datasource.models.DeleteCallResponse
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
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

class CallsServiceImpl(private val httpClient: HttpClient): CallsApiServices {

    override suspend fun getCallsList(token: String,callsRequestDto: CallsRequestDto): CallsListResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += CallsApiServices.GET_CALLS_LIST
            }
            header("Authorization","Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(callsRequestDto)
        }.body()
    }

    override suspend fun createCall(token: String, requestDto: CreateCallRequestDto): CreateCallResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += CallsApiServices.CREATE_CALL }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun searchLeads(token: String, requestDto: LeadsRequestDto): LeadsListResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += CallsApiServices.SEARCH_LEADS }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun getCallDetails(token: String, id: String): CallDetailResponse {
        return httpClient.get {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${CallsApiServices.GET_CALL_DETAILS}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun deleteCall(token: String, id: String): DeleteCallResponse {
        return httpClient.delete {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${CallsApiServices.DELETE_CALL}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }
}