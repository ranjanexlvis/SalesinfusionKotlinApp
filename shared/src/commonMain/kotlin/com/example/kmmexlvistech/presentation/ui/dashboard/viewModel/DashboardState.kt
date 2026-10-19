package com.example.kmmexlvistech.presentation.ui.dashboard.viewModel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.models.DashboardResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.MenuDashboardResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse


data class DashboardState(

    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    var systemToken: String? = null,
    val getDashboard: MenuDashboardResponse = MenuDashboardResponse(),
    val isSheetOpen: Boolean = false,

    )