package com.example.kmmexlvistech.presentation.ui.accounts.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.AccountDto

data class AccountOption(val id: Int, val label: String)

data class AccountsState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),

    // list
    val accounts: List<AccountDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedTypeName: String? = null,
    val selectedIndustryName: String? = null,
    val isActiveFilter: Boolean? = true,        // true = Active, false = Inactive, null = All

    // lookups
    val typeOptions: List<AccountOption> = emptyList(),
    val industryOptions: List<AccountOption> = emptyList(),
    val ownerOptions: List<AccountOption> = emptyList(),

    // details
    val selectedAccountId: String? = null,
    val account: AccountDto? = null,
    val isLoadingDetails: Boolean = false,
    val detailsError: String? = null,

    // form
    val editingAccountId: String? = null,
    val accountCode: String = "",
    val accountName: String = "",
    val accountTypeId: Int? = null,
    val industryId: Int? = null,
    val website: String = "",
    val ownerUserId: Int? = null,
    val showValidation: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val accountSaved: Boolean = false,

    val isDeleting: Boolean = false,
    val accountDeleted: Boolean = false,
) {
    val hasMore: Boolean get() = accounts.size < totalRecords
    val isEditMode: Boolean get() = editingAccountId != null
    val hasActiveFilters: Boolean
        get() = selectedTypeName != null || selectedIndustryName != null || isActiveFilter != true
}