package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.AppContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
actual fun KoinApplication.setupPlatformKoin(context: AppContext) {
    androidContext(context.get())
}

actual fun platformModule(): Module = module {
    single { DeviceInfoProvider(androidContext()) }
}




@Composable
actual fun SystemBarsEffect(darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
}