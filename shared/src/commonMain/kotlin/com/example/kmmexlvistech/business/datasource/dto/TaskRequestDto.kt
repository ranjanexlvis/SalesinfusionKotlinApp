package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequestDto(
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("filters") val filters: TaskFilterDto,
    @SerialName("sort") val sort: TaskSortDto = TaskSortDto(),
)

@Serializable
data class TaskFilterDto(
    @SerialName("search") val search: String? = null,
    @SerialName("assignedTo") val assignedTo: Int, // mandatory — UserCode
    @SerialName("priorityName") val priorityName: String? = null,
    @SerialName("statusName") val statusName: String? = null,
    @SerialName("leadName") val leadName: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("contactName") val contactName: String? = null,
    @SerialName("isActive") val isActive: Boolean = true,
)

@Serializable
data class TaskSortDto(
    @SerialName("field") val field: String = "dueDateTime",
    @SerialName("direction") val direction: String = "asc",
)