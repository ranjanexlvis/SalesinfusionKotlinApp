package com.example.kmmexlvistech.business.datasource.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCallRequestDto(
    val subject: String,
    val description: String? = null,
    val priorityId: Int,
    val statusId: Int,
    val startDateTime: String,
    val endDateTime: String,
    val reminderDateTime: String? = null,
    val assignedTo: Int,
    val leadId: String? = null,
    val accountId: String? = null,
    val contactId: String? = null,
)

@Serializable
data class CreateCallResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: CreateCallResponseData? = null
)

@Serializable
data class CreateCallResponseData(
    val id: String = "",
    val callId: Int = 0,
)