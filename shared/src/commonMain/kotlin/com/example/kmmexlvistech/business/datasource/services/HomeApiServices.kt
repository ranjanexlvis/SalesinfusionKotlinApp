package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.DashboardResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse


interface HomeApiServices {

    companion object{

        const val GET_WIDGET_ACTIVITY = "Menus/GetWidgetWithActivities"
        const val GET_DASHBOARD = "Dashboard/Get"
        const val QUICK_ACTION_ACTIVITY = "Menus/GetQuickActionsWithActivities"
    }

    suspend fun getWidgetWithActivities(token: String): GetWidgetActivityResponse
    suspend fun getQuickActionActivities(token: String, quickActionDTO: QuickActionDto): QuickActionResponse
    suspend fun getDashboard(token: String): DashboardResponse



}