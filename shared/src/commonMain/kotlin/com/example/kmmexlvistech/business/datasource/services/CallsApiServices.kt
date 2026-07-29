package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateCallRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateCallResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.models.CallDetailResponse
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.DeleteCallResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse


interface CallsApiServices {
    companion object {
        const val GET_CALLS_LIST = "Call/List"
        const val GET_CALL_DETAILS = "Call" // id appended in impl
        const val CREATE_CALL = "Call"
        const val SEARCH_LEADS = "Lead/List"
        const val DELETE_CALL = "Call/Delete" // id appended in impl
    }

    suspend fun getCallsList(token: String, callsRequestDto: CallsRequestDto): CallsListResponse
    suspend fun getCallDetails(token: String, id: String): CallDetailResponse
    suspend fun createCall(token: String, requestDto: CreateCallRequestDto): CreateCallResponse
    suspend fun searchLeads(token: String, requestDto: LeadsRequestDto): LeadsListResponse
    suspend fun deleteCall(token: String, id: String): DeleteCallResponse
}