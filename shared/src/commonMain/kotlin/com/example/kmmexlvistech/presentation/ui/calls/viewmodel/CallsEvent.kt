package com.example.kmmexlvistech.presentation.ui.calls.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.models.ActivityCallData

sealed class CallsEvent {
    data class GetCalls(val token: String, val userCode: Int) : CallsEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : CallsEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : CallsEvent()
    data class StatusFilterChanged(val statusName: String?, val token: String, val userCode: Int) : CallsEvent()
    object OnRemoveHeadFromQueue : CallsEvent()
    data class Error(val uiComponent: UIComponent) : CallsEvent()

    data class SubjectChanged(val value: String) : CallsEvent()
    data class DescriptionChanged(val value: String) : CallsEvent()
    data class RelatedRecordTypeChanged(val type: String) : CallsEvent()
    data class LeadSearchQueryChanged(val query: String, val token: String) : CallsEvent()
    data class LeadSelected(val lead: LeadDto) : CallsEvent()
    data class CallPriorityChanged(val id: Int) : CallsEvent()
    data class CallStatusChanged(val id: Int) : CallsEvent()
    data class CallAssignedToChanged(val userId: Int) : CallsEvent()
    data class StartDateTimeChanged(val millis: Long) : CallsEvent()
    data class EndDateTimeChanged(val millis: Long) : CallsEvent()
    data class ReminderDateTimeChanged(val millis: Long) : CallsEvent()
    data class LoadCreateCallLookups(val token: String) : CallsEvent()
    data class SaveCall(val token: String) : CallsEvent()
    object ResetCreateCallForm : CallsEvent()

    data class SelectCall(val callId: String) : CallsEvent()
    data class LoadCallDetails(val token: String, val callId: String) : CallsEvent()
    data class DeleteCall(val token: String, val callId: String) : CallsEvent()
    object ResetDeleteCallFlag : CallsEvent()

    // CallsEvent: add
    data class LoadCallForEdit(val call: ActivityCallData) : CallsEvent()
}