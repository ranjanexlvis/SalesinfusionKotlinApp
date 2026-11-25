package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.models.DashboardDataDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.WidgetDTO
import com.example.kmmexlvistech.presentation.theme.*          // parseHexColor lives here
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeEvent
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

@Composable
fun HomeScreen(
    homeState: HomeState,
    events: (HomeEvent) -> Unit,
    systemToken: String,
    userName: String = "",                       // fallback only; the API name wins
    onCodeClick: (String) -> Unit = {},          // receives the tapped widget's widgetCode
    onQuickActionClick: (QuickActionDTO) -> Unit = {},
) {
    LaunchedEffect(systemToken) {
        if (systemToken.isNotBlank()) {
            events(HomeEvent.GetDashboard(systemToken))
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

        // ---- everything below is derived from the two APIs ----
        val dashboard: DashboardDataDTO? = homeState.getDashboard.data
        val header = dashboard?.header
        val counts = remember(dashboard) { dashboard.toCountMap() }

        val groups = homeState.getWidgetActivityResponse.data
            .filter { it.isVisible }
            .sortedBy { it.displayOrder }

        val displayName = header?.displayName ?: header?.firstName ?: userName
        val greeting = header?.greeting?.takeIf { it.isNotBlank() }
        val subTitle = header?.subTitle?.takeIf { it.isNotBlank() }
        val headline = listOfNotNull(greeting, displayName.takeIf { it.isNotBlank() }).joinToString(", ")

        Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // ---------- Greeting ----------
                if (headline.isNotBlank() || subTitle != null) {
                    item {
                        Column {
                            if (headline.isNotBlank()) {
                                Text(
                                    "$headline \uD83D\uDC4B",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onBackground,
                                )
                            }
                            subTitle?.let {
                                Text(
                                    it,
                                    fontSize = 13.sp,
                                    color = colors.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                        }
                    }
                }

                // ---------- Sections: fully driven by the widget API ----------
                if (groups.isEmpty() && homeState.progressBarState == ProgressBarState.Idle) {
                    item { EmptyWidgetsState() }
                }
                groups.forEach { group ->
                    item(key = group.widgetId) {
                        WidgetSection(node = group, counts = counts, onCodeClick = onCodeClick)
                    }
                }
            }

            // ---------- Quick action scrim ----------
            AnimatedVisibility(
                visible = homeState.isSheetOpen,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200)),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.scrim.copy(alpha = 0.5f))
                        .clickable { events(HomeEvent.ToggleQuickActionSheet) },
                )
            }

            // ---------- FAB + quick actions (from the quick-action API) ----------
            Column(
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AnimatedVisibility(
                    visible = homeState.isSheetOpen,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 4 },
                    exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 4 },
                ) {
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        homeState.getQuickActionResponse.data.forEach { action ->
                            QuickActionRow(action) {
                                events(HomeEvent.ToggleQuickActionSheet)
                                onQuickActionClick(action)
                            }
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

// =============================================================
// Counts: the one place where the counts API is joined to widgets by widgetCode.
// A new count field on the backend = one new line here.
// =============================================================
private fun DashboardDataDTO?.toCountMap(): Map<String, Int> {
    if (this == null) return emptyMap()
    return buildMap {
        myWork?.let {
            put("LEADS", it.leads)
            put("TASKS", it.tasks)
            put("CALLS", it.calls)
            put("MEETINGS", it.meetings)
        }
        todaysFocus?.let {
            put("TODAYS_TASKS", it.dueTasks)
            put("PENDING_CALLS", it.pendingCalls)
            put("UPCOMING_MEETINGS", it.meetingsToday)
            put("FOLLOW_UPS", it.followUps)
        }
    }
}

// =============================================================
// Dynamic widget tree
// =============================================================

private fun WidgetDTO.visibleChildren(): List<WidgetDTO> =
    children.filter { it.isVisible }.sortedBy { it.displayOrder }

// `as Int?` keeps this compiling whether columnSpan is Int or Int?
@Suppress("USELESS_CAST")
private fun WidgetDTO.span(): Int = (columnSpan as Int?)?.coerceIn(1, 2) ?: 1

@Composable
private fun WidgetSection(
    node: WidgetDTO,
    counts: Map<String, Int>,
    onCodeClick: (String) -> Unit,
    depth: Int = 0,
) {
    val children = node.visibleChildren()

    // a widget with no children is a single item with no section title
    if (children.isEmpty()) {
        WidgetBlock(listOf(node), node.layoutType, counts, onCodeClick)
        return
    }

    val leaves = children.filter { it.visibleChildren().isEmpty() }
    val nested = children.filter { it.visibleChildren().isNotEmpty() }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(node.widgetName, depth)
        if (leaves.isNotEmpty()) {
            WidgetBlock(leaves, leaves.first().layoutType, counts, onCodeClick)
        }
        nested.forEach { WidgetSection(it, counts, onCodeClick, depth + 1) }
    }
}

@Composable
private fun WidgetBlock(
    items: List<WidgetDTO>,
    layoutType: String?,
    counts: Map<String, Int>,
    onCodeClick: (String) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    if (layoutType == "GRID") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            packRows(items, maxCols = 2).forEach { rowItems ->
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),   // equal card heights per row
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { w ->
                        StatCard(
                            modifier = Modifier.weight(w.span().toFloat()).fillMaxHeight(),
                            label = w.widgetName,
                            count = counts[w.widgetCode],
                            icon = iconForName(w.icon),
                            tone = widgetTone(w),
                            onClick = { onCodeClick(w.widgetCode) },
                        )
                    }
                    val used = rowItems.sumOf { it.span() }
                    if (used < 2) Spacer(Modifier.weight((2 - used).toFloat()))
                }
            }
        }
    } else {
        val shape = RoundedCornerShape(16.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.surface)
                .border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), shape),
        ) {
            items.forEachIndexed { index, w ->
                if (index > 0) HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
                ListRow(
                    label = w.widgetName,
                    count = counts[w.widgetCode],
                    icon = iconForName(w.icon),
                    tone = widgetTone(w),
                ) { onCodeClick(w.widgetCode) }
            }
        }
    }
}

// pack children into rows of at most `maxCols` columns, honouring each columnSpan
private fun packRows(items: List<WidgetDTO>, maxCols: Int): List<List<WidgetDTO>> {
    val rows = mutableListOf<MutableList<WidgetDTO>>()
    var used = 0
    for (w in items) {
        val s = w.span().coerceAtMost(maxCols)
        if (rows.isEmpty() || used + s > maxCols) {
            rows += mutableListOf<WidgetDTO>()
            used = 0
        }
        rows.last() += w
        used += s
    }
    return rows
}

// =============================================================
// Building blocks
// =============================================================

@Composable
private fun SectionTitle(text: String, depth: Int = 0) {
    Text(
        text,
        fontSize = if (depth == 0) 16.sp else 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun StatCard(
    label: String,
    count: Int?,
    icon: ImageVector,
    tone: Tone,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(tone.bg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = tone.fg, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // a widget with no count shows no number at all; null while loading shows a dash
            Text(
                count?.toString() ?: "—",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ListRow(
    label: String,
    count: Int?,
    icon: ImageVector,
    tone: Tone,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(tone.bg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tone.fg, modifier = Modifier.size(17.dp))
        }
        Text(
            label,
            fontSize = 14.sp,
            color = colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(start = 12.dp),
        )
        if (count != null) {
            Text(
                count.toString(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (count > 0) tone.fg else colors.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp).size(20.dp),
        )
    }
}

@Composable
private fun QuickActionRow(action: QuickActionDTO, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val accent = parseHexColor(action.color)
    Row(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .background(colors.surfaceContainerHigh, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(34.dp).background(accent.copy(alpha = 0.18f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                iconForQuickAction(action.icon),
                contentDescription = action.actionName,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            action.actionName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface,
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
        )
    }
}

@Composable
private fun EmptyWidgetsState() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Default.Widgets, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(8.dp))
        Text("No widgets configured yet.", fontSize = 13.sp, color = colors.onSurfaceVariant)
    }
}

// =============================================================
// Icons and colours
// =============================================================

private fun iconForName(icon: String?): ImageVector = when (icon) {
    "users" -> Icons.Default.Groups
    "check-square" -> Icons.Default.CheckCircle
    "phone" -> Icons.Default.Call
    "calendar" -> Icons.Default.CalendarMonth
    "calendar-clock" -> Icons.Default.Event
    "briefcase" -> Icons.Default.Work
    "target" -> Icons.Default.GpsFixed
    "clipboard-list" -> Icons.Default.Assignment
    "phone-forwarded" -> Icons.Default.PhoneForwarded
    "history" -> Icons.Default.History
    "clock" -> Icons.Default.AccessTime
    else -> Icons.Default.Widgets
}

private fun iconForQuickAction(icon: String?): ImageVector = when (icon) {
    "person_add" -> Icons.Default.PersonAdd
    "task_alt" -> Icons.Default.TaskAlt
    "call" -> Icons.Default.Call
    "event" -> Icons.Default.Event
    "note_add", "description" -> Icons.Default.Description
    else -> Icons.Default.Add
}

private data class Tone(val fg: Color, val bg: Color)

private enum class Palette { PURPLE, GREEN, BLUE, ORANGE, RED, AMBER }

@Composable
private fun paletteTone(p: Palette): Tone {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return when (p) {
        Palette.PURPLE -> if (dark) Tone(Color(0xFFC4B5FD), Color(0xFF2E2547)) else Tone(Color(0xFF7C3AED), Color(0xFFF1E8FF))
        Palette.GREEN -> if (dark) Tone(Color(0xFF86EFAC), Color(0xFF14352A)) else Tone(Color(0xFF16A34A), Color(0xFFE3F6EA))
        Palette.BLUE -> if (dark) Tone(Color(0xFF93C5FD), Color(0xFF172A4A)) else Tone(Color(0xFF2563EB), Color(0xFFE5EEFF))
        Palette.ORANGE -> if (dark) Tone(Color(0xFFFDBA74), Color(0xFF3F2A17)) else Tone(Color(0xFFEA580C), Color(0xFFFFEDE0))
        Palette.RED -> if (dark) Tone(Color(0xFFFCA5A5), Color(0xFF3F1D1D)) else Tone(Color(0xFFDC2626), Color(0xFFFEE2E2))
        Palette.AMBER -> if (dark) Tone(Color(0xFFFCD34D), Color(0xFF3A2F10)) else Tone(Color(0xFFD97706), Color(0xFFFEF3C7))
    }
}

// API colour when the widget sends one; otherwise a stable colour from its widgetId,
// so every widget (including ones added later) gets a theme-safe colour with no code change.
@Composable
private fun widgetTone(w: WidgetDTO): Tone {
    w.color?.takeIf { it.isNotBlank() }?.let { hex ->
        val c = parseHexColor(hex)
        return Tone(fg = c, bg = c.copy(alpha = 0.14f))
    }
    return paletteTone(Palette.entries[w.widgetId.mod(Palette.entries.size)])
}

// =============================================================
// Top bar
// =============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    notificationCount: Int,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    title: String = "Sales Infusion",
    userInitial: String = "",          // blank shows a person icon instead of a fixed letter
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
                title,
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
                            .background(colors.error, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            notificationCount.coerceAtMost(9).toString(),
                            color = colors.onError,
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
                if (userInitial.isNotBlank()) {
                    Text(
                        userInitial.take(1).uppercase(),
                        color = colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = colors.onPrimaryContainer,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}