package com.example.kmmexlvistech.presentation.ui.meetings.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent

sealed class MeetingsEvent {
    data class GetMeetingss(val token: String, val userCode: Int) : MeetingsEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : MeetingsEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : MeetingsEvent()
    data class StatusFilterChanged(val statusName: String?, val token: String, val userCode: Int) : MeetingsEvent()
    object OnRemoveHeadFromQueue : MeetingsEvent()
    data class Error(val uiComponent: UIComponent) : MeetingsEvent()
}