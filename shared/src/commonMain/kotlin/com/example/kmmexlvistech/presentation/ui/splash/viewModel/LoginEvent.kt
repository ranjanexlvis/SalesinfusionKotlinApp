package com.example.kmmexlvistech.presentation.ui.splash.viewModel

import com.example.kmmexlvistech.business.core.NetworkState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO


sealed class LoginEvent {

    data class Login(val email: String, val password: String, val systemToken: String?) : LoginEvent()
    data class SystemToken(val userName : String, val password : String) : LoginEvent()
    data class LoginSocial(val token:String,val email : String, val callFrom : String) : LoginEvent()
    data class UiErrorToast(val message : String) : LoginEvent()

    object OnRemoveHeadFromQueue : LoginEvent()
    object DismissDialog : LoginEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : LoginEvent()


    object OnRetryNetwork : LoginEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ): LoginEvent()
}