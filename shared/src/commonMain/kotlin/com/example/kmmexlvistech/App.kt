package com.example.kmmexlvistech

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import com.example.kmmexlvistech.common.setupPlatformKoin
import com.example.kmmexlvistech.di.appModule
import com.example.kmmexlvistech.navigation.AppNavigation
import com.example.kmmexlvistech.presentation.theme.SalesInfusionTheme
import com.example.kmmexlvistech.presentation.ui.splash.SplashNav
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.KoinApplication
import com.example.kmmexlvistech.common.platformModule

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.common.SystemBarsEffect

import com.example.kmmexlvistech.presentation.ui.splash.SplashNav
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@Composable
fun App(context: AppContext) {
    KoinApplication(application = {
        setupPlatformKoin(context)
        modules(appModule(context), platformModule())
    }) {
        PreComposeApp {
            setSingletonImageLoaderFactory { platformContext: PlatformContext ->
                createImageLoader(platformContext)
            }

            // Optional: persisted user choice (see note below). Defaults to following the system.
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            val appDataStore: AppDataStore = koinInject()
            LaunchedEffect(Unit) {
                themeMode = when (appDataStore.readValue(DataStoreKeys.THEME_MODE)) {
                    "LIGHT" -> ThemeMode.LIGHT
                    "DARK" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            }

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            SalesInfusionTheme(darkTheme = darkTheme) {
                // Keeps status/navigation bar icons readable on both themes
                SystemBarsEffect(darkTheme = darkTheme)

                // Paints the theme background and sets the default text/icon color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    val navigator = rememberNavigator()
                    NavHost(
                        navigator = navigator,
                        initialRoute = AppNavigation.Main.route,
                    ) {
                        scene(route = AppNavigation.Main.route) {
                            SplashNav(
                                navigateToMain = {
                                    navigator.navigate(AppNavigation.Main.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}