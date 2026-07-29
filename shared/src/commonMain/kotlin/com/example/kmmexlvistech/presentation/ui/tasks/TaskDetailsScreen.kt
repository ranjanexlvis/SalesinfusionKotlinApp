package com.example.kmmexlvistech.presentation.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.TaskDto
import com.example.kmmexlvistech.business.datasource.models.ActivityTaskData
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskState
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TasksEvent
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    state: TaskState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,   // renamed from onDeleteClick
    onRemoveHeadFromQueue: () -> Unit = {},
    onTryAgain: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
        progressBarState = if (state.isLoadingDetails || state.isDeletingTask) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = onTryAgain,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Task Details", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Box(Modifier.background(Color(0xFFEF4444), RoundedCornerShape(8.dp)).padding(6.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E2A5E))
            )

            Box(Modifier.fillMaxSize().weight(1f).background(Color(0xFFF5F6FA))) {
                when {
                    state.detailsError != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(state.detailsError, color = Color.Red)
                        }
                    }
                    state.task != null -> TaskDetailsContent(state.task)
                    !state.isLoadingDetails -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No task found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteConfirmed()
                }) {
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TaskDetailsContent(task: ActivityTaskData) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.subject, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    StatusBadge(task.status.statusName)
                }
                Spacer(Modifier.height(6.dp))
                Text("Lead: ${task.lead.leadName}", color = Color.Gray, fontSize = 13.sp)
            }
        }

        // Priority / Assigned To
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    InfoField("Priority", task.priority.priorityName, Modifier.weight(1f))
                    InfoField("Assigned To", task.assignedTo.fullName, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth()) {
                    InfoField("Active", if (task.isActive) "Active" else "Inactive", Modifier.weight(1f))
                }
            }
        }

        // Details section
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF7C5CFC))
                    Spacer(Modifier.width(8.dp))
                    Text("Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(12.dp))
                DetailRow(Icons.Default.CalendarToday, "Start Date", formatDate(task.startDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CalendarToday, "Due Date", formatDate(task.dueDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CalendarToday, "End Date", formatDate(task.endDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Notifications, "Reminder", formatDate(task.reminderDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CheckCircle, "Completed On", formatDate(task.completionDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CalendarToday, "Created", formatDate(task.createddatetime))
            }
        }

        // Description
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEBFF))) {
            Column(Modifier.padding(16.dp)) {
                Text("Description", fontWeight = FontWeight.Bold, color = Color(0xFF4C3AA8))
                Spacer(Modifier.height(6.dp))
                Text(task.description.ifBlank { "No description available." }, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String) {
    Box(Modifier.background(Color(0xFFDCFCE7), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, color = Color(0xFF16A34A), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp)
        }
        Text(value, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
}