package com.example.kmmexlvistech.presentation.ui.dashboard.viewModel

import com.example.kmmexlvistech.business.core.UIComponent

sealed class HomeEvent {
    data class GetWidgetWithActivities(val token: String) : HomeEvent()
    data class GetDashboard(val token: String) : HomeEvent()
    data class GetQuickActionMenu(val token: String) : HomeEvent()
    object ToggleQuickActionSheet : HomeEvent()
    data class UIErrorToast(val message: String) : HomeEvent()
    object OnRemoveHeadFromQueue : HomeEvent()
    data class Error(val uiComponent: UIComponent) : HomeEvent()
}