package com.example.kmmexlvistech.android

import android.app.Application
import com.example.kmmexlvistech.AppContext
import com.example.kmmexlvistech.common.ApplicationContextHolder
import com.example.kmmexlvistech.common.ToastHandlerFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.set(this)
        ApplicationContextHolder.init(this)
        ToastHandlerFactory.initialize(this)
    }
}