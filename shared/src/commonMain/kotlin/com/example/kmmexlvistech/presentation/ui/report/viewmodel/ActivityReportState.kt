package com.example.kmmexlvistech.presentation.ui.report.viewmodel

import com.example.kmmexlvistech.business.datasource.services.ActivityType
import com.example.kmmexlvistech.business.datasource.services.ActivityType.*
import com.example.kmmexlvistech.business.datasource.services.DashboardData
import com.example.kmmexlvistech.business.datasource.services.ReportFilters

data class ActivityReportState(
    val selected: ActivityType = TASK,
    val tabs: Map<ActivityType, TabState>
) {
    val current: TabState get() = tabs.getValue(selected)
}

data class TabState(
    val filters: ReportFilters,
    val data: DashboardData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)