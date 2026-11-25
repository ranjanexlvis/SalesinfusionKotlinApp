@file:OptIn(ExperimentalSerializationApi::class)

package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class ContactAccountRefDto(
    val id: String? = null,              // account UUID
    val accountId: Int = 0,
    val accountCode: String? = null,
    val accountName: String? = null,
)

@Serializable
data class ContactRoleRefDto(
    val id: String? = null,
    val contactRoleId: Int = 0,
    val roleName: String? = null,
)

// `id` (UUID) is the external identifier for navigation, update and delete.
@Serializable
data class ContactDto(
    val id: String = "",
    val contactId: Int = 0,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val phone: String? = null,
    val account: ContactAccountRefDto? = null,
    val contactRole: ContactRoleRefDto? = null,
    val accountName: String? = null,     // flat variants, in case the API sends them
    val roleName: String? = null,
    @JsonNames("isactive") val isActive: Boolean = true,
    val isPrimaryContact: Boolean = false,
    @JsonNames("createddatetime") val createdDateTime: String? = null,
    @JsonNames("modifieddatetime") val modifiedDateTime: String? = null,
) {
    val displayName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "Unknown" }
    val initials: String
        get() = "${firstName?.firstOrNull() ?: ""}${lastName?.firstOrNull() ?: ""}"
            .ifBlank { displayName.take(2) }.uppercase()
    val accountLabel: String? get() = (account?.accountName ?: accountName)?.takeIf { it.isNotBlank() }
    val roleLabel: String? get() = (contactRole?.roleName ?: roleName)?.takeIf { it.isNotBlank() }
}

@Serializable
data class ContactsListResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<ContactDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val error: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

@Serializable
data class ContactDetailResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: ContactDto? = null,
    val error: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

// create / update / delete: `data` is {id, contactId}, {id} or {}, so it is parsed leniently
@Serializable
data class ContactActionResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: JsonElement? = null,
    val error: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

@Serializable
data class ContactRoleLookupDto(val id: String? = null, val contactRoleId: Int = 0,val description: String? = null, val roleName: String? = null)

@Serializable
data class ContactRolesResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<ContactRoleLookupDto> = emptyList(),
    val totalRecords: Int = 0,
)

// ---------- requests (every field passed explicitly, so nothing depends on encodeDefaults) ----------
@Serializable
data class ContactFilterDto(val search: String, val accountName: String, val isActive: Boolean?)
@Serializable data class ContactSortDto(val field: String, val direction: String)
@Serializable
data class ContactsRequestDto(
    val page: Int, val pageSize: Int, val filters: ContactFilterDto, val sort: ContactSortDto,
)

@Serializable
data class CreateContactRequestDto(
    val accountId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobile: String,
    val phone: String?,
    val contactRoleId: Int,

    val isPrimaryContact: Boolean,
)

@Serializable
data class UpdateContactRequestDto(      // per your doc, Update has no phone field
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobile: String,
    val contactRoleId: Int,
    val isPrimaryContact: Boolean,
    val accountId: String,
)