package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCallRequestDto(
    val id: String,
    val subject: String,
    val description: String? = null,
    val priorityId: Int,
    val statusId: Int,
    val startDateTime: String,
    val endDateTime: String,
    val reminderDateTime: String? = null,
    val assignedTo: String,
    val leadId: String? = null,
    val accountId: String = "",   // your API sample sends "" for unused links
    val contactId: String = "",
)

// The doc only says "returns the call id", so the shape of `data` is unknown.
// JsonElement parses a string or an object without crashing.
@Serializable
data class UpdateCallResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: kotlinx.serialization.json.JsonElement? = null,
)