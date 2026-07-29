package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable


@Serializable
data class CreateMeetingRequestDto(
    val subject: String,
    val description: String? = null,
    val priorityId: Int,
    val statusId: Int,
    val startDateTime: String,
    val endDateTime: String,
    val reminderDateTime: String? = null,
    val meetingLocation: String? = null,
    val meetingType: String, // "OFFLINE" | "ONLINE"
    val meetingLink: String? = null,
    val assignedTo: Int,
    val leadId: String? = null,
    val accountId: String? = null,
    val contactId: String? = null,
)

@Serializable
data class CreateMeetingResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: CreateMeetingResponseData? = null
)

@Serializable
data class CreateMeetingResponseData(
    val id: String = "",
    val meetingId: Int = 0,
)