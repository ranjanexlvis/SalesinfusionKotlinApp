package com.example.kmmexlvistech.business.repos

import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.AccountFilterDto
import com.example.kmmexlvistech.business.datasource.dto.AccountSortDto
import com.example.kmmexlvistech.business.datasource.dto.AccountsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.ContactsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateContactRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LookupFilterDto
import com.example.kmmexlvistech.business.datasource.dto.LookupRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LookupSortDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateContactRequestDto
import com.example.kmmexlvistech.business.datasource.services.AccountsApiServices
import com.example.kmmexlvistech.business.datasource.services.ContactsApiServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContactsRepository(
    private val service: ContactsApiServices,
    private val accountsService: AccountsApiServices,
) {
    private fun <T> call(block: suspend () -> T): Flow<DataState<T>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try { emit(DataState.Data(block())) }
        catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

    fun getContacts(token: String, r: ContactsRequestDto) = call { service.getContacts(token, r) }
    fun getContactDetails(token: String, id: String) = call { service.getContactDetails(token, id) }
    fun createContact(token: String, r: CreateContactRequestDto) = call { service.createContact(token, r) }
    fun updateContact(token: String, r: UpdateContactRequestDto) = call { service.updateContact(token, r) }
    fun deleteContact(token: String, id: String) = call { service.deleteContact(token, id) }

    fun getContactRoles(token: String) = call {
        service.getContactRoles(token,
            LookupRequestDto(1, 50, LookupFilterDto("", true), LookupSortDto("role_name", "ASC"))
        )
    }

    // account picker: first 100 active accounts
    fun getAccountsForPicker(token: String) = call {
        accountsService.getAccounts(
            token,
            AccountsRequestDto(
                1, 100, AccountFilterDto("", "", "", true),
                AccountSortDto("accountName", "asc")
            ),
        )
    }
}