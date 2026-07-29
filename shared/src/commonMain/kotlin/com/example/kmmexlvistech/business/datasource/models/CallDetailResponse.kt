package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CallDetailResponse(
    val status: Boolean,
    val message: String,
    val data: ActivityCallData
)

@Serializable
data class ActivityCallData(
    val id: String,
    val lead: Lead,
    val callId: Int,
    val status: TaskStatus,
    val account: JsonElement? = null,
    val contact: JsonElement? = null,
    val subject: String,
    val isActive: Boolean,
    val priority: Priority,
    val assignedTo: AssignedTo,
    val description: String,
    val endDateTime: String,
    val startDateTime: String,
    val createddatetime: String,
    val modifieddatetime: String? = null,
    val reminderDateTime: String,
)

@Serializable
data class DeleteCallResponse(
    val status: Boolean = false,
    val message: String? = null,
)