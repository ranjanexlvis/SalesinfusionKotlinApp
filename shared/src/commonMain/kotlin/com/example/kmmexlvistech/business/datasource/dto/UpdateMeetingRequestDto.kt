package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable


@Serializable
data class UpdateMeetingRequestDto(
    val id: String,
    val subject: String,
    val description: String? = null,
    val priorityId: Int,
    val statusId: Int,
    val startDateTime: String,
    val endDateTime: String,
    val reminderDateTime: String? = null,
    val meetingLocation: String? = null,
    val meetingType: String,            // "OFFLINE" | "ONLINE"
    val meetingLink: String? = null,
    val assignedTo: Int,
    val leadId: String? = null,
    val accountId: String = "",         // your API sample sends "" for unused links
    val contactId: String = "",
)

// Doc only says "returns the meeting id", so `data` is parsed leniently
@Serializable
data class UpdateMeetingResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: kotlinx.serialization.json.JsonElement? = null,
)