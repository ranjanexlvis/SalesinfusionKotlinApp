package com.example.kmmexlvistech.presentation.ui.tasks.viewmodel


import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.dto.TaskDto
import com.example.kmmexlvistech.business.datasource.models.ActivityTaskData
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption

data class TaskState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val tasks: List<TaskDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedPriorityName: String? = null,

    // --- Task details ---
    val selectedTaskId: String? = null,
    val isLoadingDetails: Boolean = false,
    val task: ActivityTaskData? = null,
    val detailsError: String? = null,
    val startDate: String = "",
    val startTime: String = "10:00 AM",
    val endDate: String = "",
    val endTime: String = "11:00 AM",
    val dueDate: String = "",
    val dueTime: String = "06:00 PM",
    val reminderDate: String = "",
    val reminderTime: String = "09:30 AM",
    val subject: String = "",
    val description: String = "",
    val relatedRecordType: String = "LEAD", // "LEAD" | "ACCOUNT" | "CONTACT"
    val leadSearchQuery: String = "",
    val leadSearchResults: List<LeadDto> = emptyList(),
    val selectedLead: LeadDto? = null,
    val priorityId: Int? = null,
    val statusId: Int? = null,
    val assignedToUserId: Int? = null,
    val priorityOptions: List<DropdownOption> = emptyList(),
    val statusOptions: List<DropdownOption> = emptyList(),
    val userOptions: List<DropdownOption> = emptyList(),

    val isSavingTask: Boolean = false,
    val saveTaskError: String? = null,
    val taskSaved: Boolean = false,
    val isLoadingLookups: Boolean = false,

    val isDeletingTask: Boolean = false,
    val deleteTaskError: String? = null,
    val taskDeleted: Boolean = false,
) {
    val hasMore: Boolean get() = tasks.size < totalRecords
}