package com.example.kmmexlvistech.presentation.ui.contacts.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.ContactDto

data class RoleOption(val id: Int, val label: String, val description: String? = null)
data class AccountPick(val id: String, val label: String, val code: String? = null)


data class ContactsState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),

    // list
    val contacts: List<ContactDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedAccountName: String? = null,
    val isActiveFilter: Boolean? = true,          // true = Active, false = Inactive, null = All

    // lookups
    val roleOptions: List<RoleOption> = emptyList(),
    val accountOptions: List<AccountPick> = emptyList(),

    // details
    val selectedContactId: String? = null,
    val contact: ContactDto? = null,
    val isLoadingDetails: Boolean = false,
    val detailsError: String? = null,
    val primaryOnly: Boolean = false,
    // form
    val editingContactId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val mobile: String = "",
    val phone: String = "",
    val accountId: String? = null,
    val editAccountName: String? = null,          // label fallback if the account isn't in the first 100
    val contactRoleId: Int? = null,
    val isPrimaryContact: Boolean = false,
    val showValidation: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val contactSaved: Boolean = false,
    val sortAscending: Boolean = true,
    val isDeleting: Boolean = false,
    val contactDeleted: Boolean = false,
) {
    val hasMore: Boolean get() = contacts.size < totalRecords
    val isEditMode: Boolean get() = editingContactId != null


    // inside the class body, add / replace:
    val visibleContacts: List<ContactDto>
        get() = if (primaryOnly) contacts.filter { it.isPrimaryContact } else contacts
    val hasActiveFilters: Boolean
        get() = selectedAccountName != null || !sortAscending || isActiveFilter == null
}