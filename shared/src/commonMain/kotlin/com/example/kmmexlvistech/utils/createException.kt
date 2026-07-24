package com.example.kmmexlvistech.utils

import com.example.kmmexlvistech.business.datasource.models.ExceptionAlertResponse


const val THROWABLE_DIVIDER = "THROWABLE_DIVIDER"

fun ExceptionAlertResponse.createException() =  Throwable(" $THROWABLE_DIVIDER ${this.title} $THROWABLE_DIVIDER ${this.message}")