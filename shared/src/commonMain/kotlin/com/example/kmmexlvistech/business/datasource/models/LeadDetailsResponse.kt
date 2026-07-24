package com.example.kmmexlvistech.business.datasource.models


@kotlinx.serialization.Serializable
data class LeadDetailsResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: LeadDetailDto? = null,
    val errors: List<String> = emptyList()
)

@kotlinx.serialization.Serializable
data class LeadDetailDto(
    val id: String = "",
    val leadId: Int = 0,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val phone: String? = null,
    val isactive: Boolean = true,
    val leadCompanyName: String? = null,
    val leadOwner: Int = 0,
    val assignedTo: Int = 0,
    val campaignId: Int? = null,
    val isConverted: Boolean = false,
    val leadSourceId: Int = 0,
    val leadStatusId: Int = 0,
    val createddatetime: String? = null,
    val modifieddatetime: String? = null,
    val leadItems: List<LeadItemDto> = emptyList(),
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "Unknown" }
}

@kotlinx.serialization.Serializable
data class LeadItemDto(
    val id: String = "",
    val leadItemId: Int = 0,
    val itemName: String? = null,
    val catalogId: Int = 0,
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val expectedPrice: Double = 0.0,
    val taxId: Int = 0,
    val uomId: Int = 0,
    val serviceDuration: Int = 0,
)