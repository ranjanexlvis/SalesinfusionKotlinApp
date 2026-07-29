package com.example.kmmexlvistech.business.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteTaskResponse(
    val status: Boolean = false,
    val message: String? = null,
)