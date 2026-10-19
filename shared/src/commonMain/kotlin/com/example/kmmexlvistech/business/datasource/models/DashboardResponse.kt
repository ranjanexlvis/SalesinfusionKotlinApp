package com.example.kmmexlvistech.business.datasource.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    @SerialName("data")
    val data: DashboardDataDTO? = null,

    @SerialName("status")
    val status: Boolean = false,

    @SerialName("message")
    val message: String? = null
)

@Serializable
data class DashboardDataDTO(
    @SerialName("header")
    val header: DashboardHeaderDTO? = null,

    @SerialName("myWork")
    val myWork: MyWorkDTO? = null,

    @SerialName("todaysFocus")
    val todaysFocus: TodaysFocusDTO? = null
)

@Serializable
data class DashboardHeaderDTO(
    @SerialName("greeting")
    val greeting: String? = null,

    @SerialName("subTitle")
    val subTitle: String? = null,

    @SerialName("firstName")
    val firstName: String? = null,

    @SerialName("displayName")
    val displayName: String? = null,

    @SerialName("greetingKey")
    val greetingKey: String? = null,

    @SerialName("subTitleKey")
    val subTitleKey: String? = null
)

@Serializable
data class MyWorkDTO(
    @SerialName("calls")
    val calls: Int = 0,

    @SerialName("leads")
    val leads: Int = 0,

    @SerialName("tasks")
    val tasks: Int = 0,

    @SerialName("meetings")
    val meetings: Int = 0
)

@Serializable
data class TodaysFocusDTO(
    @SerialName("dueTasks")
    val dueTasks: Int = 0,

    @SerialName("followUps")
    val followUps: Int = 0,

    @SerialName("pendingCalls")
    val pendingCalls: Int = 0,

    @SerialName("meetingsToday")
    val meetingsToday: Int = 0
)