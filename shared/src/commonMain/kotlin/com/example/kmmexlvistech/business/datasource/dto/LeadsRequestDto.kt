package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadsRequestDto(
    @SerialName("page") val page: Int = 1,
    @SerialName("pageSize") val pageSize: Int = 20,
    @SerialName("filters") val filters: LeadsFilterDto,
    @SerialName("sort") val sort: LeadsSortDto = LeadsSortDto(),
)

@Serializable
data class LeadsFilterDto(
    @SerialName("search") val search: String? = null,
    @SerialName("assignedTo") val assignedTo: Int,
    @SerialName("statusId") val statusId: Int? = null,

)

@Serializable
data class LeadsSortDto(
    @SerialName("field") val field: String = "modifieddatetime",
    @SerialName("direction") val direction: String = "desc",
)