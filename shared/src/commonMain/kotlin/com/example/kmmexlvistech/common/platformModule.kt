package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.AppContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module

expect fun KoinApplication.setupPlatformKoin(context: AppContext)
expect fun platformModule(): Module

