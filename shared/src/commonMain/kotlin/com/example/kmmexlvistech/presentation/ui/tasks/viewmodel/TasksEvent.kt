package com.example.kmmexlvistech.presentation.ui.tasks.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent

sealed class TasksEvent {
    data class GetTasks(val token: String, val userCode: Int) : TasksEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : TasksEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : TasksEvent()
    data class PriorityFilterChanged(val priorityName: String?, val token: String, val userCode: Int) : TasksEvent()
    object OnRemoveHeadFromQueue : TasksEvent()
    data class Error(val uiComponent: UIComponent) : TasksEvent()
}