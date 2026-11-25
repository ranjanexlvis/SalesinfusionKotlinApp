package com.example.kmmexlvistech.business.datasource.impl

import com.example.kmmexlvistech.business.datasource.models.AccountDashboardData
import com.example.kmmexlvistech.business.datasource.models.AccountReportReq
import com.example.kmmexlvistech.business.datasource.models.CrmApiResp
import com.example.kmmexlvistech.business.datasource.models.CrmListResp
import com.example.kmmexlvistech.business.datasource.models.LeadDashboardData
import com.example.kmmexlvistech.business.datasource.models.LeadReportReq
import com.example.kmmexlvistech.business.datasource.services.CrmReportService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class CrmReportServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : CrmReportService {

    private suspend inline fun <reified B, reified T> post(path: String, token: String, body: B): T =
        client.post("$baseUrl$path") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    override suspend fun leadDashboard(token: String, req: LeadReportReq): CrmApiResp<LeadDashboardData> =
        post("/api/CrmReport/Lead/Dashboard", token, req)

    override suspend fun accountDashboard(token: String, req: AccountReportReq): CrmApiResp<AccountDashboardData> =
        post("/api/CrmReport/Account/Dashboard", token, req)

    override suspend fun leadReport(token: String, req: LeadReportReq): CrmListResp =
        post("/api/CrmReport/Lead", token, req)

    override suspend fun accountReport(token: String, req: AccountReportReq): CrmListResp =
        post("/api/CrmReport/Account", token, req)
}
