package com.example.kmmexlvistech.presentation.ui.meetings.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingDto
import com.example.kmmexlvistech.business.datasource.models.ActivityMeetingData
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption

data class MeetingsState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val meetings: List<MeetingDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedStatusName: String? = null,

    val selectedMeetingId: String? = null,
    val meeting: ActivityMeetingData? = null,
    val isLoadingDetails: Boolean = false,
    val detailsError: String? = null,

    val subject: String = "",
    val description: String = "",
    val relatedRecordType: String = "LEAD",
    val leadSearchQuery: String = "",
    val leadSearchResults: List<LeadDto> = emptyList(),
    val selectedLead: LeadDto? = null,
    val meetingType: String = "OFFLINE",
    val location: String = "",
    val meetingLink: String = "",
    val priorityId: Int? = null,
    val statusId: Int? = null,
    val assignedToUserId: Int? = null,

    val startDateTimeMillis: Long? = null,
    val endDateTimeMillis: Long? = null,
    val reminderDateTimeMillis: Long? = null,

    val priorityOptions: List<DropdownOption> = emptyList(),
    val statusOptions: List<DropdownOption> = emptyList(),
    val userOptions: List<DropdownOption> = emptyList(),

    val isSavingMeeting: Boolean = false,
    val saveMeetingError: String? = null,
    val meetingSaved: Boolean = false,

    val isDeletingMeeting: Boolean = false,
    val deleteMeetingError: String? = null,
    val meetingDeleted: Boolean = false,


    ) {
    val hasMore: Boolean get() = meetings.size < totalRecords
}