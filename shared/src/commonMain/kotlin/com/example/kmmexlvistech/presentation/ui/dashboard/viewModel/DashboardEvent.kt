package com.example.kmmexlvistech.presentation.ui.dashboard.viewModel

import com.example.kmmexlvistech.business.core.UIComponent

sealed class DashboardEvent {

    data class GetDashboardMenu(val token: String) : DashboardEvent()
    data class UIErrorToast(val message: String) : DashboardEvent()
    object OnRemoveHeadFromQueue : DashboardEvent()
    data class Error(val uiComponent: UIComponent) : DashboardEvent()
}