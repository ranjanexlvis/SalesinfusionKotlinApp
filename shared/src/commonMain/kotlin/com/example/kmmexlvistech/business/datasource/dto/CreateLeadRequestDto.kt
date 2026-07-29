package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateLeadRequestDto(
    val lead: LeadPayloadDto
)

@Serializable
data class LeadPayloadDto(
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val mobile: String,
    val phone: String? = null,
    val leadCompanyName: String,
    val leadSourceId: Int,
    val leadStatusId: Int,
    val campaignId: Int? = null,
    val leadOwner: Int,
    val assignedTo: Int,
    val leadItems: List<LeadItemPayloadDto> = emptyList()
)

@Serializable
data class LeadItemPayloadDto(
    val catalogId: Int,
    val itemName: String,
    val itemTypeId: Int,
    val categoryId: Int,
    val quantity: Int,
    val unitPrice: Double,
    val expectedPrice: Double,
    val discount: Double,
    val taxId: Int,
    val uomId: Int,
    val serviceDuration: Int
)

@Serializable
data class CreateLeadResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: CreateLeadResponseData? = null
)

@Serializable
data class CreateLeadResponseData(
    val id: String = "",
    val leadId: Int = 0,
    val leadItems: List<CreatedLeadItemDto> = emptyList()
)

@Serializable
data class CreatedLeadItemDto(
    val id: String = "",
    val leadItemId: Int = 0
)