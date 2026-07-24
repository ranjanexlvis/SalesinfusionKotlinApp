package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.AppContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun KoinApplication.setupPlatformKoin(context: AppContext) {
}

actual fun platformModule(): Module = module {
    single { DeviceInfoProvider() }
}