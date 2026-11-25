package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.ContactActionResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactDetailResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactRolesResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactsListResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateContactRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LookupRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateContactRequestDto
import com.example.kmmexlvistech.business.datasource.services.ContactsApiServices
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

class ContactsServiceImpl(private val httpClient: HttpClient) : ContactsApiServices {

    override suspend fun getContacts(token: String, request: ContactsRequestDto): ContactsListResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += ContactsApiServices.LIST }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getContactDetails(token: String, id: String): ContactDetailResponse =
        httpClient.get {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += "${ContactsApiServices.CONTACT}/$id" }
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun createContact(token: String, request: CreateContactRequestDto): ContactActionResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += ContactsApiServices.CONTACT }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun updateContact(token: String, request: UpdateContactRequestDto): ContactActionResponse =
        httpClient.put {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += ContactsApiServices.CONTACT }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deleteContact(token: String, id: String): ContactActionResponse =
        httpClient.delete {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += "${ContactsApiServices.DELETE}/$id" }
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun getContactRoles(token: String, request: LookupRequestDto): ContactRolesResponse =
        httpClient.post {
            url { takeFrom(BASE_URL_SERVER_ONE); encodedPath += ContactsApiServices.ROLES }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}