package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.CreateMeetingRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateMeetingResponse
import com.example.kmmexlvistech.business.datasource.dto.DeleteMeetingResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsListResponse
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.models.MeetingDetailResponse
import com.example.kmmexlvistech.business.datasource.services.MeetingsApiServices
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

class  MeetingsServiceImpl(private val httpClient: HttpClient) : MeetingsApiServices {

    override suspend fun getMeetingsList(token: String, meetingsRequestDto: MeetingsRequestDto): MeetingsListResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += MeetingsApiServices.GET_MEETINGS_LIST
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(meetingsRequestDto)
        }.body()
    }

    override suspend fun getMeetingDetails(token: String, id: String): MeetingDetailResponse {
        return httpClient.get {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${MeetingsApiServices.GET_MEETING_DETAILS}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun createMeeting(token: String, requestDto: CreateMeetingRequestDto): CreateMeetingResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += MeetingsApiServices.CREATE_MEETING }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun searchLeads(token: String, requestDto: LeadsRequestDto): LeadsListResponse {
        return httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += MeetingsApiServices.SEARCH_LEADS }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }

    override suspend fun deleteMeeting(token: String, id: String): DeleteMeetingResponse {
        return httpClient.delete {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${MeetingsApiServices.DELETE_MEETING}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }
}