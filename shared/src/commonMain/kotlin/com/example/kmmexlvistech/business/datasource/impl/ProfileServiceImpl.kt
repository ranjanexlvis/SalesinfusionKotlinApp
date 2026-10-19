package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.models.ProfileResponse
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileRequestDto
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileResponse
import com.example.kmmexlvistech.business.datasource.services.ProfileApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

// business/datasource/impl/ProfileServiceImpl.kt
class ProfileServiceImpl(private val httpClient: HttpClient) : ProfileApiServices {

    // GET: no body and no content type
    override suspend fun getProfile(token: String): ProfileResponse =
        httpClient.get {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += ProfileApiServices.GET_PROFILE }
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun updateProfile(token: String, request: UpdateProfileRequestDto): UpdateProfileResponse =
        httpClient.put {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += ProfileApiServices.UPDATE_PROFILE }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}