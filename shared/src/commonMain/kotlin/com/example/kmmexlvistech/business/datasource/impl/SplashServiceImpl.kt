package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.LoginHeaderRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.LoginWithPassRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.VerifyOtpDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.services.SplashApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SplashServiceImpl(
    private val httpClient: HttpClient
) : SplashApiServices {


    override suspend fun systemToken(tokenReqDto: TokenRequestDTO): CommonGenericResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += SplashApiServices.SYSTEM_TOKEN
            }
            contentType(ContentType.Application.Json)
            setBody(tokenReqDto)

        }.body()
    }


    override suspend fun login(
        loginRequestDTO: LoginRequestDTO, systemToken: String
    ): LoginResponse {
        return httpClient.post {

                url {
                    takeFrom(BASE_URL_SERVER_ONE)
                    encodedPath += SplashApiServices.LOGIN
                }
                header("Authorization", "Bearer ${systemToken}")
                contentType(ContentType.Application.Json)
                setBody(loginRequestDTO)

        }.body()


    }

}