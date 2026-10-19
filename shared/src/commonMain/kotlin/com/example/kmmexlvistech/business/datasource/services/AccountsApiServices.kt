package com.example.kmmexlvistech.business.datasource.services

import com.example.kmmexlvistech.business.datasource.dto.AccountActionResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountDetailResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountTypesResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountsListResponse
import com.example.kmmexlvistech.business.datasource.dto.AccountsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateAccountRequestDto
import com.example.kmmexlvistech.business.datasource.dto.IndustriesResponse
import com.example.kmmexlvistech.business.datasource.dto.LookupRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateAccountRequestDto


interface AccountsApiServices {
    suspend fun getAccounts(token: String, request: AccountsRequestDto): AccountsListResponse
    suspend fun getAccountDetails(token: String, id: String): AccountDetailResponse
    suspend fun createAccount(token: String, request: CreateAccountRequestDto): AccountActionResponse
    suspend fun updateAccount(token: String, request: UpdateAccountRequestDto): AccountActionResponse
    suspend fun deleteAccount(token: String, id: String): AccountActionResponse
    suspend fun getAccountTypes(token: String, request: LookupRequestDto): AccountTypesResponse
    suspend fun getIndustries(token: String, request: LookupRequestDto): IndustriesResponse

    companion object {   // match the slash style of your other constants
        const val LIST = "Account/List"
        const val ACCOUNT = "Account"
        const val DELETE = "Account/Delete"
        const val ACCOUNT_TYPES = "CentralCrmData/AccountTypes"
        const val INDUSTRIES = "CentralCrmData/Industries"
    }
}