package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.MeetingsListResponse
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse


interface MeetingsApiServices {

    companion object{

        const val GET_MEETINGS_LIST = "Meeting/List"
    }

    suspend fun getMeetingsList(token: String,meetingsRequestDto: MeetingsRequestDto): MeetingsListResponse



}