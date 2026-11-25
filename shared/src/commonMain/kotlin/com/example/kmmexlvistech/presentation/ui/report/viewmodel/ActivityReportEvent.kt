package com.example.kmmexlvistech.presentation.ui.report.viewmodel

import com.example.kmmexlvistech.business.datasource.services.ActivityType
import com.example.kmmexlvistech.business.datasource.services.ReportFilters

sealed interface ActivityReportEvent {
    data class Load(val token: String) : ActivityReportEvent
    data class SelectTab(val type: ActivityType, val token: String) : ActivityReportEvent
    data class ApplyFilters(val filters: ReportFilters, val token: String) : ActivityReportEvent
    data object ErrorShown : ActivityReportEvent
}