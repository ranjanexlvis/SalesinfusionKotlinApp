package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.Serializable

@Serializable
data class LeadDetailsResponse(
    val status: Boolean = false,
    val message: String? = null,
    val error: String? = null,          // API sends "error": "" (a string)
    val data: LeadDetailDto? = null,
)

@Serializable
data class UserRefDto(
    val id: String = "",
    val code: String? = null,
    val name: String = "",
    val userId: Int = 0,
)

@Serializable
data class LookupRefDto(
    val id: Int = 0,
    val code: String? = null,
    val name: String = "",
)

@Serializable
data class CampaignRefDto(          // "campaign" was null in your sample; adjust once you see a real one
    val id: Int? = null,
    val code: String? = null,
    val name: String? = null,
)

@Serializable
data class LeadDetailDto(
    val id: String = "",
    val leadId: Int = 0,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val isActive: Boolean = true,
    val isConverted: Boolean = false,
    val leadCompanyName: String? = null,
    val leadOwner: UserRefDto? = null,
    val assignedTo: UserRefDto? = null,
    val campaign: CampaignRefDto? = null,
    val leadSource: LookupRefDto? = null,
    val leadStatus: LookupRefDto? = null,
    val createdDateTime: String? = null,
    val modifiedDateTime: String? = null,
    val leadItems: List<LeadItemDto> = emptyList(),
) {
    // The API also sends "fullName"; it's rebuilt here (needs ignoreUnknownKeys = true, see notes)
    val fullName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "Unknown" }

    // Compatibility aliases for existing code (edit form etc.)
    val isactive: Boolean get() = isActive
    val leadOwnerId: Int get() = leadOwner?.userId ?: 0
    val assignedToId: Int get() = assignedTo?.userId ?: 0
    val campaignId: Int? get() = campaign?.id
    val leadSourceId: Int get() = leadSource?.id ?: 0
    val leadStatusId: Int get() = leadStatus?.id ?: 0
    val createddatetime: String? get() = createdDateTime
    val modifieddatetime: String? get() = modifiedDateTime
}

@Serializable data class TaxRefDto(val taxId: Int = 0, val code: String? = null, val name: String? = null)
@Serializable data class UomRefDto(val uomId: Int = 0, val code: String? = null, val name: String? = null)
@Serializable data class CategoryRefDto(val categoryId: Int = 0, val code: String? = null, val name: String? = null)
@Serializable data class ItemTypeRefDto(val itemTypeId: Int = 0, val code: String? = null, val name: String? = null)
@Serializable
data class ItemDetailsRefDto(
    val id: String = "",
    val code: String? = null,
    val name: String? = null,
    val catalogId: Int = 0,
)

@Serializable
data class LeadItemDto(
    val id: String = "",
    val leadItemId: Int = 0,
    val tax: TaxRefDto? = null,
    val uom: UomRefDto? = null,
    val category: CategoryRefDto? = null,
    val itemType: ItemTypeRefDto? = null,
    val itemDetails: ItemDetailsRefDto? = null,
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val expectedPrice: Double = 0.0,
    val serviceDuration: Int = 0,
) {
    val itemName: String? get() = itemDetails?.name
    val catalogId: Int get() = itemDetails?.catalogId ?: 0
    val taxId: Int get() = tax?.taxId ?: 0
    val uomId: Int get() = uom?.uomId ?: 0
}