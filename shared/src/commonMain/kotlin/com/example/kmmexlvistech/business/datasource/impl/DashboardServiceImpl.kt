package com.example.kmmexlvistech.business.datasource.impl



import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.models.DashboardResponse
import com.example.kmmexlvistech.business.datasource.models.MenuDashboardResponse

import com.example.kmmexlvistech.business.datasource.services.DashboardApiServices
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

class DashboardServiceImpl(
    private val httpClient: HttpClient
): DashboardApiServices {


    override suspend fun getDashboardMenu(token: String): MenuDashboardResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += DashboardApiServices.GET_DASHBOARD_MENU
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())

        }.body()
    }



}