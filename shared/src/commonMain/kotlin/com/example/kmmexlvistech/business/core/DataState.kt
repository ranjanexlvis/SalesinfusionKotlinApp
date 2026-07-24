package com.example.kmmexlvistech.business.core


sealed class DataState<T> {

    data class NetworkStatus<T>(val networkState: NetworkState) : DataState<T>()

    data class Response<T>(val uiComponent: UIComponent) : DataState<T>()

    data class Data<T>(val data: T? = null, val status: String? = null) : DataState<T>()

    data class Loading<T>(val progressBarState: ProgressBarState = ProgressBarState.Idle) :
        DataState<T>()


}