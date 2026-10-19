package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.AppContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module
import androidx.compose.runtime.Composable

actual fun KoinApplication.setupPlatformKoin(context: AppContext) {
}

actual fun platformModule(): Module = module {
    single { DeviceInfoProvider() }
}



@Composable
actual fun SystemBarsEffect(darkTheme: Boolean) {
    // iOS follows the UIUserInterfaceStyle automatically; nothing needed
    // unless you force a theme different from the system one.
}