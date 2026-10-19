// business/datasource/models/ProfileModels.kt
package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class ProfileResponse(
    val status: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val data: ProfileDto? = null,
    val errors: List<JsonElement> = emptyList(),
)

@Serializable data class RoleRefDto(val roleId: Int = 0, val roleName: String? = null)
@Serializable data class TimeZoneRefDto(val timeZoneId: Int = 0, val timeZoneName: String? = null, val utcOffset: String? = null)
@Serializable data class CurrencyRefDto(
    val currencyId: Int = 0, val currencyCode: String? = null,
    val currencyName: String? = null, val symbol: String? = null,
)
@Serializable data class LanguageRefDto(
    val languageCode: String? = null, val displayName: String? = null, val nativeName: String? = null,
)

@Serializable
data class EmployeeDetailsDto(
    val gender: String? = null,
    val dateOfBirth: String? = null,
    val dateOfJoining: String? = null,
    val employeeId: JsonElement? = null,
    val employmentCode: JsonElement? = null,
    // arrive as {} in your sample; parsed leniently (see displayName() below)
    val department: JsonObject? = null,
    val designation: JsonObject? = null,
    val reportingTo: JsonObject? = null,
    val employeeRole: JsonObject? = null,
    val employmentType: JsonObject? = null,
)

@Serializable
data class ProfileDto(
    val id: String = "",
    val userId: Int = 0,
    val userName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val emailId: String? = null,
    val mobileNumber: String? = null,
    val companyCode: Int = 0,
    val isUserActive: Boolean = true,
    val isEmailVerified: Boolean = false,
    val isMobileVerified: Boolean = false,
    val role: RoleRefDto? = null,
    val branch: JsonObject? = null,
    val timeZone: TimeZoneRefDto? = null,
    val preferredCurrency: CurrencyRefDto? = null,
    val preferredLanguage: LanguageRefDto? = null,
    val employeeDetails: EmployeeDetailsDto? = null,
    val activityIds: List<Int> = emptyList(),
    val createdDateTime: String? = null,
    val modifiedDateTime: String? = null,
) {
    val displayName: String
        get() = fullName?.takeIf { it.isNotBlank() }
            ?: listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "User" }

    val initials: String
        get() = "${firstName?.firstOrNull() ?: ""}${lastName?.firstOrNull() ?: ""}"
            .ifBlank { displayName.take(2) }.uppercase()
}

@Serializable
data class UpdateProfileRequestDto(
    val firstName: String,
    val lastName: String,
    val emailId: String,
    val mobileNumber: String,
    val preferredLanguage: String,     // language code, e.g. "en"
    val preferredCurrencyId: Int,
    val timeZoneId: Int,
)

@Serializable
data class UpdateProfileResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: JsonElement? = null,
    val errors: List<JsonElement> = emptyList(),
)

// ---- helpers ----

/** First name-like value of an object such as {"departmentId":1,"departmentName":"Sales"}. */
fun JsonObject?.displayName(): String? {
    if (this == null || isEmpty()) return null
    val key = listOf("name", "fullName", "displayName")
        .firstNotNullOfOrNull { k -> keys.firstOrNull { it.equals(k, true) } }
        ?: keys.firstOrNull {
            it.endsWith("Name", true) && !it.equals("firstName", true) && !it.equals("lastName", true)
        }
    return (key?.let { this[it] } as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }
}

fun JsonElement?.text(): String? =
    (this as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }

fun formatProfileDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    return try {
        val d = kotlinx.datetime.LocalDate.parse(raw.substringBefore("T"))
        "${d.dayOfMonth} ${d.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${d.year}"
    } catch (e: Exception) { raw }
}