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




@OptIn(ExperimentalCoilApi::class)
@Composable
fun App(
    context: AppContext
) {
    KoinApplication(application = {
        setupPlatformKoin(context)                       // registers the Android Context with Koin
        modules(appModule(context), platformModule())    // registers your actual dependency bindings
    }) {
        PreComposeApp {
            setSingletonImageLoaderFactory { platformContext: PlatformContext ->
                createImageLoader(platformContext)
            }
            SalesInfusionTheme {
                val navigator = rememberNavigator()
                Box(modifier = Modifier.fillMaxSize()) {
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