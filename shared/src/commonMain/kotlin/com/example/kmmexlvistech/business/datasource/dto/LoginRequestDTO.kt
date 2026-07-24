package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class TokenRequestDTO(
    @SerialName("userName")val userName: String,
    @SerialName("password")val password: String,

    )

@Serializable
data class LoginHeaderRequestDTO(
    @SerialName("action")val action: String,
)
@Serializable
data class VerifyOtpDTO(
    @SerialName("otp")val otp: String,
    @SerialName("token")val token: String,
    @SerialName("deviceID")val deviceID: String,
    @SerialName("deviceVersion")val deviceVersion: String,
    @SerialName("deviceType")val deviceType: String,
    @SerialName("mobno")val mobno: String,
    @SerialName("version")val version: String,
)

@Serializable
data class VerifyHeaderRequestDTO(
    @SerialName("mobNo")val mobNo: String,
    @SerialName("action")val action: String,
)

@Serializable
data class LoginWithPassRequestDTO(
    @SerialName("password")val password: String,
    @SerialName("token")val token: String,
    @SerialName("deviceID")val deviceID: String,
    @SerialName("deviceType")val deviceType: String,
    @SerialName("deviceVersion")val deviceVersion: String,
    @SerialName("iminum")val imiNum: String,
)






@Serializable
data class DeviceInfoDTO(
    @SerialName("deviceId") val deviceId: String,
    @SerialName("deviceName") val deviceName: String,
    @SerialName("devicemodel") val devicemodel: String,
    @SerialName("osvesion") val osvesion: String, // kept as-is to match backend's existing typo
    @SerialName("appversion") val appversion: String,
    @SerialName("macaddress") val macaddress: String,
)

@Serializable
data class GeoInfoDTO(
    @SerialName("publicIp") val publicIp: String,
    @SerialName("timezoneId") val timezoneId: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("country") val country: String,
)

@Serializable
data class LoginRequestDTO(
    @SerialName("userName") val userName: String,
    @SerialName("password") val password: String,
    @SerialName("device") val device: DeviceInfoDTO,
    @SerialName("geo") val geo: GeoInfoDTO,
)

