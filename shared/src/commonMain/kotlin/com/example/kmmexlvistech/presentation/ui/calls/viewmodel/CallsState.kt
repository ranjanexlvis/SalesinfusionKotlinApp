package com.example.kmmexlvistech.presentation.ui.calls.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CallDto

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
) {
    val hasMore: Boolean get() = calls.size < totalRecords
}