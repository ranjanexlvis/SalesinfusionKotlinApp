package com.example.kmmexlvistech.navigation

sealed class AppNavigation(
    val route: String,

) {

    object Splash : AppNavigation(route = "Splash")
    object Main : AppNavigation(route = "Main")
    object Login : AppNavigation(route = "Login")


}
