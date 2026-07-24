package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse


interface LeadsApiServices {

    companion object{

        const val GET_LEADS_LIST = "Lead/List"
        const val GET_LEADS_DETAILS = "Lead"
    }

    suspend fun getLeadsList(token: String,leadsRequestDto: LeadsRequestDto): LeadsListResponse
    suspend fun getLeadsDetails(token: String,id:String): LeadDetailsResponse



}