package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuickActionResponse(
    @SerialName("status") val status: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: List<QuickActionDTO> = emptyList(),
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class QuickActionDTO(
    @SerialName("quickActionId") val quickActionId: Int,
    @SerialName("actionCode") val actionCode: String,
    @SerialName("actionName") val actionName: String,
    @SerialName("actionNameKey") val actionNameKey: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("iconType") val iconType: String? = null, // e.g. "material"
    @SerialName("color") val color: String? = null, // hex string, e.g. "#7C4DFF"
    @SerialName("moduleCode") val moduleCode: String? = null,
    @SerialName("routePath") val routePath: String? = null,
    @SerialName("apiEndpoint") val apiEndpoint: String? = null,
    @SerialName("displayOrder") val displayOrder: Int = 0,
    @SerialName("isSystem") val isSystem: Boolean = false,
    @SerialName("isVisible") val isVisible: Boolean = true,
    @SerialName("platform") val platform: String? = null, // "BOTH" / "ANDROID" / "IOS"
    @SerialName("activity") val activity: QuickActionActivityDTO,
)

@Serializable
data class QuickActionActivityDTO(
    @SerialName("activityId") val activityId: Int,
    @SerialName("activityName") val activityName: String,
)