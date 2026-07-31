package com.example.kmmexlvistech.presentation.ui.meetings.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LeadDto

sealed class MeetingsEvent {
    data class GetMeetingss(val token: String, val userCode: Int) : MeetingsEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : MeetingsEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : MeetingsEvent()
    data class StatusFilterChanged(val statusName: String?, val token: String, val userCode: Int) : MeetingsEvent()
    object OnRemoveHeadFromQueue : MeetingsEvent()
    data class Error(val uiComponent: UIComponent) : MeetingsEvent()

    data class SelectMeeting(val meetingId: String) : MeetingsEvent()
    data class LoadMeetingDetails(val token: String, val meetingId: String) : MeetingsEvent()

    data class SubjectChanged(val value: String) : MeetingsEvent()
    data class DescriptionChanged(val value: String) : MeetingsEvent()
    data class RelatedRecordTypeChanged(val type: String) : MeetingsEvent()
    data class LeadSearchQueryChanged(val query: String, val token: String) : MeetingsEvent()
    data class LeadSelected(val lead: LeadDto) : MeetingsEvent()
    data class MeetingTypeChanged(val type: String) : MeetingsEvent() // "OFFLINE" | "ONLINE"
    data class LocationChanged(val value: String) : MeetingsEvent()
    data class MeetingLinkChanged(val value: String) : MeetingsEvent()
    data class MeetingPriorityChanged(val id: Int) : MeetingsEvent()
    data class MeetingStatusChanged(val id: Int) : MeetingsEvent()
    data class MeetingAssignedToChanged(val userId: Int) : MeetingsEvent()
    data class StartDateTimeChanged(val millis: Long) : MeetingsEvent()
    data class EndDateTimeChanged(val millis: Long) : MeetingsEvent()
    data class ReminderDateTimeChanged(val millis: Long) : MeetingsEvent()
    data class LoadCreateMeetingLookups(val token: String) : MeetingsEvent()
    data class SaveMeeting(val token: String) : MeetingsEvent()
    object ResetCreateMeetingForm : MeetingsEvent()
    data class DeleteMeeting(val token: String, val meetingId: String) : MeetingsEvent()
    object ResetDeleteMeetingFlag : MeetingsEvent()

}