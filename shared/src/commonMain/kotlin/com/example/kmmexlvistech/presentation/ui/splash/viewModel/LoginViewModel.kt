package com.example.kmmexlvistech.presentation.ui.splash.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent

import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.VerifyOtpDTO
import com.example.kmmexlvistech.business.datasource.models.GeoInfoProvider
import com.example.kmmexlvistech.business.repos.LoginRepository
import com.example.kmmexlvistech.common.AppUpdateHelper
import com.example.kmmexlvistech.common.DeviceInfoProvider
import com.example.kmmexlvistech.getPlatform
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val geoInfoProvider: GeoInfoProvider,
) : ViewModel() {
    val state: MutableState<LoginState> = mutableStateOf(
        LoginState()
    )

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> {
                login(event.email, event.password, event.systemToken.toString())
            }
            is LoginEvent.SystemToken -> {
                getToken(event.userName, event.password)
            }
            is LoginEvent.LoginSocial -> {
                // loginWithGoogle(event.token,event.email, event.callFrom)
            }

            is LoginEvent.UiErrorToast -> {
                appendToMessageQueue(UIComponent.ToastSimple(event.message))
            }
            is LoginEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }
            else -> {}
        }
    }

    private fun login(email: String, password: String, systemToken: String) {
        viewModelScope.launch {
            state.value = state.value.copy(progressBarState = ProgressBarState.Loading)

            val device = deviceInfoProvider.collect()
            val geo = geoInfoProvider.fetch()

            loginRepository.getLogin(
                LoginRequestDTO(
                    userName = email,
                    password = password,
                    device = device,
                    geo = geo,
                ),
                systemToken = systemToken

            ).onEach { dataState ->
                when (dataState) {


                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onEvent(LoginEvent.Error(dataState.uiComponent))
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            msg = dataState.data?.message.orEmpty(),
                            progressBarState = ProgressBarState.Idle,
                        )
                        if (dataState.data?.status == true) {
                            println("@@@print"+dataState.data?.data?.token)

                            state.value = state.value.copy(
                                apiSuccessEvent = ApiSuccessEvent.NavigateToMain,
                            )
                        } else {
                            appendToMessageQueue(
                                UIComponent.ToastSimple(dataState.data?.message.orEmpty())
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getToken(userName: String, password: String) {
        loginRepository.getToken(
            TokenRequestDTO(userName = userName, password = password)
        ).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onEvent(LoginEvent.Error(dataState.uiComponent))
                }
                is DataState.Data -> {
                    state.value = state.value.copy(
                        msg = dataState.data?.message.orEmpty(),
                        progressBarState = ProgressBarState.Idle,
                    )
                    if (dataState.data?.status == true.toString()) {
                        state.value = state.value.copy(systemToken = dataState.data.data)
                    }
                }
                is DataState.Loading -> {
                    state.value = state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) {
            println("CUSTOM_TAG: onEvent: ${uiComponent.message}")
            return
        }
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove()
            state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("AppDebug: removeHeadMessage: Nothing to remove from DialogQueue")
        }
    }
}