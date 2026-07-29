package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallsRequestDto(
    val page: Int = 1,
    val pageSize: Int = 20,
    val filters: CallFilterDto,
    val sort: CallSortDto = CallSortDto(),
)

@Serializable
data class CallFilterDto(
    val search: String = "",
    val priorityName: String = "",
    val statusName: String = "",
    val assignedTo: Int,              // mandatory — no default, must always be passed
    val leadName: String = "",
    val accountName: String = "",
    val contactName: String = "",
    val isActive: Boolean = true,
)

@Serializable
data class CallSortDto(
    val field: String = "startDateTime",
    val direction: String = "asc",
)