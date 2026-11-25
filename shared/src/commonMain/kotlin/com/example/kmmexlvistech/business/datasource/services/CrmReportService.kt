package com.example.kmmexlvistech.business.datasource.services

import com.example.kmmexlvistech.business.datasource.models.AccountDashboardData
import com.example.kmmexlvistech.business.datasource.models.AccountReportReq
import com.example.kmmexlvistech.business.datasource.models.CrmApiResp
import com.example.kmmexlvistech.business.datasource.models.CrmListResp
import com.example.kmmexlvistech.business.datasource.models.LeadDashboardData
import com.example.kmmexlvistech.business.datasource.models.LeadReportReq

interface CrmReportService {
    suspend fun leadDashboard(token: String, req: LeadReportReq): CrmApiResp<LeadDashboardData>
    suspend fun accountDashboard(token: String, req: AccountReportReq): CrmApiResp<AccountDashboardData>
    suspend fun leadReport(token: String, req: LeadReportReq): CrmListResp
    suspend fun accountReport(token: String, req: AccountReportReq): CrmListResp
}