package com.example.kmmexlvistech.presentation.ui.calls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.CallDto
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsEvent
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

private data class StatusFilter(val name: String?, val label: String)
private val callStatusFilters = listOf(
    StatusFilter(null, "All"),
    StatusFilter("Pending", "Pending"),
    StatusFilter("Completed", "Completed"),
    StatusFilter("Missed", "Missed"),
)

@Composable
fun CallsScreen(
    callsState: CallsState,
    events: (CallsEvent) -> Unit,
    systemToken: String,
    userCode: Int,
    onCallClick: (CallDto) -> Unit = {},
    onDialClick: (CallDto) -> Unit = {},
    onCallMenuClick: (CallDto) -> Unit = {},
) {
    LaunchedEffect(systemToken, userCode) {
        if (systemToken.isNotBlank()) {
            events(CallsEvent.GetCalls(systemToken, userCode))
        }
    }

    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(listState, callsState.calls.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && callsState.hasMore && !callsState.isLoadingMore) {
                events(CallsEvent.LoadNextPage(systemToken, userCode))
            }
        }
    }

    DefaultUiScreen(
        queue = callsState.errorQueue,
        onRemoveHeadFromQueue = { events(CallsEvent.OnRemoveHeadFromQueue) },
        progressBarState = callsState.progressBarState,
    ) {
        val colors = MaterialTheme.colorScheme

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DashboardBackground)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    events(CallsEvent.SearchQueryChanged(it, systemToken, userCode))
                },
                placeholder = { Text("Search calls...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.onSurface.copy(alpha = 0.5f)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                callStatusFilters.forEach { filter ->
                    val selected = filter.name == callsState.selectedStatusName
                    FilterChip(
                        selected = selected,
                        onClick = { events(CallsEvent.StatusFilterChanged(filter.name, systemToken, userCode)) },
                        label = { Text(filter.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primary,
                            selectedLabelColor = colors.onPrimary,
                            containerColor = colors.surface,
                            labelColor = colors.onSurface.copy(alpha = 0.7f),
                        ),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (callsState.calls.isEmpty() && callsState.progressBarState == ProgressBarState.Idle) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Text("No calls scheduled.", fontSize = 13.sp, color = colors.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    itemsIndexed(callsState.calls, key = { _, call -> call.id }) { _, call ->
                        CallCard(
                            call = call,
                            onClick = { onCallClick(call) },
                            onDialClick = { onDialClick(call) },
                            onMenuClick = { onCallMenuClick(call) },
                        )
                    }
                    if (callsState.isLoadingMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = colors.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallCard(call: CallDto, onClick: () -> Unit, onDialClick: () -> Unit, onMenuClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val statusColor = callStatusColorFor(call.status.statusName)
    val (startTime, startDate) = splitDateTime(call.startDateTime)
    val (endTime, _) = splitDateTime(call.endDateTime ?: "")
    val hasReminder = !call.reminderDateTime.isNullOrBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
    ) {
        // Colored accent strip — same visual device as the dashboard task cards,
        // gives an at-a-glance status read before reading any text.
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(statusColor, RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)),
        )

        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                // Time block — the visual anchor for a call card, since "when"
                // matters more here than for leads/tasks
                Column(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(startTime, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    if (endTime.isNotBlank()) {
                        Text("– $endTime", fontSize = 10.sp, color = statusColor.copy(alpha = 0.75f))
                    }
                }

                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(call.subject, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface, maxLines = 1)
                    call.lead?.let {
                        Text(it.leadName, fontSize = 12.5.sp, color = colors.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
                    }
                }

                if (hasReminder) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = "Reminder set", tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                }

                IconButton(onClick = onMenuClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = colors.onSurface.copy(alpha = 0.5f))
                }
            }

            call.description?.let {
                Text(
                    it,
                    fontSize = 12.sp,
                    color = colors.onSurface.copy(alpha = 0.55f),
                    maxLines = 1,
                    modifier = Modifier.padding(top = 8.dp, start = 2.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusPill(label = call.status.statusName, color = statusColor)

                Row(
                    modifier = Modifier
                        .background(colors.primary.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
                        .clickable(onClick = onDialClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Dial", tint = colors.primary, modifier = Modifier.size(14.dp))
                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.primary, modifier = Modifier.padding(start = 5.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

private fun callStatusColorFor(statusName: String): Color = when (statusName.lowercase()) {
    "pending" -> Color(0xFF2196F3)
    "completed" -> Color(0xFF4CAF50)
    "missed" -> Color(0xFFDC2626)
    else -> Color(0xFF607D8B)
}

/**
 * Splits a raw "2026-05-10T10:00:00" string into (time "10:00", date "2026-05-10").
 * Minimal parsing, no timezone handling — swap for kotlinx-datetime formatting
 * once you settle on how times should localize/display across the app.
 */
private fun splitDateTime(raw: String): Pair<String, String> {
    if (raw.isBlank()) return "" to ""
    return try {
        val date = raw.substringBefore("T")
        val time = raw.substringAfter("T").take(5)
        time to date
    } catch (e: Exception) {
        raw to ""
    }
}