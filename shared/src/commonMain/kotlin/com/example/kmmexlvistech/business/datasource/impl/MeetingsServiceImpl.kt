package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsListResponse
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
import com.example.kmmexlvistech.business.datasource.services.MeetingsApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class MeetingsServiceImpl(private val httpClient: HttpClient): MeetingsApiServices {

    override suspend fun getMeetingsList(token: String,meetingsRequestDto: MeetingsRequestDto): MeetingsListResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += MeetingsApiServices.GET_MEETINGS_LIST
            }
            header("Authorization","Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(meetingsRequestDto)
        }.body()
    }
}