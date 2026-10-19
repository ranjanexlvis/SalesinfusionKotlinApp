package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.AccountActionResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountDetailResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountTypesResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountsListResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateAccountRequestDto
import com.example.kmmexlvistech.business.datasource.dto.IndustriesResponse
import com.example.kmmexlvistech.business.datasource.dto.LookupRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateAccountRequestDto
import com.example.kmmexlvistech.business.datasource.services.AccountsApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom


class AccountsServiceImpl(private val httpClient: HttpClient) : AccountsApiServices {

    override suspend fun getAccounts(token: String, request: AccountsRequestDto): AccountsListResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += AccountsApiServices.LIST }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getAccountDetails(token: String, id: String): AccountDetailResponse =
        httpClient.get {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += "${AccountsApiServices.ACCOUNT}/$id" }
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun createAccount(token: String, request: CreateAccountRequestDto): AccountActionResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += AccountsApiServices.ACCOUNT }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun updateAccount(token: String, request: UpdateAccountRequestDto): AccountActionResponse =
        httpClient.put {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += AccountsApiServices.ACCOUNT }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deleteAccount(token: String, id: String): AccountActionResponse =
        httpClient.delete {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += "${AccountsApiServices.DELETE}/$id" }
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun getAccountTypes(token: String, request: LookupRequestDto): AccountTypesResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += AccountsApiServices.ACCOUNT_TYPES }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getIndustries(token: String, request: LookupRequestDto): IndustriesResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += AccountsApiServices.INDUSTRIES }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}