package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLeadRequestDto(
    val id: String,
    val lead: UpdateLeadPayloadDto
)

@Serializable
data class UpdateLeadPayloadDto(
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
    val leadItems: List<UpdateLeadItemPayloadDto> = emptyList()
)

@Serializable
data class UpdateLeadItemPayloadDto(
    val id: String? = null,           // present when updating/deleting an existing item; absent for new items
    val catalogId: Int? = null,
    val itemName: String? = null,
    val itemTypeId: Int? = null,
    val categoryId: Int? = null,
    val quantity: Int? = null,
    val unitPrice: Double? = null,
    val expectedPrice: Double? = null,
    val discount: Double? = null,
    val taxId: Int? = null,
    val uomId: Int? = null,
    val serviceDuration: Int? = null,
    val isDeleted: Boolean? = null,   // set true to remove an existing item
)

@Serializable
data class UpdateLeadResponse(
    val status: Boolean = false,
    val message: String? = null,
)