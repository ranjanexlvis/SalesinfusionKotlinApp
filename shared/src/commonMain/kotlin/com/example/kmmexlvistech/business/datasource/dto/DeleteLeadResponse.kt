package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteLeadResponse(
    val status: Boolean = false,
    val message: String? = null,
)