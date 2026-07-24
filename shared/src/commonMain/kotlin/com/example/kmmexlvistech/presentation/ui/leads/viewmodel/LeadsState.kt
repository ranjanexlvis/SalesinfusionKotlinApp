package com.example.kmmexlvistech.presentation.ui.leads.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.models.LeadDetailDto
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse

data class LeadsState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val leads: List<LeadDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedStatusId: Int? = null,

    // --- Lead details ---
    val isLoadingDetails: Boolean = false,
    val lead: LeadDetailDto? = null,
    val detailsError: String? = null,
    val selectedLeadId: String? = null,
) {
    // True once every available page has been fetched — used to stop
    // triggering further pagination calls at list-end.
    val hasMore: Boolean get() = leads.size < totalRecords



}