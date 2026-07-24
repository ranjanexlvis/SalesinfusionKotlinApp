package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.presentation.theme.BottomNavIndicator
import com.example.kmmexlvistech.presentation.theme.BottomNavSelected
import com.example.kmmexlvistech.presentation.theme.BottomNavUnselected

enum class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("Home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    LEADS("Leads", "Leads", Icons.Filled.Groups, Icons.Outlined.Groups),
    TASKS("Tasks", "Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
    CALENDAR("Calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    MORE("More", "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz),
}

@Composable
fun SalesInfusionBottomBar(
    currentRoute: String?,
    onDestinationSelected: (BottomNavDestination) -> Unit,
) {
    NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
        BottomNavDestination.entries.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(
                        destination.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BottomNavSelected,
                    selectedTextColor = BottomNavSelected,
                    unselectedIconColor = BottomNavUnselected,
                    unselectedTextColor = BottomNavUnselected,
                    indicatorColor = BottomNavIndicator,
                ),
            )
        }
    }
}