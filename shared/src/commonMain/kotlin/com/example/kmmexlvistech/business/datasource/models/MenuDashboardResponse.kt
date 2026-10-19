package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDashboardResponse(
    @SerialName("status")
    val status: Boolean = false,

    @SerialName("message")
    val message: String? = null,

    @SerialName("error")
    val error: String? = null,

    @SerialName("data")
    val data: List<MenuItemDTO> = emptyList(),

    @SerialName("errors")
    val errors: List<String> = emptyList()
)

@Serializable
data class MenuItemDTO(
    @SerialName("menuId")
    val menuId: Int = 0,

    @SerialName("menuCode")
    val menuCode: String = "",

    @SerialName("menuName")
    val menuName: String = "",

    @SerialName("menuNameKey")
    val menuNameKey: String? = null,

    // "ADMIN", "PAGE", "GROUP", "PROFILE"
    @SerialName("menuType")
    val menuType: String = "",

    @SerialName("icon")
    val icon: String? = null,

    // null for group menus
    @SerialName("routePath")
    val routePath: String? = null,

    @SerialName("is_system")
    val isSystem: Boolean = false,

    @SerialName("is_visible")
    val isVisible: Boolean = true,

    @SerialName("is_mobile_visible")
    val isMobileVisible: Boolean = true,

    @SerialName("activities")
    val activities: List<MenuActivityDTO> = emptyList(),

    @SerialName("children")
    val children: List<MenuItemDTO> = emptyList()
) {
    // Computed, so they are not serialized
    val hasChildren: Boolean get() = children.isNotEmpty()
    val showOnMobile: Boolean get() =  isMobileVisible
}

@Serializable
data class MenuActivityDTO(
    @SerialName("activityId")
    val activityId: Int = 0,

    @SerialName("activityName")
    val activityName: String = ""
)