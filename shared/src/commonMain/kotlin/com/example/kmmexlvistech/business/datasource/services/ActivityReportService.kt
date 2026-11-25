package com.example.kmmexlvistech.business.datasource.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

data class IdLabel(val id: Int, val label: String)

@Serializable
data class KpiDto(
    val code: String = "",
    val count: Int = 0,
    val label: String = "",
    val percentage: Double? = null
)

// NOTE: chart item shape is assumed – adjust field names to the real response.
@Serializable
data class ChartItemDto(
    val id: Int? = null,
    val code: String? = null,
    val label: String = "",
    val count: Int = 0,
    val percentage: Double? = null
)
@Serializable
data class ChartsDto(
    val byStatus: List<ChartItemDto> = emptyList(),
    val byPriority: List<ChartItemDto> = emptyList()
)

@Serializable
data class DashboardData(
    val totalTasks: Int? = null,
    val totalCalls: Int? = null,
    val totalMeetings: Int? = null,
    val kpis: List<KpiDto> = emptyList(),
    val charts: ChartsDto = ChartsDto()
) {
    val total: Int get() = totalTasks ?: totalCalls ?: totalMeetings ?: 0
}
@Serializable
data class DashboardResp(
    val status: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val data: DashboardData? = null
)

@Serializable
data class ReportFilters(
    val search: String = "",
    val fromDate: String,
    val toDate: String,
    val statusIds: List<Int> = emptyList(),
    val priorityIds: List<Int> = emptyList(),
    val assignedToIds: List<Int> = emptyList(),
    val leadIds: List<Int> = emptyList(),
    val accountIds: List<Int> = emptyList(),
    val contactIds: List<Int> = emptyList()
) {
    /** Number shown on the "Filters (n)" chip / Apply button. */
    val activeCount: Int
        get() = listOf(
            statusIds, priorityIds, assignedToIds, leadIds, accountIds, contactIds
        ).count { it.isNotEmpty() } + if (search.isNotBlank()) 1 else 0
}

@Serializable
data class SortDto(val field: String = "createddatetime", val direction: String = "DESC")

@Serializable
data class DashboardReq(
    val page: Int = 1,
    val pageSize: Int = 20,
    val filters: ReportFilters,
    val sort: SortDto = SortDto()
)

enum class ActivityType(
    val tabLabel: String,
    val summaryTitle: String,
    val path: String,
    val totalLabel: String,
    val centerLabel: String,
    val searchHint: String,
    val filterTitle: String
) {
    TASK("Task", "Task Summary", "ActivityReport/Task/Dashboard", "Total Tasks", "Tasks", "Search tasks...", "Task Report Filters"),
    CALL("Call", "Call Summary", "ActivityReport/Call/Dashboard", "Total Calls", "Calls", "Search calls...", "Call Report Filters"),
    MEETING("Meeting", "Meeting Summary", "ActivityReport/Meeting/Dashboard", "Total Meetings", "Meetings", "Search meetings...", "Meeting Report Filters");

    /** TODO: replace with the real status ids from your master-data API. */
    val statusOptions: List<IdLabel>
        get() = when (this) {
            TASK -> listOf(IdLabel(1, "Pending"), IdLabel(2, "In Progress"), IdLabel(3, "Completed"), IdLabel(4, "Cancelled"))
            CALL -> listOf(IdLabel(1, "Scheduled"), IdLabel(2, "Completed"), IdLabel(3, "No Answer"), IdLabel(4, "Cancelled"))
            MEETING -> listOf(IdLabel(1, "Scheduled"), IdLabel(2, "Completed"), IdLabel(3, "Rescheduled"), IdLabel(4, "Cancelled"))
        }
}

val PriorityOptions = listOf(IdLabel(1, "Low"), IdLabel(2, "Medium"), IdLabel(3, "High"), IdLabel(4, "Urgent"))

/** Dropdown data for Assigned To / Lead / Account / Contact. Fill from your existing list APIs. */
data class FilterLookups(
    val users: List<IdLabel> = emptyList(),
    val leads: List<IdLabel> = emptyList(),
    val accounts: List<IdLabel> = emptyList(),
    val contacts: List<IdLabel> = emptyList()
)
class ActivityReportService(private val client: HttpClient, private val baseUrl: String) {
    suspend fun dashboard(type: ActivityType, token: String, req: DashboardReq): DashboardResp =
        client.post("$baseUrl${type.path}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()
}