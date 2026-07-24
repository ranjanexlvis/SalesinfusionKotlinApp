package com.example.kmmexlvistech.navigation

sealed class MainNavigation(
    val route: String,
    val title: String,

){

    object Home : MainNavigation(
        route = "Home", title = "Home",

    )

    object Favorites : MainNavigation(
        route = "Favorites", title = "Favorites",

    )

    object Cart : MainNavigation(
        route = "Cart", title = "Cart",

    )

    object Login : MainNavigation(route = "Login", title = "Login")

    object AddressList : MainNavigation(route = "AddressList", title = "AddressList")
    object EditProfile : MainNavigation(route = "EditProfile", title = "EditProfile")

    object Profile : MainNavigation(
        route = "Profile", title = "Profile",

    )
    object Address : MainNavigation(
        route = "Address", title = "Address",

    )

    object Product : MainNavigation(route = "product/{groupId}", "product")
    object ProductDetails : MainNavigation(route = "ProductDetails/{groupId}/{prodId}", "ProductDetails")

}
