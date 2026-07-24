package com.example.kmmexlvistech.presentation.ui.meetings.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.MeetingDto

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
) {
    val hasMore: Boolean get() = meetings.size < totalRecords
}