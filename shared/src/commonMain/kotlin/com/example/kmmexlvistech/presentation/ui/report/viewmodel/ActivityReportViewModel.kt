package com.example.kmmexlvistech.presentation.ui.report.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.kmmexlvistech.business.datasource.services.ActivityReportService
import com.example.kmmexlvistech.business.datasource.services.ActivityType
import com.example.kmmexlvistech.business.datasource.services.DashboardReq
import com.example.kmmexlvistech.business.datasource.services.ReportFilters
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.collections.toMutableMap


class ActivityReportViewModel(private val service: ActivityReportService) : ViewModel() {

    // Default range: edit to your preferred default (e.g. first of month → today)
    private fun defaultFilters() = ReportFilters(fromDate = "2026-10-01", toDate = "2026-10-17")

    var state by mutableStateOf(
        ActivityReportState(tabs = ActivityType.entries.associateWith { TabState(defaultFilters()) })
    )
        private set

    fun onEvent(e: ActivityReportEvent) {
        when (e) {
            is ActivityReportEvent.Load -> fetch(state.selected, e.token)
            is ActivityReportEvent.SelectTab -> {
                state = state.copy(selected = e.type)
                if (state.current.data == null && !state.current.isLoading) fetch(e.type, e.token)
            }
            is ActivityReportEvent.ApplyFilters -> {
                update(state.selected) { it.copy(filters = e.filters) }
                fetch(state.selected, e.token)
            }
            ActivityReportEvent.ErrorShown -> update(state.selected) { it.copy(error = null) }
        }
    }

    private fun update(type: ActivityType, block: (TabState) -> TabState) {
        state = state.copy(tabs = state.tabs.toMutableMap().also { it[type] = block(it.getValue(type)) })
    }

    private fun fetch(type: ActivityType, token: String) {
        if (token.isBlank()) return
        update(type) { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val resp = service.dashboard(type, token,
                    DashboardReq(filters = state.tabs.getValue(type).filters)
                )
                if (resp.status && resp.data != null) {
                    update(type) { it.copy(isLoading = false, data = resp.data) }
                } else {
                    update(type) { it.copy(isLoading = false, error = resp.error ?: resp.message ?: "Something went wrong") }
                }
            } catch (t: Throwable) {
                update(type) { it.copy(isLoading = false, error = t.message ?: "Network error") }
            }
        }
    }
}