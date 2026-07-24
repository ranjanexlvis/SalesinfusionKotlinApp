package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallsListResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<CallDto> = emptyList(),
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("totalRecords") val totalRecords: Int = 0,
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class CallDto(
    @SerialName("id") val id: String,
    @SerialName("callId") val callId: Int,
    @SerialName("subject") val subject: String,
    @SerialName("description") val description: String? = null,
    @SerialName("isActive") val isActive: Boolean = true,
    @SerialName("startDateTime") val startDateTime: String,
    @SerialName("endDateTime") val endDateTime: String? = null,
    @SerialName("reminderDateTime") val reminderDateTime: String? = null,
    @SerialName("lead") val lead: CallLeadDto? = null,
    @SerialName("account") val account: CallAccountDto? = null,
    @SerialName("contact") val contact: CallContactDto? = null,
    @SerialName("status") val status: CallStatusDto,
    @SerialName("priority") val priority: CallPriorityDto,
    @SerialName("assignedTo") val assignedTo: CallAssigneeDto,
)

@Serializable
data class CallLeadDto(
    @SerialName("id") val id: String,
    @SerialName("leadId") val leadId: Int,
    @SerialName("leadName") val leadName: String,
)

@Serializable
data class CallAccountDto(
    @SerialName("id") val id: String,
    @SerialName("accountName") val accountName: String? = null,
)

@Serializable
data class CallContactDto(
    @SerialName("id") val id: String,
    @SerialName("contactName") val contactName: String? = null,
)

@Serializable
data class CallStatusDto(
    @SerialName("id") val id: String,
    @SerialName("statusId") val statusId: Int,
    @SerialName("statusName") val statusName: String,
)

@Serializable
data class CallPriorityDto(
    @SerialName("id") val id: String,
    @SerialName("priorityId") val priorityId: Int,
    @SerialName("priorityName") val priorityName: String,
)

@Serializable
data class CallAssigneeDto(
    @SerialName("id") val id: String,
    @SerialName("userid") val userId: Int,
    @SerialName("fullName") val fullName: String,
)