package com.example.kmmexlvistech.business.datasource.services

import com.example.kmmexlvistech.business.datasource.dto.ContactActionResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactDetailResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactRolesResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactsListResponse
import com.example.kmmexlvistech.business.datasource.dto.ContactsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateContactRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LookupRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateContactRequestDto

interface ContactsApiServices {
    suspend fun getContacts(token: String, request: ContactsRequestDto): ContactsListResponse
    suspend fun getContactDetails(token: String, id: String): ContactDetailResponse
    suspend fun createContact(token: String, request: CreateContactRequestDto): ContactActionResponse
    suspend fun updateContact(token: String, request: UpdateContactRequestDto): ContactActionResponse
    suspend fun deleteContact(token: String, id: String): ContactActionResponse
    suspend fun getContactRoles(token: String, request: LookupRequestDto): ContactRolesResponse

    companion object {
        // BASE_URL_SERVER_ONE already ends in /api/, so: no prefix, no leading slash
        const val LIST = "Contact/List"
        const val CONTACT = "Contact"
        const val DELETE = "Contact/Delete"
        const val ROLES = "CentralCrmData/ContactRoles"
    }
}