package com.example.kmmexlvistech.business.datasource.dto


import com.example.kmmexlvistech.business.datasource.dto.CentralCrmSortDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class LeadSearchRequestDto(
    val page: Int = 1,
    val pageSize: Int = 20,
    val filters: LeadSearchFilterDto,
    val sort: CentralCrmSortDto = CentralCrmSortDto(field = "firstName"),
)

@Serializable
data class LeadSearchFilterDto(
    val search: String = "",
)