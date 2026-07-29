package com.example.kmmexlvistech.business.datasource.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TaskDetailResponse(
    val status: Boolean,
    val message: String,
    val data: ActivityTaskData
)

@Serializable
data class ActivityTaskData(
    val id: String,
    val lead: Lead,
    val status: TaskStatus,
    val taskId: Int,
    val account: JsonElement? = null,
    val contact: JsonElement? = null,
    val subject: String,
    val isActive: Boolean,
    val priority: Priority,
    val assignedTo: AssignedTo,
    val description: String,
    val dueDateTime: String,
    val endDateTime: String,
    val startDateTime: String,
    val createddatetime: String,
    val modifieddatetime: String? = null,      // ✅ was non-nullable, now nullable
    val reminderDateTime: String,
    val completionDateTime: String? = null,    // ✅ likely null before a task is completed — make nullable too
)
@Serializable
data class Lead(
    val id: String,
    val leadId: Int,
    val leadName: String
)

@Serializable
data class TaskStatus(
    val id: String,
    val statusId: Int,
    val statusName: String
)

@Serializable
data class Priority(
    val id: String,
    val priorityId: Int,
    val priorityName: String
)

@Serializable
data class AssignedTo(
    val id: String,
    val userid: Int,
    val fullName: String
)