package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadsListResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<LeadDto> = emptyList(),
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("totalRecords") val totalRecords: Int = 0,
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class LeadDto(
    @SerialName("id") val id: String,
    @SerialName("leadId") val leadId: Int,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("assignedTo") val assignedTo: LeadAssigneeDto,
    @SerialName("leadStatus") val leadStatus: LeadStatusDto,
    @SerialName("isConverted") val isConverted: Boolean = false,
    @SerialName("createddatetime") val createdDateTime: String,
    @SerialName("modifieddatetime") val modifiedDateTime: String? = null,
)

@Serializable
data class LeadAssigneeDto(
    @SerialName("userid") val userId: Int,
    @SerialName("firstname") val firstName: String,
    @SerialName("lastname") val lastName: String,
)

@Serializable
data class LeadStatusDto(
    @SerialName("leadStatusId") val leadStatusId: Int,
    @SerialName("status_code") val statusCode: String,
    @SerialName("status_name") val statusName: String,
)