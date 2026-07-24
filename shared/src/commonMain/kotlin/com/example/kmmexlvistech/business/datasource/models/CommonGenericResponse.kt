package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonGenericResponse(
    @SerialName("status") var status: String? = null,
    @SerialName("message") var message: String? = null,
    @SerialName("data") var data: String? = null,

)
