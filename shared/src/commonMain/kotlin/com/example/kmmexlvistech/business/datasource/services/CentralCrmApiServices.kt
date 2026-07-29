package com.example.kmmexlvistech.business.datasource.services

import com.example.kmmexlvistech.business.datasource.dto.CentralCrmRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadSourcesResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadStatusesResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityPrioritiesResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityStatusesResponse
import com.example.kmmexlvistech.business.datasource.models.UserListRequestDto
import com.example.kmmexlvistech.business.datasource.models.UserListResponse


interface CentralCrmApiServices {
    companion object {
        const val GET_LEAD_STATUSES = "CentralCrmData/LeadStatuses"
        const val GET_LEAD_SOURCES = "CentralCrmData/LeadSources"
        const val GET_ACTIVITY_STATUSES = "CentralCrmData/ActivityStatuses"
        const val GET_ACTIVITY_PRIORITIES = "CentralCrmData/ActivityPriorities"
        const val GET_USER_LIST = "UserManagement/List"
    }


    suspend fun getLeadStatuses(token: String, requestDto: CentralCrmRequestDto): LeadStatusesResponse
    suspend fun getLeadSources(token: String, requestDto: CentralCrmRequestDto): LeadSourcesResponse
    suspend fun getActivityStatuses(token: String, requestDto: CentralCrmRequestDto): ActivityStatusesResponse
    suspend fun getActivityPriorities(token: String, requestDto: CentralCrmRequestDto): ActivityPrioritiesResponse
    suspend fun getUserList(token: String, requestDto: UserListRequestDto): UserListResponse
}