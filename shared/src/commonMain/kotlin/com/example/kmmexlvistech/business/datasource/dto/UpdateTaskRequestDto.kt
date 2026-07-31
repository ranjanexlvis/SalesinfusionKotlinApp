package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTaskRequestDto(
    val id: String,
    val subject: String,
    val description: String? = null,
    val priorityId: Int,
    val statusId: Int,
    val startDateTime: String,
    val endDateTime: String,
    val dueDateTime: String,
    val completionDateTime: String? = null,
    val reminderDateTime: String? = null,
    val assignedTo: Int,
    val leadId: String? = null,
    val accountId: String? = null,
    val contactId: String? = null,
)

@Serializable
data class UpdateTaskResponse(
    val status: Boolean = false,
    val message: String? = null,
)