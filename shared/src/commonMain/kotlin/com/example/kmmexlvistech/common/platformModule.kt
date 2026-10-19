package com.example.kmmexlvistech.common

import androidx.compose.runtime.Composable
import com.example.kmmexlvistech.AppContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module

expect fun KoinApplication.setupPlatformKoin(context: AppContext)
expect fun platformModule(): Module


@Composable
expect fun SystemBarsEffect(darkTheme: Boolean)

