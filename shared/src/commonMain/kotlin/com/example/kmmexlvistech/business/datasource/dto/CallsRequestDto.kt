package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallsRequestDto(
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("filters") val filters: CallFilterDto,
    @SerialName("sort") val sort: CallSortDto = CallSortDto(),
)

@Serializable
data class CallFilterDto(
    @SerialName("search") val search: String? = null,
    @SerialName("priorityName") val priorityName: String? = null,
    @SerialName("statusName") val statusName: String? = null,
    @SerialName("assignedTo") val assignedTo: Int, // mandatory — UserCode
    @SerialName("leadName") val leadName: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("contactName") val contactName: String? = null,
    @SerialName("isActive") val isActive: Boolean = true,
)

@Serializable
data class CallSortDto(
    @SerialName("field") val field: String = "startDateTime",
    @SerialName("direction") val direction: String = "asc",
)