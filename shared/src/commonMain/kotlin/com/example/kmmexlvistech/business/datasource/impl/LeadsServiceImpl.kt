package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class LeadsServiceImpl(private val httpClient: HttpClient) : LeadsApiServices {

    override suspend fun getLeadsList(token: String, leadsRequestDto: LeadsRequestDto): LeadsListResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += LeadsApiServices.GET_LEADS_LIST
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(leadsRequestDto)
        }.body()
    }

    override suspend fun getLeadsDetails(token: String, id: String): LeadDetailsResponse {
        return httpClient.get {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += "${LeadsApiServices.GET_LEADS_DETAILS}/$id"
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }
}