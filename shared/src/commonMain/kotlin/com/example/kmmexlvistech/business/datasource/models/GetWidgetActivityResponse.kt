package com.example.kmmexlvistech.business.datasource.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetWidgetActivityResponse(
    @SerialName("status") val status: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: List<WidgetDTO> = emptyList(),
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class WidgetDTO(
    @SerialName("widgetId") val widgetId: Int,
    @SerialName("widgetCode") val widgetCode: String,
    @SerialName("widgetName") val widgetName: String,
    @SerialName("widgetNameKey") val widgetNameKey: String? = null,
    @SerialName("widgetCategory") val widgetCategory: String? = null,
    @SerialName("moduleCode") val moduleCode: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("layoutType") val layoutType: String? = null,
    @SerialName("columnSpan") val columnSpan: Int = 1,
    @SerialName("rowSpan") val rowSpan: Int = 1,
    @SerialName("displayOrder") val displayOrder: Int = 0,
    @SerialName("isSystem") val isSystem: Boolean = false,
    @SerialName("isVisible") val isVisible: Boolean = true,
    @SerialName("platform") val platform: String? = null, // e.g. "BOTH", "ANDROID", "IOS"
    @SerialName("routePath") val routePath: String? = null,
    @SerialName("apiEndpoint") val apiEndpoint: String? = null,
    @SerialName("cacheDurationSeconds") val cacheDurationSeconds: Int = 0,
    @SerialName("refreshIntervalSeconds") val refreshIntervalSeconds: Int = 0,
    @SerialName("activities") val activities: List<WidgetActivityDTO> = emptyList(),
    @SerialName("children") val children: List<WidgetDTO> = emptyList(), // recursive — same type as parent
)

@Serializable
data class WidgetActivityDTO(
    @SerialName("activityId") val activityId: Int,
    @SerialName("activityName") val activityName: String,
)