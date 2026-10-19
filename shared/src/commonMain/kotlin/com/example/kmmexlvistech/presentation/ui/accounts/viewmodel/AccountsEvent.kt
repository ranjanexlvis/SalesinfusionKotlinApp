package com.example.kmmexlvistech.presentation.ui.accounts.viewmodel

import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.AccountDto

sealed class AccountsEvent {
    data class GetAccounts(val token: String) : AccountsEvent()
    data class LoadNextPage(val token: String) : AccountsEvent()
    data class SearchQueryChanged(val query: String, val token: String) : AccountsEvent()
    data class TypeChipSelected(val typeName: String?, val token: String) : AccountsEvent()
    data class ApplyFilters(
        val token: String, val search: String, val typeName: String?,
        val industryName: String?, val isActive: Boolean?,
    ) : AccountsEvent()
    data class LoadLookups(val token: String) : AccountsEvent()
    object OnRemoveHeadFromQueue : AccountsEvent()
    data class Error(val uiComponent: UIComponent) : AccountsEvent()

    data class CodeChanged(val value: String) : AccountsEvent()
    data class NameChanged(val value: String) : AccountsEvent()
    data class TypeChanged(val id: Int) : AccountsEvent()
    data class IndustryChanged(val id: Int) : AccountsEvent()
    data class WebsiteChanged(val value: String) : AccountsEvent()
    data class OwnerChanged(val id: Int) : AccountsEvent()
    object ResetAccountForm : AccountsEvent()
    data class LoadAccountForEdit(val account: AccountDto) : AccountsEvent()
    data class SaveAccount(val token: String) : AccountsEvent()

    data class SelectAccount(val id: String) : AccountsEvent()
    data class LoadAccountDetails(val token: String, val id: String) : AccountsEvent()
    data class DeleteAccount(val token: String, val id: String) : AccountsEvent()
    object ResetDeleteFlag : AccountsEvent()
}