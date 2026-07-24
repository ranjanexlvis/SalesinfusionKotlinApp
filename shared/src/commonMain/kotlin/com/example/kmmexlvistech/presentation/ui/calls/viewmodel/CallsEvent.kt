package com.example.kmmexlvistech.presentation.ui.calls.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent

sealed class CallsEvent {
    data class GetCalls(val token: String, val userCode: Int) : CallsEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : CallsEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : CallsEvent()
    data class StatusFilterChanged(val statusName: String?, val token: String, val userCode: Int) : CallsEvent()
    object OnRemoveHeadFromQueue : CallsEvent()
    data class Error(val uiComponent: UIComponent) : CallsEvent()
}