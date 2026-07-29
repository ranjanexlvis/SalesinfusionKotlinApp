package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class CreateTaskRequestDto(
    val subject: String,
    val description: String? = null,
    val relatedRecordType: String, // "LEAD" | "ACCOUNT" | "CONTACT"
    val leadId: Int? = null,
    val accountId: Int? = null,
    val contactId: Int? = null,
    val priorityId: Int,
    val statusId: Int,
    val assignedTo: Int,
    val startDateTime: String,
    val endDateTime: String,
    val dueDateTime: String,
    val reminderDateTime: String? = null,
)

@Serializable
data class CreateTaskResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: CreateTaskResponseData? = null
)

@Serializable
data class CreateTaskResponseData(
    val id: String = "",
    val taskId: Int = 0,
)