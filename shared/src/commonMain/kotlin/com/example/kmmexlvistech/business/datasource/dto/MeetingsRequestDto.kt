package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingsRequestDto(
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("filters") val filters: MeetingsFilterDto,
    @SerialName("sort") val sort: MeetingsSortDto = MeetingsSortDto(),
)

@Serializable
data class MeetingsFilterDto(
    @SerialName("search") val search: String? = null,
    @SerialName("meetingType") val meetingType: String? = null, // e.g. "ONLINE" / "OFFLINE"
    @SerialName("priorityName") val priorityName: String? = null,
    @SerialName("statusName") val statusName: String? = null,
    @SerialName("assignedTo") val assignedTo: Int, // mandatory — UserCode
    @SerialName("leadName") val leadName: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("contactName") val contactName: String? = null,
    @SerialName("isActive") val isActive: Boolean = true,
)

@Serializable
data class MeetingsSortDto(
    @SerialName("field") val field: String = "startDateTime",
    @SerialName("direction") val direction: String = "asc",
)