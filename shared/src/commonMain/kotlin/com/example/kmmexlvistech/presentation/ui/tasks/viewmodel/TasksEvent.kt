package com.example.kmmexlvistech.presentation.ui.tasks.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto

sealed class TasksEvent {
    data class GetTasks(val token: String, val userCode: Int) : TasksEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : TasksEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : TasksEvent()
    data class PriorityFilterChanged(val priorityName: String?, val token: String, val userCode: Int) : TasksEvent()
    object OnRemoveHeadFromQueue : TasksEvent()
    data class Error(val uiComponent: UIComponent) : TasksEvent()

    data class SelectTask(val taskId: String) : TasksEvent()
    data class LoadTaskDetails(val token: String, val taskId: String) : TasksEvent()

    data class SubjectChanged(val value: String) : TasksEvent()
    data class DescriptionChanged(val value: String) : TasksEvent()
    data class RelatedRecordTypeChanged(val type: String) : TasksEvent()
    data class LeadSearchQueryChanged(val query: String, val token: String) : TasksEvent()
    data class LeadSelected(val lead: LeadDto) : TasksEvent()
    data class TaskPriorityChanged(val id: Int) : TasksEvent()
    data class TaskStatusChanged(val id: Int) : TasksEvent()
    data class TaskAssignedToChanged(val userId: Int) : TasksEvent()
    data class StartDateChanged(val value: String) : TasksEvent()
    data class StartTimeChanged(val value: String) : TasksEvent()
    data class EndDateChanged(val value: String) : TasksEvent()
    data class EndTimeChanged(val value: String) : TasksEvent()
    data class DueDateChanged(val value: String) : TasksEvent()
    data class DueTimeChanged(val value: String) : TasksEvent()
    data class ReminderDateChanged(val value: String) : TasksEvent()
    data class ReminderTimeChanged(val value: String) : TasksEvent()
    data class LoadCreateTaskLookups(val token: String) : TasksEvent()
    data class SaveTask(val token: String) : TasksEvent()
    object ResetCreateTaskForm : TasksEvent()

    data class DeleteTask(val token: String, val taskId: String) : TasksEvent()
    object ResetDeleteTaskFlag : TasksEvent()
}