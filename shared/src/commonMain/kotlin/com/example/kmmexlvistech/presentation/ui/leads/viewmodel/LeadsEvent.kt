package com.example.kmmexlvistech.presentation.ui.leads.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent

sealed class LeadsEvent {
    data class GetLeads(val token: String, val userCode: Int) : LeadsEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : LeadsEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : LeadsEvent()
    data class StatusFilterChanged(val statusId: Int?, val token: String, val userCode: Int) : LeadsEvent()
    object OnRemoveHeadFromQueue : LeadsEvent()
    data class Error(val uiComponent: UIComponent) : LeadsEvent()
    data class LoadLeadDetails(val token: String, val leadId: String) : LeadsEvent()
    data class SelectLead(val leadId: String) : LeadsEvent()


}