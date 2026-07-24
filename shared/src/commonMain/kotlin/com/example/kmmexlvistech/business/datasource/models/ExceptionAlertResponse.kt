package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ExceptionAlertResponse(
    @SerialName("title") var title: String = "",
    @SerialName("message") var message: String = ""
)