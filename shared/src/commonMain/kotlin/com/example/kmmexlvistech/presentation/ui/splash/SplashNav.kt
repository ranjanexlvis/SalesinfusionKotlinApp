package com.example.kmmexlvistech.presentation.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.navigation.MainNavigation
import com.example.kmmexlvistech.navigation.SplashNavigation
import com.example.kmmexlvistech.presentation.ui.dashboard.DashboardScreen
import com.example.kmmexlvistech.presentation.ui.login.LoginScreen
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject

@Composable
internal fun SplashNav(
    viewModel: LoginViewModel = koinInject(),
    navigateToMain: () -> Unit,
) {
    val navigator = rememberNavigator()
    val appDataStoreManager: AppDataStore = koinInject()
    val scope = rememberCoroutineScope()

    NavHost(
        navigator = navigator,
        initialRoute = SplashNavigation.Splash.route
    ) {
        scene(route = SplashNavigation.Splash.route) {
            SplashScreen(
                state = viewModel.state.value,
                events = viewModel::onEvent,
                navigateToMain = {
                    navigator.navigate(MainNavigation.Home.route)
                },
                navigateToLogin = {
                    navigator.navigate(SplashNavigation.Login.route)
                },
                appDataStoreManager = appDataStoreManager
            )
        }

        scene(route = SplashNavigation.Login.route) {
            LoginScreen(
                loginState = viewModel.state.value,
                events = viewModel::onEvent,
                onGoogleLogin = {
                    scope.launch {
                        println("GoogleLogin: clicked")
                    }
                },
                navigateToMain = {
                    navigator.navigate(MainNavigation.Home.route)
                }
            )
        }

        scene(route = MainNavigation.Home.route) {
            DashboardScreen(
                onNavigateToNotifications = {
                  //  navigator.navigate(MainNavigation.Notifications.route)
                },
                onNavigateToProfile = {
                    navigator.navigate(MainNavigation.Profile.route)
                },
                onNavigateToMyWork = {
                //    navigator.navigate(MainNavigation.MyWork.route)
                },
                onNavigateToActivityLog = {
                //    navigator.navigate(MainNavigation.ActivityLog.route)
                },
                onLogout = {
                    scope.launch {
                        // Clear the persisted session so Splash's token check
                        // sends the user back to Login next launch, and so
                        // this logout takes effect immediately too.
                    //    appDataStoreManager.saveValue(DataStoreKeys.TOKEN, "")
                        navigator.navigate(SplashNavigation.Login.route)
                    }
                },
            )
        }
    }
}