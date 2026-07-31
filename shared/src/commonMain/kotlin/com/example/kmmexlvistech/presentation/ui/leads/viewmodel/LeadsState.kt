package com.example.kmmexlvistech.presentation.ui.leads.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.models.LeadDetailDto
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption

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

    val firstName: String = "",
    val lastName: String = "",
    val companyName: String = "",
    val countryCode: String = "+91",
    val mobile: String = "",
    val phone: String = "",
    val email: String = "",
    val leadSourceId: Int? = null,
    val leadStatusId: Int? = null,
    val leadOwnerId: Int? = null,
    val assignedToId: Int? = null,
    val campaignId: Int? = null,
    val isActive: Boolean = true,
    val isConverted: Boolean = false,
    val isSavingLead: Boolean = false,
    val saveLeadError: String? = null,
    val leadSaved: Boolean = false,

    val leadStatusOptions: List<DropdownOption> = emptyList(),
    val leadSourceOptions: List<DropdownOption> = emptyList(),
    val isLoadingLookups: Boolean = false,


    val isDeletingLead: Boolean = false,
    val deleteLeadError: String? = null,
    val leadDeleted: Boolean = false,

    val isEditMode: Boolean = false,
    val editingLeadId: String? = null,   // GUID being edited
    val isUpdatingLead: Boolean = false,
    val updateLeadError: String? = null,
    val leadUpdated: Boolean = false,


    val editingLeadDisplayId: String? = null,
    val editingLeadCreatedOn: String? = null,
    val editingLeadLastModified: String? = null,
    val editingLeadLastModifiedBy: String? = null,

) {

    val hasMore: Boolean get() = leads.size < totalRecords





}