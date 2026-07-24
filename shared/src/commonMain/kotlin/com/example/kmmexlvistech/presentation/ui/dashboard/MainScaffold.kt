package com.example.kmmexlvistech.presentation.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.kmmexlvistech.presentation.ui.dashboard.AppDrawerContent
import com.example.kmmexlvistech.presentation.ui.dashboard.BottomNavDestination
import com.example.kmmexlvistech.presentation.ui.dashboard.DrawerItem
import com.example.kmmexlvistech.presentation.ui.dashboard.SalesInfusionBottomBar
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    currentRoute: String?,
    showBottomBar: Boolean = true,
    onBottomNavSelected: (BottomNavDestination) -> Unit,
    onDrawerItemClick: (DrawerItem) -> Unit,
    topBar: @Composable (openDrawer: () -> Unit) -> Unit = {},
    content: @Composable (openDrawer: () -> Unit, padding: PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                selectedId = currentRoute?.lowercase() ?: "home",
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    onDrawerItemClick(item)
                },
            )
        },
    ) {
        Scaffold(
            topBar = { topBar(openDrawer) },
            bottomBar = {
                if (showBottomBar) {
                    SalesInfusionBottomBar(
                        currentRoute = currentRoute,
                        onDestinationSelected = onBottomNavSelected,
                    )
                }
            },
        ) { padding ->
            content(openDrawer, padding)
        }
    }
}