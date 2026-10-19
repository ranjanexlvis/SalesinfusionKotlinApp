package com.example.kmmexlvistech.presentation.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.TaskDto
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskState
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TasksEvent
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

private data class PriorityFilter(val name: String?, val label: String)
private val priorityFilters = listOf(
    PriorityFilter(null, "All"),
    PriorityFilter("Low", "Low"),
    PriorityFilter("Medium", "Medium"),
    PriorityFilter("High", "High"),
)

@Composable
fun TaskScreen(
    taskState: TaskState,
    events: (TasksEvent) -> Unit,
    systemToken: String,
    userCode: Int,
    onTaskClick: (TaskDto) -> Unit = {},
    onTaskMenuClick: (TaskDto) -> Unit = {},
) {
    LaunchedEffect(systemToken, userCode) {
        if (systemToken.isNotBlank()) {
            events(TasksEvent.GetTasks(systemToken, userCode))
        }
    }

    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(listState, taskState.tasks.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && taskState.hasMore && !taskState.isLoadingMore) {
                events(TasksEvent.LoadNextPage(systemToken, userCode))
            }
        }
    }

    DefaultUiScreen(
        queue = taskState.errorQueue,
        onRemoveHeadFromQueue = { events(TasksEvent.OnRemoveHeadFromQueue) },
        progressBarState = taskState.progressBarState,
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
                    events(TasksEvent.SearchQueryChanged(it, systemToken, userCode))
                },
                placeholder = { Text("Search tasks...") },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                priorityFilters.forEach { filter ->
                    val selected = filter.name == taskState.selectedPriorityName
                    FilterChip(
                        selected = selected,
                        onClick = { events(TasksEvent.PriorityFilterChanged(filter.name, systemToken, userCode)) },
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

            if (taskState.tasks.isEmpty() && taskState.progressBarState == ProgressBarState.Idle) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Text("No tasks found.", fontSize = 13.sp, color = colors.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    itemsIndexed(taskState.tasks, key = { _, task -> task.id }) { _, task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onMenuClick = { onTaskMenuClick(task) },
                        )
                    }
                    if (taskState.isLoadingMore) {
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
private fun TaskCard(task: TaskDto, onClick: () -> Unit, onMenuClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val priorityColor = priorityColorFor(task.priority.priorityName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(priorityColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = priorityColor, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    task.subject,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                )
                Spacer(Modifier.width(8.dp))
                StatusPill(label = task.priority.priorityName, color = priorityColor)
            }

            task.lead?.let {
                Text("Lead: ${it.leadName}", fontSize = 12.5.sp, color = colors.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
            }
            Text(
                "Status: ${task.status.statusName}",
                fontSize = 12.5.sp,
                color = colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp),
            )
            Text(
                "Due: ${formatDueDate(task.dueDateTime)}",
                fontSize = 12.5.sp,
                color = colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        IconButton(onClick = onMenuClick, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = colors.onSurface.copy(alpha = 0.5f))
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

private fun priorityColorFor(priorityName: String): Color = when (priorityName.lowercase()) {
    "high" -> Color(0xFFDC2626)
    "medium" -> Color(0xFFFF9800)
    "low" -> Color(0xFF4CAF50)
    else -> Color(0xFF607D8B)
}

private fun formatDueDate(raw: String): String {
    return try {
        val datePart = raw.substringBefore("T")
        val timePart = raw.substringAfter("T").take(5)
        "$datePart $timePart"
    } catch (e: Exception) {
        raw
    }
}