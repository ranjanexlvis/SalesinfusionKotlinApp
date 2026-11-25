package com.example.kmmexlvistech.presentation.ui.contacts.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.ContactDto

sealed class ContactsEvent {
    data class GetContacts(val token: String) : ContactsEvent()
    data class LoadNextPage(val token: String) : ContactsEvent()
    data class SearchQueryChanged(val query: String, val token: String) : ContactsEvent()
    data class StatusChipSelected(val isActive: Boolean?, val token: String) : ContactsEvent()

    data class LoadLookups(val token: String) : ContactsEvent()
    object OnRemoveHeadFromQueue : ContactsEvent()
    data class Error(val uiComponent: UIComponent) : ContactsEvent()

    data class FirstNameChanged(val value: String) : ContactsEvent()
    data class LastNameChanged(val value: String) : ContactsEvent()
    data class EmailChanged(val value: String) : ContactsEvent()
    data class MobileChanged(val value: String) : ContactsEvent()
    data class PhoneChanged(val value: String) : ContactsEvent()
    data class AccountChanged(val id: String) : ContactsEvent()
    data class RoleChanged(val id: Int) : ContactsEvent()
    data class PrimaryChanged(val value: Boolean) : ContactsEvent()
    object ResetContactForm : ContactsEvent()
    data class LoadContactForEdit(val contact: ContactDto) : ContactsEvent()
    data class SaveContact(val token: String) : ContactsEvent()

    data class SelectContact(val id: String) : ContactsEvent()
    data class LoadContactDetails(val token: String, val id: String) : ContactsEvent()
    data class DeleteContact(val token: String, val id: String) : ContactsEvent()
    object ResetDeleteFlag : ContactsEvent()

    data class QuickFilterSelected(val primaryOnly: Boolean, val isActive: Boolean?, val token: String) : ContactsEvent()
    data class ApplyFilters(
        val token: String, val search: String, val accountName: String?,
        val isActive: Boolean?, val sortAscending: Boolean,
    ) : ContactsEvent()
    data class ToggleSort(val token: String) : ContactsEvent()

    // ContactsEvent
    object AccountCleared : ContactsEvent()
    object RoleCleared : ContactsEvent()


}