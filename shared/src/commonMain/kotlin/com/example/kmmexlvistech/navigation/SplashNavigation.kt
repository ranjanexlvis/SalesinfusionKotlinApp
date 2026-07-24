package com.example.kmmexlvistech.navigation

sealed class SplashNavigation(
    val route: String,
    val objectName: String = "",
    val objectPath: String = ""
) {

    object Splash : SplashNavigation(route = "Splash")

    object Login : SplashNavigation(route = "Login")


    object Address : SplashNavigation(route = "Address")
}