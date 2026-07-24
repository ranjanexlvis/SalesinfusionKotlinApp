package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class LoginResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: LoginDataDTO? = null,
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class LoginDataDTO(
    @SerialName("user") val user: UserDTO,
    @SerialName("token") val token: String,
)

@Serializable
data class UserDTO(
    @SerialName("fullName") val fullName: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("userName") val userName: String,
    @SerialName("roleName") val roleName: String,
    @SerialName("countryCode") val countryCode: String,
    @SerialName("currencyCode") val currencyCode: String,
    @SerialName("languageCode") val languageCode: String,
    @SerialName("activityList") val activityList: List<Int> = emptyList(),
)