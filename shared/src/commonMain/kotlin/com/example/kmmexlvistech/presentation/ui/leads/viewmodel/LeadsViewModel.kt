package com.example.kmmexlvistech.presentation.ui.leads.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CreateLeadRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadPayloadDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsFilterDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsSortDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateLeadPayloadDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateLeadRequestDto
import com.example.kmmexlvistech.business.datasource.models.LeadDetailDto
import com.example.kmmexlvistech.business.repos.LeadsRepository
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption
import com.example.kmmexlvistech.presentation.ui.leads.formatDate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class LeadsViewModel(
    private val leadsRepository: LeadsRepository
) : ViewModel() {
    val state: MutableState<LeadsState> = mutableStateOf(LeadsState())

    fun onEvent(event: LeadsEvent) {
        when (event) {
            is LeadsEvent.GetLeads -> {
                state.value = state.value.copy(page = 1, leads = emptyList())
                fetchLeads(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is LeadsEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                val nextPage = state.value.page + 1
                fetchLeads(token = event.token, userCode = event.userCode, page = nextPage, append = true)
            }
            is LeadsEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, leads = emptyList())
                fetchLeads(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is LeadsEvent.StatusFilterChanged -> {
                state.value = state.value.copy(selectedStatusId = event.statusId, page = 1, leads = emptyList())
                fetchLeads(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is LeadsEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is LeadsEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
            is LeadsEvent.LoadLeadDetails -> loadLeadDetails(event.token, event.leadId)
            is LeadsEvent.SelectLead -> state.value = state.value.copy(selectedLeadId = event.leadId)

            is LeadsEvent.LoadCreateLeadLookups -> loadCreateLeadLookups(event.token)

            is LeadsEvent.FirstNameChanged -> state.value = state.value.copy(firstName = event.value)
            is LeadsEvent.LastNameChanged -> state.value = state.value.copy(lastName = event.value)
            is LeadsEvent.CompanyNameChanged -> state.value = state.value.copy(companyName = event.value)
            is LeadsEvent.MobileChanged -> state.value = state.value.copy(mobile = event.value)
            is LeadsEvent.PhoneChanged -> state.value = state.value.copy(phone = event.value)
            is LeadsEvent.EmailChanged -> state.value = state.value.copy(email = event.value)
            is LeadsEvent.LeadSourceChanged -> state.value = state.value.copy(leadSourceId = event.id)
            is LeadsEvent.LeadStatusChanged -> state.value = state.value.copy(leadStatusId = event.id)
            is LeadsEvent.LeadOwnerChanged -> state.value = state.value.copy(leadOwnerId = event.id)
            is LeadsEvent.AssignedToChanged -> state.value = state.value.copy(assignedToId = event.id)
            is LeadsEvent.CampaignChanged -> state.value = state.value.copy(campaignId = event.id)
            is LeadsEvent.IsActiveChanged -> state.value = state.value.copy(isActive = event.value)
            is LeadsEvent.IsConvertedChanged -> state.value = state.value.copy(isConverted = event.value)
            is LeadsEvent.SaveLead -> saveLead(event.token)
            is LeadsEvent.ResetCreateLeadForm -> state.value = state.value.copy(
                firstName = "", lastName = "", companyName = "", mobile = "", phone = "", email = "",
                leadSourceId = null, leadStatusId = null, leadOwnerId = null, assignedToId = null,
                campaignId = null, isActive = true, isConverted = false,
                isSavingLead = false, saveLeadError = null, leadSaved = false,
                isEditMode = false, editingLeadId = null,
                isUpdatingLead = false, updateLeadError = null, leadUpdated = false,
            )

            is LeadsEvent.DeleteLead -> deleteLead(event.token, event.leadId)
            is LeadsEvent.ResetDeleteLeadFlag -> state.value = state.value.copy(leadDeleted = false)

            is LeadsEvent.LoadLeadForEdit -> loadLeadForEdit(event.lead)
            is LeadsEvent.UpdateLead -> updateLead(event.token)

        }
    }


    private fun loadLeadForEdit(lead: LeadDetailDto) {
        state.value = state.value.copy(
            isEditMode = true,
            editingLeadId = lead.id,
            firstName = lead.firstName ?: "",
            lastName = lead.lastName ?: "",
            companyName = lead.leadCompanyName ?: "",
            mobile = lead.mobile ?: "",
            phone = lead.phone ?: "",
            email = lead.email ?: "",
            leadSourceId = lead.leadSourceId,
            leadStatusId = lead.leadStatusId,
            leadOwnerId = lead.leadOwner,
            assignedToId = lead.assignedTo,
            campaignId = lead.campaignId,
            isActive = lead.isactive,
            isConverted = lead.isConverted,
            isSavingLead = false,
            saveLeadError = null,
            leadSaved = false,
            isUpdatingLead = false,
            updateLeadError = null,
            leadUpdated = false,

            editingLeadDisplayId = lead.leadId.toString(),
            editingLeadCreatedOn = formatDate(lead.createddatetime),
            editingLeadLastModified = formatDate(lead.modifieddatetime ?: lead.createddatetime),
            editingLeadLastModifiedBy = "-",
        )
    }

    private fun updateLead(token: String) {
        val s = state.value
        val leadId = s.isUpdatingLead

        if (leadId.toString().isBlank()) {
            appendToMessageQueue(UIComponent.ToastSimple("Missing lead id for update"))
            return
        }

        if (s.firstName.isBlank() || s.lastName.isBlank() || s.companyName.isBlank() ||
            s.mobile.isBlank() || s.leadSourceId == null || s.leadStatusId == null ||
            s.leadOwnerId == null || s.assignedToId == null
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(isUpdatingLead = true, updateLeadError = null)

            val requestDto = UpdateLeadRequestDto(
                id = leadId.toString(),
                lead = UpdateLeadPayloadDto(
                    firstName = s.firstName,
                    lastName = s.lastName,
                    email = s.email.ifBlank { null },
                    mobile = s.mobile,
                    phone = s.phone.ifBlank { null },
                    leadCompanyName = s.companyName,
                    leadSourceId = s.leadSourceId!!,
                    leadStatusId = s.leadStatusId!!,
                    campaignId = s.campaignId,
                    leadOwner = s.leadOwnerId!!,
                    assignedTo = s.assignedToId!!,
                    leadItems = emptyList() // TODO: wire real item edits once items UI exists
                )
            )

            leadsRepository.updateLead(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isUpdatingLead = false, updateLeadError = "Failed to update lead")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(isUpdatingLead = false, leadUpdated = true)
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Lead updated successfully"))
                        } else {
                            state.value = state.value.copy(
                                isUpdatingLead = false,
                                updateLeadError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isUpdatingLead = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun deleteLead(token: String, leadId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isDeletingLead = true, deleteLeadError = null)

            leadsRepository.deleteLead(token, leadId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isDeletingLead = false, deleteLeadError = "Failed to delete lead")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isDeletingLead = false,
                                leadDeleted = true,
                                // Remove the deleted lead from the cached list too, so going back to
                                // the Leads list doesn't briefly show a now-nonexistent row.
                                leads = state.value.leads.filterNot { it.id == leadId },
                            )
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Lead deleted successfully"))
                        }
                        else {
                            state.value = state.value.copy(
                                isDeletingLead = false,
                                deleteLeadError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isDeletingLead = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadCreateLeadLookups(token: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingLookups = true)

            leadsRepository.getLeadStatuses(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    val options = dataState.data.data.map {
                        DropdownOption(id = it.leadStatusId, label = it.statusName)
                    }
                    state.value = state.value.copy(leadStatusOptions = options)
                }
            }.launchIn(viewModelScope)

            leadsRepository.getLeadSources(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    val options = dataState.data.data.map {
                        DropdownOption(id = it.leadSourceId, label = it.sourceName)
                    }
                    state.value = state.value.copy(leadSourceOptions = options)
                }
            }.launchIn(viewModelScope)

            state.value = state.value.copy(isLoadingLookups = false)
        }
    }

    private fun saveLead(token: String) {
        val s = state.value

        // Basic validation matching the required (*) fields in the screenshot
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.companyName.isBlank() ||
            s.mobile.isBlank() || s.leadSourceId == null || s.leadStatusId == null ||
            s.leadOwnerId == null || s.assignedToId == null
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(isSavingLead = true, saveLeadError = null)

            val requestDto = CreateLeadRequestDto(
                lead = LeadPayloadDto(
                    firstName = s.firstName,
                    lastName = s.lastName,
                    email = s.email.ifBlank { null },
                    mobile = s.mobile,
                    phone = s.phone.ifBlank { null },
                    leadCompanyName = s.companyName,
                    leadSourceId = s.leadSourceId!!,
                    leadStatusId = s.leadStatusId!!,
                    campaignId = null,
                    leadOwner = s.leadOwnerId!!,
                    assignedTo = s.assignedToId!!,
                    leadItems = emptyList() // no item-entry UI in this form yet
                )
            )

            leadsRepository.createLead(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isSavingLead = false, saveLeadError = "Failed to save lead")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(isSavingLead = false, leadSaved = true, saveLeadError = null)
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Lead created successfully"))
                        } else {
                            state.value = state.value.copy(
                                isSavingLead = false,
                                saveLeadError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isSavingLead = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun fetchLeads(token: String, userCode: Int, page: Int, append: Boolean) {
        viewModelScope.launch {
            state.value = if (append) state.value.copy(isLoadingMore = true)
            else state.value.copy(progressBarState = ProgressBarState.Loading)

            val requestDto = LeadsRequestDto(
                page = page,
                pageSize = state.value.pageSize,
                filters = LeadsFilterDto(
                    search = state.value.searchQuery.ifBlank { null },
                    assignedTo = userCode,
                    statusId = state.value.selectedStatusId,
                ),
                sort = LeadsSortDto(),
            )

            leadsRepository.getLeads(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> onEvent(LeadsEvent.Error(dataState.uiComponent))
                    is DataState.Data -> {
                        state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                        if (dataState.data?.status == true) {
                            val incoming = dataState.data.data
                            state.value = state.value.copy(
                                leads = if (append) state.value.leads + incoming else incoming,
                                page = page,
                                totalRecords = dataState.data.totalRecords,
                            )
                        } else {
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data?.message.orEmpty()))
                        }
                    }
                    is DataState.Loading -> {
                        if (!append) state.value = state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadLeadDetails(token: String, leadId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingDetails = true, detailsError = null)

            leadsRepository.getLeadsDetails(token, leadId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isLoadingDetails = false,
                            detailsError = "Failed to load lead details"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                lead = dataState.data.data,
                                detailsError = null
                            )
                        } else {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                detailsError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isLoadingDetails = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) return
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove()
            state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("AppDebug: removeHeadMessage: Nothing to remove")
        }
    }
}