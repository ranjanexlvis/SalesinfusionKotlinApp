package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskListResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<TaskDto> = emptyList(),
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("totalRecords") val totalRecords: Int = 0,
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class TaskDto(
    @SerialName("id") val id: String,
    @SerialName("taskId") val taskId: Int,
    @SerialName("subject") val subject: String,
    @SerialName("isActive") val isActive: Boolean = true,
    @SerialName("dueDateTime") val dueDateTime: String,
    @SerialName("lead") val lead: TaskLeadDto? = null,
    @SerialName("account") val account: TaskAccountDto? = null,
    @SerialName("contact") val contact: TaskContactDto? = null,
    @SerialName("status") val status: TaskStatusDto,
    @SerialName("priority") val priority: TaskPriorityDto,
    @SerialName("assignedTo") val assignedTo: TaskAssigneeDto,
)

@Serializable
data class TaskLeadDto(
    @SerialName("id") val id: String,
    @SerialName("leadId") val leadId: Int,
    @SerialName("leadName") val leadName: String,
)

@Serializable
data class TaskAccountDto(
    @SerialName("id") val id: String,
    @SerialName("accountName") val accountName: String? = null,
)

@Serializable
data class TaskContactDto(
    @SerialName("id") val id: String,
    @SerialName("contactName") val contactName: String? = null,
)

@Serializable
data class TaskStatusDto(
    @SerialName("id") val id: String,
    @SerialName("statusId") val statusId: Int,
    @SerialName("statusName") val statusName: String,
)

@Serializable
data class TaskPriorityDto(
    @SerialName("id") val id: String,
    @SerialName("priorityId") val priorityId: Int,
    @SerialName("priorityName") val priorityName: String,
)

@Serializable
data class TaskAssigneeDto(
    @SerialName("id") val id: String,
    @SerialName("userid") val userId: Int,
    @SerialName("fullName") val fullName: String,
)