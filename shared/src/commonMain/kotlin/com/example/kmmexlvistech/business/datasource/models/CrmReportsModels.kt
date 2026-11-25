package com.example.kmmexlvistech.business.datasource.models



import com.example.kmmexlvistech.business.datasource.services.ChartItemDto
import com.example.kmmexlvistech.business.datasource.services.IdLabel
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/* ───────────── Request DTOs (no defaults on purpose, so every field is always sent) ───────────── */

@Serializable
data class CrmSortDto(
    val field: String,
    val direction: String
)

@Serializable
data class LeadFilterDto(
    val search: String,
    val fromDate: String,
    val toDate: String,
    val leadStatusIds: List<Int>,
    val leadSourceIds: List<Int>,
    val ownerIds: List<Int>,
    val campaignIds: List<Int>,
    @SerialName("lead_item_ids") val leadItemIds: List<Int>
)

@Serializable
data class AccountFilterDto(
    val search: String,
    val fromDate: String,
    val toDate: String,
    val ownerIds: List<Int>,
    val industryIds: List<Int>,
    val accountTypeIds: List<Int>
)

/** Used by POST /api/CrmReport/Lead/Dashboard and POST /api/CrmReport/Lead */
@Serializable
data class LeadReportReq(
    val page: Int,
    val pageSize: Int,
    val filters: LeadFilterDto,
    val sort: CrmSortDto
)

/** Used by POST /api/CrmReport/Account/Dashboard and POST /api/CrmReport/Account */
@Serializable
data class AccountReportReq(
    val page: Int,
    val pageSize: Int,
    val filters: AccountFilterDto,
    val sort: CrmSortDto
)

/* ───────────── Response DTOs ───────────── */

@Serializable
data class CrmApiResp<T>(
    val status: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val data: T? = null
)

@Serializable
data class CrmKpiDto(
    val id: Int? = null,          // API sends "ID" – needs ignoreUnknownKeys (unused in the UI anyway)
    val code: String = "",
    val count: Int = 0,
    val label: String = "",
    val percentage: Double? = null
)

@Serializable
data class LeadChartsDto(
    val bySource: List<ChartItemDto> = emptyList(),
    val byStatus: List<ChartItemDto> = emptyList()
)

@Serializable
data class AccountChartsDto(
    val byIndustry: List<ChartItemDto> = emptyList(),
    val byAccountType: List<ChartItemDto> = emptyList()
)

@Serializable
data class LeadDashboardData(
    val totalLeads: Int = 0,
    val kpis: List<CrmKpiDto> = emptyList(),
    val charts: LeadChartsDto = LeadChartsDto()
)

@Serializable
data class AccountDashboardData(
    val totalAccounts: Int = 0,
    val kpis: List<CrmKpiDto> = emptyList(),
    val charts: AccountChartsDto = AccountChartsDto()
)

/** Paginated list responses (Lead / Account report). Record shape unknown, so kept as raw JSON for now. */
@Serializable
data class CrmListResp(
    val status: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val totalPages: Int = 0,
    val data: List<JsonObject> = emptyList()
)

/* ───────────── UI models ───────────── */

enum class CrmTab(
    val tabLabel: String,
    val summaryTitle: String,
    val filterTitle: String,
    val searchHint: String
) {
    LEAD("Lead Report", "Lead Summary", "Lead Report Filters", "Search leads..."),
    ACCOUNT("Account Report", "Account Summary", "Account Report Filters", "Search accounts...")
}

/** One editable filter model for both tabs; each tab only uses its own fields. */
data class CrmFilters(
    val search: String = "",
    val fromDate: String,
    val toDate: String,
    val leadStatusIds: List<Int> = emptyList(),
    val leadSourceIds: List<Int> = emptyList(),
    val ownerIds: List<Int> = emptyList(),
    val campaignIds: List<Int> = emptyList(),
    val productIds: List<Int> = emptyList(),
    val industryIds: List<Int> = emptyList(),
    val accountTypeIds: List<Int> = emptyList()
) {
    fun activeCount(tab: CrmTab): Int {
        val lists = when (tab) {
            CrmTab.LEAD -> listOf(leadStatusIds, leadSourceIds, ownerIds, campaignIds, productIds)
            CrmTab.ACCOUNT -> listOf(ownerIds, industryIds, accountTypeIds)
        }
        return lists.count { it.isNotEmpty() } + if (search.isNotBlank()) 1 else 0
    }

    fun toLeadReq(page: Int = 1, pageSize: Int = 20) = LeadReportReq(
        page = page,
        pageSize = pageSize,
        filters = LeadFilterDto(
            search = search, fromDate = fromDate, toDate = toDate,
            leadStatusIds = leadStatusIds, leadSourceIds = leadSourceIds,
            ownerIds = ownerIds, campaignIds = campaignIds, leadItemIds = productIds
        ),
        sort = CrmSortDto("created_datetime", "DESC")
    )

    fun toAccountReq(page: Int = 1, pageSize: Int = 20) = AccountReportReq(
        page = page,
        pageSize = pageSize,
        filters = AccountFilterDto(
            search = search, fromDate = fromDate, toDate = toDate,
            ownerIds = ownerIds, industryIds = industryIds, accountTypeIds = accountTypeIds
        ),
        sort = CrmSortDto("created_datetime", "DESC")
    )
}

fun defaultCrmFilters(): CrmFilters {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return CrmFilters(
        fromDate = today.minus(DatePeriod(months = 3)).toString(),
        toDate = today.toString()
    )
}

/** Dropdown data for the filter sheet. Fill from your existing master-data APIs. */
data class CrmFilterLookups(
    val leadStatuses: List<IdLabel> = emptyList(),
    val leadSources: List<IdLabel> = emptyList(),
    val owners: List<IdLabel> = emptyList(),
    val campaigns: List<IdLabel> = emptyList(),
    val products: List<IdLabel> = emptyList(),
    val industries: List<IdLabel> = emptyList(),
    val accountTypes: List<IdLabel> = emptyList()
)