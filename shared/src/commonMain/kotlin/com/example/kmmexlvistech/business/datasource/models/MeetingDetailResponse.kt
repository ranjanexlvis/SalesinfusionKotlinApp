package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MeetingDetailResponse(
    val status: Boolean,
    val message: String,
    val data: ActivityMeetingData
)

@Serializable
data class ActivityMeetingData(
    val id: String,
    val lead: Lead,
    val status: TaskStatus,
    val account: JsonElement? = null,
    val contact: JsonElement? = null,
    val subject: String,
    val isActive: Boolean,
    val priority: Priority,
    val meetingId: Int,
    val assignedTo: AssignedTo,
    val description: String,
    val endDateTime: String,
    val meetingLink: String? = null,
    val meetingType: String,
    val startDateTime: String,
    val createddatetime: String,
    val meetingLocation: String? = null,
    val modifieddatetime: String? = null,
    val reminderDateTime: String,
)