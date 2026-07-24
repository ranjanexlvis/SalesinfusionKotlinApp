package com.example.kmmexlvistech.business.core

import com.example.kmmexlvistech.business.datasource.models.ExceptionAlertResponse


sealed class UIComponent {

    data class Toast(
        val alert: ExceptionAlertResponse
    ): UIComponent()

    data class Dialog(
        val alert: ExceptionAlertResponse
    ): UIComponent()


    data class ToastSimple(
        val title:String,
    ): UIComponent()

    data     class DialogSimple(
        val title:String,
        val description:String
    ): UIComponent()


    data class None(
        val message:String,
    ): UIComponent()



}