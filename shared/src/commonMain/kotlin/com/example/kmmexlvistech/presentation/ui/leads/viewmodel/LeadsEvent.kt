package com.example.kmmexlvistech.presentation.ui.leads.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.models.LeadDetailDto

sealed class LeadsEvent {
    data class GetLeads(val token: String, val userCode: Int) : LeadsEvent()
    data class LoadNextPage(val token: String, val userCode: Int) : LeadsEvent()
    data class SearchQueryChanged(val query: String, val token: String, val userCode: Int) : LeadsEvent()
    data class StatusFilterChanged(val statusId: Int?, val token: String, val userCode: Int) : LeadsEvent()
    object OnRemoveHeadFromQueue : LeadsEvent()
    data class Error(val uiComponent: UIComponent) : LeadsEvent()
    data class LoadLeadDetails(val token: String, val leadId: String) : LeadsEvent()
    data class SelectLead(val leadId: String) : LeadsEvent()

    data class FirstNameChanged(val value: String) : LeadsEvent()
    data class LastNameChanged(val value: String) : LeadsEvent()
    data class CompanyNameChanged(val value: String) : LeadsEvent()
    data class MobileChanged(val value: String) : LeadsEvent()
    data class PhoneChanged(val value: String) : LeadsEvent()
    data class EmailChanged(val value: String) : LeadsEvent()
    data class LeadSourceChanged(val id: Int) : LeadsEvent()
    data class LeadStatusChanged(val id: Int) : LeadsEvent()
    data class LeadOwnerChanged(val id: Int) : LeadsEvent()
    data class AssignedToChanged(val id: Int) : LeadsEvent()
    data class CampaignChanged(val id: Int?) : LeadsEvent()
    data class IsActiveChanged(val value: Boolean) : LeadsEvent()
    data class IsConvertedChanged(val value: Boolean) : LeadsEvent()
    data class SaveLead(val token: String) : LeadsEvent()
    object ResetCreateLeadForm : LeadsEvent()

    data class LoadCreateLeadLookups(val token: String) : LeadsEvent()

    data class DeleteLead(val token: String, val leadId: String) : LeadsEvent()
    object ResetDeleteLeadFlag : LeadsEvent()

    data class LoadLeadForEdit(val lead: LeadDetailDto) : LeadsEvent()
    data class UpdateLead(val token: String) : LeadsEvent()
}