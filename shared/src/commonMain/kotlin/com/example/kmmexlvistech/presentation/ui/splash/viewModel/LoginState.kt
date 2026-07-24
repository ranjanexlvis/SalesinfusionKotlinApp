package com.example.kmmexlvistech.presentation.ui.splash.viewModel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent


data class LoginState(


    var mobNo: String = "",
    var firebaseToken: String = "",
    var deviceToken: String = "",
    var msg: String = "",
    val callFrom: String = "",
    var otp: String = "",
    var username: String = "",
    var password: String = "",
    var email: String = "",
    val isTokenValid: Boolean = false,
    var isShowDialog: Boolean = false,

    val navigateToLogin: Boolean = false,
    val navigateToOtp: Boolean = false,
    var navigateToPasswordLogin: Boolean = false,
    var navigateToMain: Boolean? = false,
    val navigateToGuardMain: Boolean? = false,
    var navigateToIntro: Boolean? = false,
    var navigateToRegister: Boolean = false,
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val registrationSuccess: Boolean = false,

    var systemToken: String? = null,

    val apiSuccessEvent: ApiSuccessEvent? = null,

    )

sealed class ApiSuccessEvent {
    data object NavigateToOtp :ApiSuccessEvent()
    data object NavigateToMain : ApiSuccessEvent()

}