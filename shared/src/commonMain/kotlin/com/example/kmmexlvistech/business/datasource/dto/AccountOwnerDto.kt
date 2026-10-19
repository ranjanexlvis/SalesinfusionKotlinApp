@file:OptIn(ExperimentalSerializationApi::class)

package com.example.kmmexlvistech.business.datasource.dto


import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class AccountOwnerDto(
    val id: String? = null,
    @JsonNames("userid") val userId: Int = 0,
    @JsonNames("name") val fullName: String? = null,
)

@Serializable
data class AccountTypeRefDto(
    val id: String? = null,
    val accountTypeId: Int = 0,
    val typeName: String? = null,
)

@Serializable
data class IndustryRefDto(
    val id: String? = null,
    val industryId: Int = 0,
    val industryName: String? = null,
)

// Used for both list items and details. `id` is the UUID; accountId (numeric) is NOT used for navigation.
@Serializable
data class AccountDto(
    val id: String = "",
    val accountId: Int = 0,
    val accountCode: String? = null,
    val accountName: String? = null,
    val website: String? = null,
    val owner: AccountOwnerDto? = null,
    val industry: IndustryRefDto? = null,
    val accountType: AccountTypeRefDto? = null,
    @JsonNames("isactive") val isActive: Boolean = true,
    @JsonNames("createddatetime") val createdDateTime: String? = null,
    @JsonNames("modifieddatetime") val modifiedDateTime: String? = null,
)

@Serializable
data class AccountsListResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<AccountDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val error: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

@Serializable
data class AccountDetailResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: AccountDto? = null,
    val error: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

// create / update / delete: `data` is {id, accountId} or {}, so it is parsed leniently
@Serializable
data class AccountActionResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: JsonElement? = null,
    val error: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

@Serializable
data class AccountTypeLookupDto(val id: String? = null, val accountTypeId: Int = 0, val typeName: String? = null)
@Serializable
data class IndustryLookupDto(val id: String? = null, val industryId: Int = 0, val industryName: String? = null)

@Serializable
data class AccountTypesResponse(
    val status: Boolean = false, val message: String? = null,
    val data: List<AccountTypeLookupDto> = emptyList(), val totalRecords: Int = 0,
)
@Serializable
data class IndustriesResponse(
    val status: Boolean = false, val message: String? = null,
    val data: List<IndustryLookupDto> = emptyList(), val totalRecords: Int = 0,
)

// ---------- requests ----------
@Serializable
data class AccountFilterDto(
    val search: String, val accountTypeName: String, val industryName: String, val isActive: Boolean?,
)
@Serializable data class AccountSortDto(val field: String, val direction: String)
@Serializable
data class AccountsRequestDto(val page: Int, val pageSize: Int, val filters: AccountFilterDto, val sort: AccountSortDto)

@Serializable data class LookupFilterDto(val search: String, val isActive: Boolean)
@Serializable data class LookupSortDto(val field: String, val direction: String)
@Serializable
data class LookupRequestDto(val page: Int, val pageSize: Int, val filters: LookupFilterDto, val sort: LookupSortDto)

@Serializable
data class CreateAccountRequestDto(
    val accountCode: String, val accountName: String, val accountTypeId: Int,
    val industryId: Int, val website: String?, val ownerUserId: Int,
)
@Serializable
data class UpdateAccountRequestDto(      // no accountCode: your doc says not to send it
    val id: String, val accountName: String, val website: String?,
    val accountTypeId: Int, val industryId: Int, val ownerUserId: Int,
)