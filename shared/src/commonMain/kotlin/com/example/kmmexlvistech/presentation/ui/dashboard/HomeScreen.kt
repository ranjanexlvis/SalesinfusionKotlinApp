package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.WidgetDTO
import com.example.kmmexlvistech.presentation.theme.*
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeEvent
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

@Composable
fun HomeScreen(
    homeState: HomeState,
    events: (HomeEvent) -> Unit,
    systemToken: String,
    userName: String = "User",
    onWidgetClick: (WidgetDTO) -> Unit = {},
    onQuickActionClick: (QuickActionDTO) -> Unit = {},
) {
    LaunchedEffect(systemToken) {
        if (systemToken.isNotBlank()) {
            events(HomeEvent.GetWidgetWithActivities(systemToken))
            events(HomeEvent.GetQuickActionMenu(systemToken))
        }
    }

    DefaultUiScreen(
        queue = homeState.errorQueue,
        onRemoveHeadFromQueue = { events(HomeEvent.OnRemoveHeadFromQueue) },
        progressBarState = homeState.progressBarState,
    ) {
        val colors = MaterialTheme.colorScheme
        val groups = homeState.getWidgetActivityResponse.data
            .filter { it.isVisible }
            .sortedBy { it.displayOrder }

        Box(modifier = Modifier.fillMaxSize().background(DashboardBackground)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                item {
                    Column {
                        Text(
                            "Good morning, $userName \uD83D\uDC4B",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground,
                        )
                        Text(
                            "Here's what's happening with your work today.",
                            fontSize = 13.sp,
                            color = colors.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }

                if (groups.isEmpty() && homeState.progressBarState == ProgressBarState.Idle) {
                    item { EmptyWidgetsState() }
                }

                groups.forEach { group ->
                    item(key = group.widgetId) {
                        WidgetGroupSection(group = group, onWidgetClick = onWidgetClick)
                    }
                }
            }

            // Scrim behind the action list — tapping it closes the sheet
            AnimatedVisibility(
                visible = homeState.isSheetOpen,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200)),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { events(HomeEvent.ToggleQuickActionSheet) },
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AnimatedVisibility(
                    visible = homeState.isSheetOpen,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 4 },
                    exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 4 },
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        homeState.getQuickActionResponse.data.forEach { action ->
                            QuickActionRow(
                                action = action,
                                onClick = {
                                    events(HomeEvent.ToggleQuickActionSheet)
                                    onQuickActionClick(action)
                                },
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { events(HomeEvent.ToggleQuickActionSheet) },
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        if (homeState.isSheetOpen) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (homeState.isSheetOpen) "Close quick actions" else "Quick actions",
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionRow(action: QuickActionDTO, onClick: () -> Unit) {
    val accent = parseHexColor(action.color)

    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(accent.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(iconForQuickAction(action.icon), contentDescription = action.actionName, tint = accent, modifier = Modifier.size(18.dp))
        }
        Text(
            action.actionName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
        )
    }
}

private fun iconForQuickAction(icon: String?): ImageVector = when (icon) {
    "person_add" -> Icons.Default.PersonAdd
    "task_alt" -> Icons.Default.TaskAlt
    "call" -> Icons.Default.Call
    "event" -> Icons.Default.Event
    "note_add", "description" -> Icons.Default.Description
    else -> Icons.Default.Add
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    notificationCount: Int,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(color = colors.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Open menu", tint = colors.onSurface)
            }
            Text(
                "Sales Infusion",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
            Box {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = colors.onSurface)
                }
                if (notificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(16.dp)
                            .background(Color(0xFFDC2626), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            notificationCount.coerceAtMost(9).toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center,
            ) {
                Text("A", color = colors.onPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun WidgetGroupSection(group: WidgetDTO, onWidgetClick: (WidgetDTO) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val visibleChildren = group.children.filter { it.isVisible }.sortedBy { it.displayOrder }

    Column {
        Text(group.widgetName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.onBackground)
        Spacer(Modifier.height(10.dp))
        when (group.widgetCategory) {
            "GROUP" -> when (visibleChildren.firstOrNull()?.layoutType) {
                "GRID" -> WidgetStatGrid(visibleChildren, onWidgetClick)
                else -> WidgetListCard(visibleChildren, onWidgetClick)
            }
            else -> WidgetListCard(visibleChildren, onWidgetClick)
        }
    }
}

@Composable
private fun WidgetStatGrid(children: List<WidgetDTO>, onClick: (WidgetDTO) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        children.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { widget -> DynamicStatCard(widget, modifier = Modifier.weight(1f), onClick = { onClick(widget) }) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WidgetListCard(children: List<WidgetDTO>, onClick: (WidgetDTO) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(14.dp))) {
        children.forEachIndexed { index, widget ->
            DynamicListRow(widget, onClick = { onClick(widget) })
            if (index != children.lastIndex) HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
        }
    }
}

@Composable
private fun DynamicStatCard(widget: WidgetDTO, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val accent = accentForWidgetCode(widget.widgetCode)
    Column(
        modifier = modifier.background(colors.surface, RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(14.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(accent.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(iconForName(widget.icon), contentDescription = widget.widgetName, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text("—", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accent)
        Text(widget.widgetName, fontSize = 12.sp, color = colors.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun DynamicListRow(widget: WidgetDTO, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val accent = accentForWidgetCode(widget.widgetCode)
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(30.dp).background(accent.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(iconForName(widget.icon), contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
        }
        Text(
            widget.widgetName,
            fontSize = 13.5.sp,
            color = colors.onSurface,
            modifier = Modifier.weight(1f).padding(start = 12.dp),
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.onSurface.copy(alpha = 0.35f), modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun EmptyWidgetsState() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(14.dp)).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Default.Widgets, contentDescription = null, tint = colors.onSurface.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(8.dp))
        Text("No widgets configured yet.", fontSize = 13.sp, color = colors.onSurface.copy(alpha = 0.6f))
    }
}

private fun iconForName(icon: String?): ImageVector = when (icon) {
    "users" -> Icons.Default.Groups
    "check-square" -> Icons.Default.CheckCircle
    "phone" -> Icons.Default.Call
    "calendar" -> Icons.Default.CalendarMonth
    "briefcase" -> Icons.Default.Work
    "target" -> Icons.Default.GpsFixed
    "clipboard-list" -> Icons.Default.Assignment
    "calendar-clock" -> Icons.Default.Event
    "phone-forwarded" -> Icons.Default.PhoneForwarded
    "history" -> Icons.Default.History
    "clock" -> Icons.Default.AccessTime
    else -> Icons.Default.Widgets
}

private fun accentForWidgetCode(widgetCode: String): Color = when (widgetCode) {
    "LEADS" -> DashLeadsAccent
    "TASKS", "TODAYS_TASKS" -> DashTasksAccent
    "CALLS", "PENDING_CALLS" -> DashCallsAccent
    "MEETINGS", "UPCOMING_MEETINGS" -> DashMeetingsAccent
    "FOLLOW_UPS" -> DashDueAccent
    "RECENT_ACTIVITY_LIST", "RECENT_ACTIVITIES" -> DashTasksAccent
    else -> DashCallsAccent
}