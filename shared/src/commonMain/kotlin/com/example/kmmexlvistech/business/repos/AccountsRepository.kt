package com.example.kmmexlvistech.business.repos

import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.AccountsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateAccountRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LookupFilterDto
import com.example.kmmexlvistech.business.datasource.dto.LookupRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LookupSortDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateAccountRequestDto
import com.example.kmmexlvistech.business.datasource.models.UserListRequestDto
import com.example.kmmexlvistech.business.datasource.services.AccountsApiServices
import com.example.kmmexlvistech.business.datasource.services.CentralCrmApiServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AccountsRepository(
    private val service: AccountsApiServices,
    private val centralCrmApiServices: CentralCrmApiServices,
) {
    private fun <T> call(block: suspend () -> T): Flow<DataState<T>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try { emit(DataState.Data(block())) }
        catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

    fun getAccounts(token: String, r: AccountsRequestDto) = call { service.getAccounts(token, r) }
    fun getAccountDetails(token: String, id: String) = call { service.getAccountDetails(token, id) }
    fun createAccount(token: String, r: CreateAccountRequestDto) = call { service.createAccount(token, r) }
    fun updateAccount(token: String, r: UpdateAccountRequestDto) = call { service.updateAccount(token, r) }
    fun deleteAccount(token: String, id: String) = call { service.deleteAccount(token, id) }

    fun getAccountTypes(token: String) = call {
        service.getAccountTypes(token, LookupRequestDto(
            1, 50,
            LookupFilterDto("", true), LookupSortDto("type_name", "ASC")
        )
        )
    }
    fun getIndustries(token: String) = call {
        service.getIndustries(token, LookupRequestDto(1, 100, LookupFilterDto("", true),
            LookupSortDto("industry_name", "ASC")
        ))
    }
    fun getUserList(token: String) = call { centralCrmApiServices.getUserList(token,
        UserListRequestDto()
    ) }
}