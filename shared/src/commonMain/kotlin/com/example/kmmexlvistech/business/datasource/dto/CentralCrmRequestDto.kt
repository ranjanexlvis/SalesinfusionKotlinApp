package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class CentralCrmRequestDto(
    val page: Int = 1,
    val pageSize: Int = 20,
    val filters: CentralCrmFilterDto = CentralCrmFilterDto(),
    val sort: CentralCrmSortDto
)

@Serializable
data class CentralCrmFilterDto(
    val search: String = "",
    val isActive: Boolean = true,
    val activityTypeId: Int? = null,
)

@Serializable
data class CentralCrmSortDto(
    val field: String,
    val direction: String = "asc"
)

@Serializable
data class LeadStatusesResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<LeadStatusOptionDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val errors: List<String> = emptyList()
)

@Serializable
data class LeadSourceOptionDto(
    val id: String = "",
    val leadSourceId: Int = 0,
    val sourceCode: String? = null,
    val sourceName: String = "",
    val description: String? = null,
    val isActive: Boolean = true,
    val isSystem: Boolean = false,
)

@Serializable
data class LeadStatusOptionDto(
    val id: String = "",
    val leadStatusId: Int = 0,
    val statusCode: String? = null,
    val statusName: String = "",
    val description: String? = null,
    val isActive: Boolean = true,
    val isSystem: Boolean = false,
    val isClosed: Boolean = false,
    val isFinal: Boolean = false,
)

@Serializable
data class LeadSourcesResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<LeadSourceOptionDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val errors: List<String> = emptyList()
)

