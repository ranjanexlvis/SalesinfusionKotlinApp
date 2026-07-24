package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.presentation.theme.DrawerHeaderBg
import com.example.kmmexlvistech.presentation.theme.DrawerItemSelectedBg

data class DrawerItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

// Static for now — swap for a ViewModel-backed list once roles/permissions decide the menu.
val defaultDrawerItems = listOf(
    DrawerItem("home", "Home", Icons.Default.Home),
    DrawerItem("leads", "My Leads", Icons.Default.Groups),
    DrawerItem("tasks", "My Tasks", Icons.Default.CheckCircle),
    DrawerItem("calls", "My Calls", Icons.Default.Call),
    DrawerItem("calendar", "Calendar", Icons.Default.CalendarMonth),
    DrawerItem("reports", "Reports", Icons.Default.BarChart),
    DrawerItem("profile", "My Profile", Icons.Default.Person),
    DrawerItem("settings", "Settings", Icons.Default.Settings),
    DrawerItem("logout", "Log Out", Icons.Default.Logout),
)

@Composable
fun AppDrawerContent(
    userName: String = "New User",
    userRole: String = "Sales Executive",
    items: List<DrawerItem> = defaultDrawerItems,
    selectedId: String = "home",
    onItemClick: (DrawerItem) -> Unit,
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DrawerHeaderBg)
                .padding(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    userName.take(1),
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                userName,
                color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Text(
                userRole,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(8.dp))

        items.forEach { item ->
            val selected = item.id == selectedId
            val isDestructive = item.id == "logout"
            NavigationDrawerItem(
                label = {
                    Text(
                        item.label,
                        color = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface,
                    )
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    )
                },
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = DrawerItemSelectedBg,
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    .clickable(enabled = false) { }, // click handled by NavigationDrawerItem itself
            )
        }
    }
}