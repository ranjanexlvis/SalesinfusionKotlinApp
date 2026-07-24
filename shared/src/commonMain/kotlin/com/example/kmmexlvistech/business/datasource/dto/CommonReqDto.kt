package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonReqDto(
    @SerialName("userid")val Userid: String,
    @SerialName("login")val login: String,
    @SerialName("mobile")val mobile: String,
    @SerialName("hashcode")val hashkey: String,
    )