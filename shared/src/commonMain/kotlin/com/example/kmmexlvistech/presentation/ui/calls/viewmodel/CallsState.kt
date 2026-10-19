package com.example.kmmexlvistech.presentation.ui.calls.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CallDto
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.models.ActivityCallData
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption

data class CallsState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val calls: List<CallDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedStatusName: String? = null,

    val subject: String = "",
    val description: String = "",
    val relatedRecordType: String = "LEAD",
    val leadSearchQuery: String = "",
    val leadSearchResults: List<LeadDto> = emptyList(),
    val selectedLead: LeadDto? = null,
    val priorityId: Int? = null,
    val statusId: Int? = null,
    val assignedToUserId: Int? = null,

    val startDateTimeMillis: Long? = null,
    val endDateTimeMillis: Long? = null,
    val reminderDateTimeMillis: Long? = null,

    val priorityOptions: List<DropdownOption> = emptyList(),
    val statusOptions: List<DropdownOption> = emptyList(),
    val userOptions: List<DropdownOption> = emptyList(),

    val isSavingCall: Boolean = false,
    val saveCallError: String? = null,
    val callSaved: Boolean = false,

    val selectedCallId: String? = null,
    val call: ActivityCallData? = null,
    val isLoadingDetails: Boolean = false,
    val detailsError: String? = null,

    val isDeletingCall: Boolean = false,
    val deleteCallError: String? = null,
    val callDeleted: Boolean = false,

    val editingCallId: String? = null,
    val editLeadId: String? = null,
) {
    val hasMore: Boolean get() = calls.size < totalRecords
    val isEditMode: Boolean get() = editingCallId != null
}