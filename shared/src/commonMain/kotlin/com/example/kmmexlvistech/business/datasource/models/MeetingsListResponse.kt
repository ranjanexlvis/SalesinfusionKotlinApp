package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingsListResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<MeetingDto> = emptyList(),
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("totalRecords") val totalRecords: Int = 0,
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class MeetingDto(
    @SerialName("id") val id: String,
    @SerialName("meetingId") val meetingId: Int,
    @SerialName("subject") val subject: String,
    @SerialName("description") val description: String? = null,
    @SerialName("isActive") val isActive: Boolean = true,
    @SerialName("meetingType") val meetingType: String, // "ONLINE" / "OFFLINE"
    @SerialName("meetingLink") val meetingLink: String? = null, // populated when ONLINE
    @SerialName("meetingLocation") val meetingLocation: String? = null, // populated when OFFLINE
    @SerialName("startDateTime") val startDateTime: String,
    @SerialName("endDateTime") val endDateTime: String? = null,
    @SerialName("reminderDateTime") val reminderDateTime: String? = null,
    @SerialName("lead") val lead: MeetingLeadDto? = null,
    @SerialName("account") val account: MeetingAccountDto? = null,
    @SerialName("contact") val contact: MeetingContactDto? = null,
    @SerialName("status") val status: MeetingStatusDto,
    @SerialName("priority") val priority: MeetingPriorityDto,
    @SerialName("assignedTo") val assignedTo: MeetingAssigneeDto,
)

@Serializable
data class MeetingLeadDto(
    @SerialName("id") val id: String,
    @SerialName("leadId") val leadId: Int,
    @SerialName("leadName") val leadName: String,
)

@Serializable
data class MeetingAccountDto(
    @SerialName("id") val id: String,
    @SerialName("accountName") val accountName: String? = null,
)

@Serializable
data class MeetingContactDto(
    @SerialName("id") val id: String,
    @SerialName("contactName") val contactName: String? = null,
)

@Serializable
data class MeetingStatusDto(
    @SerialName("id") val id: String,
    @SerialName("statusId") val statusId: Int,
    @SerialName("statusName") val statusName: String,
)

@Serializable
data class MeetingPriorityDto(
    @SerialName("id") val id: String,
    @SerialName("priorityId") val priorityId: Int,
    @SerialName("priorityName") val priorityName: String,
)

@Serializable
data class MeetingAssigneeDto(
    @SerialName("id") val id: String,
    @SerialName("userid") val userId: Int,
    @SerialName("fullName") val fullName: String,
)