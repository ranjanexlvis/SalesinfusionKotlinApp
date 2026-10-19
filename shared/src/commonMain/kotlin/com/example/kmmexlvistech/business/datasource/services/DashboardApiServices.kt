package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.models.MenuDashboardResponse



interface DashboardApiServices {

    companion object{

        const val GET_DASHBOARD_MENU = "Menus/GetWithActivities"
    }

    suspend fun
            getDashboardMenu(token: String): MenuDashboardResponse



}