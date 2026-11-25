package com.example.kmmexlvistech.presentation.ui.contacts.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.ContactFilterDto
import com.example.kmmexlvistech.business.datasource.dto.ContactSortDto
import com.example.kmmexlvistech.business.datasource.dto.ContactsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateContactRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateContactRequestDto
import com.example.kmmexlvistech.business.repos.ContactsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ContactsViewModel(private val repo: ContactsRepository) : ViewModel() {
    val state: MutableState<ContactsState> = mutableStateOf(ContactsState())

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.GetContacts -> {
                state.value = state.value.copy(page = 1, contacts = emptyList())
                fetchContacts(event.token, 1, append = false)
            }
            is ContactsEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                fetchContacts(event.token, state.value.page + 1, append = true)
            }
            is ContactsEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, contacts = emptyList())
                fetchContacts(event.token, 1, append = false)
            }
            is ContactsEvent.StatusChipSelected -> {
                state.value = state.value.copy(isActiveFilter = event.isActive, page = 1, contacts = emptyList())
                fetchContacts(event.token, 1, append = false)
            }

            is ContactsEvent.LoadLookups -> loadLookups(event.token)
            is ContactsEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is ContactsEvent.OnRemoveHeadFromQueue -> removeHeadMessage()

            is ContactsEvent.FirstNameChanged -> state.value = state.value.copy(firstName = event.value)
            is ContactsEvent.LastNameChanged -> state.value = state.value.copy(lastName = event.value)
            is ContactsEvent.EmailChanged -> state.value = state.value.copy(email = event.value)
            is ContactsEvent.MobileChanged -> state.value =
                state.value.copy(mobile = event.value.filter { it.isDigit() || it == '+' }.take(15))
            is ContactsEvent.PhoneChanged -> state.value = state.value.copy(phone = event.value)
            is ContactsEvent.AccountChanged -> state.value = state.value.copy(accountId = event.id, editAccountName = null)
            is ContactsEvent.RoleChanged -> state.value = state.value.copy(contactRoleId = event.id)
            is ContactsEvent.PrimaryChanged -> state.value = state.value.copy(isPrimaryContact = event.value)
            is ContactsEvent.ResetContactForm -> state.value = state.value.copy(
                editingContactId = null, firstName = "", lastName = "", email = "", mobile = "", phone = "",
                accountId = null, editAccountName = null, contactRoleId = null, isPrimaryContact = false,
                showValidation = false, isSaving = false, saveError = null, contactSaved = false,
            )
            is ContactsEvent.LoadContactForEdit -> {
                val c = event.contact
                state.value = state.value.copy(
                    editingContactId = c.id,
                    firstName = c.firstName.orEmpty(),
                    lastName = c.lastName.orEmpty(),
                    email = c.email.orEmpty(),
                    mobile = c.mobile.orEmpty(),
                    phone = c.phone.orEmpty(),
                    accountId = c.account?.id,
                    editAccountName = c.accountLabel,
                    contactRoleId = c.contactRole?.contactRoleId?.takeIf { it > 0 },
                    isPrimaryContact = c.isPrimaryContact,
                    showValidation = false, isSaving = false, saveError = null, contactSaved = false,
                )
            }
            is ContactsEvent.SaveContact -> saveContact(event.token)

            is ContactsEvent.SelectContact ->
                state.value = state.value.copy(selectedContactId = event.id, contact = null, detailsError = null)
            is ContactsEvent.LoadContactDetails -> loadDetails(event.token, event.id)
            is ContactsEvent.DeleteContact -> deleteContact(event.token, event.id)
            is ContactsEvent.ResetDeleteFlag -> state.value = state.value.copy(contactDeleted = false)

            is ContactsEvent.QuickFilterSelected -> {
                state.value = state.value.copy(
                    primaryOnly = event.primaryOnly, isActiveFilter = event.isActive, page = 1, contacts = emptyList(),
                )
                fetchContacts(event.token, 1, append = false)
            }
            is ContactsEvent.ApplyFilters -> {
                state.value = state.value.copy(
                    searchQuery = event.search, selectedAccountName = event.accountName,
                    isActiveFilter = event.isActive, sortAscending = event.sortAscending,
                    page = 1, contacts = emptyList(),
                )
                fetchContacts(event.token, 1, append = false)
            }
            is ContactsEvent.ToggleSort -> {
                state.value = state.value.copy(sortAscending = !state.value.sortAscending, page = 1, contacts = emptyList())
                fetchContacts(event.token, 1, append = false)
            }

            is ContactsEvent.AccountCleared -> state.value = state.value.copy(accountId = null, editAccountName = null)
            is ContactsEvent.RoleCleared -> state.value = state.value.copy(contactRoleId = null)
        }
    }

    private fun fetchContacts(token: String, page: Int, append: Boolean) {
        val s = state.value
        state.value = if (append) s.copy(isLoadingMore = true) else s.copy(progressBarState = ProgressBarState.Loading)

        val request = ContactsRequestDto(
            page = page,
            pageSize = s.pageSize,
            filters = ContactFilterDto(
                search = s.searchQuery,
                accountName = s.selectedAccountName.orEmpty(),
                isActive = s.isActiveFilter,
            ),
            sort = ContactSortDto("firstName", if (s.sortAscending) "asc" else "desc"),
        )

        repo.getContacts(token, request).onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> onEvent(ContactsEvent.Error(ds.uiComponent))
                is DataState.Data -> {
                    state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                    val body = ds.data
                    if (body?.status == true) {
                        state.value = state.value.copy(
                            contacts = if (append) state.value.contacts + body.data else body.data,
                            page = page,
                            totalRecords = body.totalRecords,
                        )
                    } else {
                        appendToMessageQueue(UIComponent.ToastSimple(body?.message.orEmpty()))
                    }
                }
                is DataState.Loading -> if (!append) {
                    state.value = state.value.copy(progressBarState = ds.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadLookups(token: String) {
        repo.getContactRoles(token).onEach { ds ->
            val body = (ds as? DataState.Data)?.data
            if (body?.status == true) state.value = state.value.copy(
                roleOptions = body.data.map { RoleOption(it.contactRoleId, it.roleName.orEmpty(), it.description) }

            )
        }.launchIn(viewModelScope)

        repo.getAccountsForPicker(token).onEach { ds ->
            val body = (ds as? DataState.Data)?.data
            if (body?.status == true) state.value = state.value.copy(
                accountOptions = body.data.map { AccountPick(it.id, it.accountName.orEmpty(), it.accountCode) }
            )
        }.launchIn(viewModelScope)
    }

    private fun loadDetails(token: String, id: String) {
        state.value = state.value.copy(isLoadingDetails = true, detailsError = null)
        repo.getContactDetails(token, id).onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    state.value = state.value.copy(isLoadingDetails = false, detailsError = "Failed to load contact")
                    appendToMessageQueue(ds.uiComponent)
                }
                is DataState.Data -> {
                    val body = ds.data
                    state.value = if (body?.status == true && body.data != null)
                        state.value.copy(isLoadingDetails = false, contact = body.data, detailsError = null)
                    else state.value.copy(isLoadingDetails = false, detailsError = body?.message ?: "Something went wrong")
                }
                is DataState.Loading ->
                    state.value = state.value.copy(isLoadingDetails = ds.progressBarState == ProgressBarState.Loading)
            }
        }.launchIn(viewModelScope)
    }

    private fun saveContact(token: String) {
        val s = state.value
        state.value = s.copy(showValidation = true)

        val accountId = s.accountId
        val roleId = s.contactRoleId
        val editId = s.editingContactId
        val emailOk = s.email.isBlank() || Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(s.email.trim())




        if (s.firstName.isBlank() || s.lastName.isBlank() || !emailOk || s.mobile.length < 7 ||
            accountId == null || roleId == null
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fix the highlighted fields"))
            return
        }
        if (s.isSaving) return   // prevent duplicate submissions

        val flow = if (editId != null) {
            repo.updateContact(token, UpdateContactRequestDto(
                id = editId, firstName = s.firstName.trim(), lastName = s.lastName.trim(),
                email = s.email.trim(), mobile = s.mobile, contactRoleId = roleId,
                isPrimaryContact = s.isPrimaryContact, accountId = accountId,
            )
            )
        } else {
            repo.createContact(token, CreateContactRequestDto(
                accountId = accountId, firstName = s.firstName.trim(), lastName = s.lastName.trim(),
                email = s.email.trim(), mobile = s.mobile, phone = s.phone.trim().ifBlank { null },
                contactRoleId = roleId, isPrimaryContact = s.isPrimaryContact,
            )
            )
        }

        state.value = state.value.copy(isSaving = true, saveError = null)
        flow.onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    state.value = state.value.copy(isSaving = false, saveError = "Failed to save contact")
                    appendToMessageQueue(ds.uiComponent)
                }
                is DataState.Data -> {
                    val body = ds.data
                    if (body?.status == true) {
                        state.value = state.value.copy(isSaving = false, contactSaved = true)
                        appendToMessageQueue(UIComponent.ToastSimple(
                            body.message ?: if (editId != null) "Contact updated successfully" else "Contact created successfully"
                        ))
                        fetchContacts(token, 1, append = false)       // refresh the list
                        if (editId != null && editId == state.value.selectedContactId) loadDetails(token, editId)
                    } else {
                        state.value = state.value.copy(isSaving = false, saveError = body?.message ?: "Something went wrong")
                        appendToMessageQueue(UIComponent.ToastSimple(body?.message ?: "Something went wrong"))
                    }
                }
                is DataState.Loading ->
                    state.value = state.value.copy(isSaving = ds.progressBarState == ProgressBarState.Loading)
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteContact(token: String, id: String) {
        state.value = state.value.copy(isDeleting = true)
        repo.deleteContact(token, id).onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    state.value = state.value.copy(isDeleting = false)
                    appendToMessageQueue(ds.uiComponent)
                }
                is DataState.Data -> {
                    val body = ds.data
                    if (body?.status == true) {
                        state.value = state.value.copy(
                            isDeleting = false, contactDeleted = true,
                            contacts = state.value.contacts.filterNot { it.id == id },
                            totalRecords = (state.value.totalRecords - 1).coerceAtLeast(0),
                        )
                        appendToMessageQueue(UIComponent.ToastSimple(body.message ?: "Contact deleted successfully"))
                    } else {
                        // not found / already deleted / failure: keep the record on screen
                        state.value = state.value.copy(isDeleting = false)
                        appendToMessageQueue(UIComponent.ToastSimple(body?.message ?: "Delete failed"))
                    }
                }
                is DataState.Loading ->
                    state.value = state.value.copy(isDeleting = ds.progressBarState == ProgressBarState.Loading)
            }
        }.launchIn(viewModelScope)
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
        } catch (e: Exception) { println("AppDebug: removeHeadMessage: Nothing to remove") }
    }
}