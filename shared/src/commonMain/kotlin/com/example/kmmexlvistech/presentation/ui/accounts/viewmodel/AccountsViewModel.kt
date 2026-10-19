package com.example.kmmexlvistech.presentation.ui.accounts.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.AccountFilterDto
import com.example.kmmexlvistech.business.datasource.dto.AccountSortDto
import com.example.kmmexlvistech.business.datasource.dto.AccountsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateAccountRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateAccountRequestDto
import com.example.kmmexlvistech.business.repos.AccountsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.collections.emptyList


class AccountsViewModel(private val repo: AccountsRepository) : ViewModel() {
    val state: MutableState<AccountsState> = mutableStateOf(AccountsState())

    fun onEvent(event: AccountsEvent) {
        when (event) {
            is AccountsEvent.GetAccounts -> {
                state.value = state.value.copy(page = 1, accounts = emptyList())
                fetchAccounts(event.token, 1, append = false)
            }
            is AccountsEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                fetchAccounts(event.token, state.value.page + 1, append = true)
            }
            is AccountsEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, accounts = emptyList())
                fetchAccounts(event.token, 1, append = false)
            }
            is AccountsEvent.TypeChipSelected -> {
                state.value = state.value.copy(selectedTypeName = event.typeName, page = 1, accounts = emptyList())
                fetchAccounts(event.token, 1, append = false)
            }
            is AccountsEvent.ApplyFilters -> {
                state.value = state.value.copy(
                    searchQuery = event.search, selectedTypeName = event.typeName,
                    selectedIndustryName = event.industryName, isActiveFilter = event.isActive,
                    page = 1, accounts = emptyList(),
                )
                fetchAccounts(event.token, 1, append = false)
            }
            is AccountsEvent.LoadLookups -> loadLookups(event.token)
            is AccountsEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is AccountsEvent.OnRemoveHeadFromQueue -> removeHeadMessage()

            is AccountsEvent.CodeChanged -> state.value = state.value.copy(accountCode = event.value)
            is AccountsEvent.NameChanged -> state.value = state.value.copy(accountName = event.value)
            is AccountsEvent.TypeChanged -> state.value = state.value.copy(accountTypeId = event.id)
            is AccountsEvent.IndustryChanged -> state.value = state.value.copy(industryId = event.id)
            is AccountsEvent.WebsiteChanged -> state.value = state.value.copy(website = event.value)
            is AccountsEvent.OwnerChanged -> state.value = state.value.copy(ownerUserId = event.id)
            is AccountsEvent.ResetAccountForm -> state.value = state.value.copy(
                editingAccountId = null, accountCode = "", accountName = "", accountTypeId = null,
                industryId = null, website = "", ownerUserId = null, showValidation = false,
                isSaving = false, saveError = null, accountSaved = false,
            )
            is AccountsEvent.LoadAccountForEdit -> {
                val a = event.account
                state.value = state.value.copy(
                    editingAccountId = a.id,
                    accountCode = a.accountCode.orEmpty(),
                    accountName = a.accountName.orEmpty(),
                    accountTypeId = a.accountType?.accountTypeId?.takeIf { it > 0 },
                    industryId = a.industry?.industryId?.takeIf { it > 0 },
                    website = a.website.orEmpty(),
                    ownerUserId = a.owner?.userId?.takeIf { it > 0 },
                    showValidation = false, isSaving = false, saveError = null, accountSaved = false,
                )
            }
            is AccountsEvent.SaveAccount -> saveAccount(event.token)

            is AccountsEvent.SelectAccount ->
                state.value = state.value.copy(selectedAccountId = event.id, account = null, detailsError = null)
            is AccountsEvent.LoadAccountDetails -> loadDetails(event.token, event.id)
            is AccountsEvent.DeleteAccount -> deleteAccount(event.token, event.id)
            is AccountsEvent.ResetDeleteFlag -> state.value = state.value.copy(accountDeleted = false)
        }
    }

    private fun fetchAccounts(token: String, page: Int, append: Boolean) {
        val s = state.value
        state.value = if (append) s.copy(isLoadingMore = true) else s.copy(progressBarState = ProgressBarState.Loading)

        val request = AccountsRequestDto(
            page = page,
            pageSize = s.pageSize,
            filters = AccountFilterDto(
                search = s.searchQuery,
                accountTypeName = s.selectedTypeName.orEmpty(),
                industryName = s.selectedIndustryName.orEmpty(),
                isActive = s.isActiveFilter,
            ),
            sort = AccountSortDto("accountName", "asc"),
        )

        repo.getAccounts(token, request).onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> onEvent(AccountsEvent.Error(ds.uiComponent))
                is DataState.Data -> {
                    state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                    val body = ds.data
                    if (body?.status == true) {
                        state.value = state.value.copy(
                            accounts = if (append) state.value.accounts + body.data else body.data,
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
        repo.getAccountTypes(token).onEach { ds ->
            val body = (ds as? DataState.Data)?.data
            if (body?.status == true) state.value = state.value.copy(
                typeOptions = body.data.map { AccountOption(it.accountTypeId, it.typeName.orEmpty()) }
            )
        }.launchIn(viewModelScope)

        repo.getIndustries(token).onEach { ds ->
            val body = (ds as? DataState.Data)?.data
            if (body?.status == true) state.value = state.value.copy(
                industryOptions = body.data.map { AccountOption(it.industryId, it.industryName.orEmpty()) }
            )
        }.launchIn(viewModelScope)

        repo.getUserList(token).onEach { ds ->
            val body = (ds as? DataState.Data)?.data
            if (body?.status == true) state.value = state.value.copy(
                ownerOptions = body.data.map { AccountOption(it.userId, "${it.fullName} (${it.userId})") }
            )
        }.launchIn(viewModelScope)
    }

    private fun loadDetails(token: String, id: String) {
        state.value = state.value.copy(isLoadingDetails = true, detailsError = null)
        repo.getAccountDetails(token, id).onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    state.value = state.value.copy(isLoadingDetails = false, detailsError = "Failed to load account")
                    appendToMessageQueue(ds.uiComponent)
                }
                is DataState.Data -> {
                    val body = ds.data
                    state.value = if (body?.status == true && body.data != null)
                        state.value.copy(isLoadingDetails = false, account = body.data, detailsError = null)
                    else state.value.copy(isLoadingDetails = false, detailsError = body?.message ?: "Something went wrong")
                }
                is DataState.Loading ->
                    state.value = state.value.copy(isLoadingDetails = ds.progressBarState == ProgressBarState.Loading)
            }
        }.launchIn(viewModelScope)
    }

    private fun saveAccount(token: String) {
        val s = state.value
        state.value = s.copy(showValidation = true)

        val typeId = s.accountTypeId
        val industryId = s.industryId
        val ownerId = s.ownerUserId
        val editId = s.editingAccountId

        if (s.accountName.isBlank() || typeId == null || industryId == null || ownerId == null ||
            (editId == null && s.accountCode.isBlank())
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }
        if (s.isSaving) return   // prevent duplicate submissions

        val website = s.website.trim().ifBlank { null }
        val flow = if (editId != null) {
            repo.updateAccount(token, UpdateAccountRequestDto(
                id = editId, accountName = s.accountName.trim(), website = website,
                accountTypeId = typeId, industryId = industryId, ownerUserId = ownerId,
            )
            )
        } else {
            repo.createAccount(token, CreateAccountRequestDto(
                accountCode = s.accountCode.trim(),
                accountName = s.accountName.trim(),
                accountTypeId = typeId,
                industryId = industryId,
                website = website,
                ownerUserId = ownerId,
            )
            )
        }

        state.value = state.value.copy(isSaving = true, saveError = null)
        flow.onEach { ds ->
            when (ds) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    state.value = state.value.copy(isSaving = false, saveError = "Failed to save account")
                    appendToMessageQueue(ds.uiComponent)
                }
                is DataState.Data -> {
                    val body = ds.data
                    if (body?.status == true) {
                        state.value = state.value.copy(isSaving = false, accountSaved = true)
                        appendToMessageQueue(UIComponent.ToastSimple(
                            body.message ?: if (editId != null) "Account updated successfully" else "Account created successfully"
                        ))
                        fetchAccounts(token, 1, append = false)                       // refresh list
                        if (editId != null && editId == state.value.selectedAccountId) loadDetails(token, editId)
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

    private fun deleteAccount(token: String, id: String) {
        state.value = state.value.copy(isDeleting = true)
        repo.deleteAccount(token, id).onEach { ds ->
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
                            isDeleting = false, accountDeleted = true,
                            accounts = state.value.accounts.filterNot { it.id == id },
                            totalRecords = (state.value.totalRecords - 1).coerceAtLeast(0),
                        )
                        appendToMessageQueue(UIComponent.ToastSimple(body.message ?: "Account deleted successfully"))
                    } else {
                        // failure: keep the record in the UI
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