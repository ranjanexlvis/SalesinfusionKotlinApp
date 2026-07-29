package com.example.kmmexlvistech.presentation.ui.calls


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
import androidx.compose.material.icons.filled.*
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
import com.example.kmmexlvistech.business.datasource.dto.CallDto
import com.example.kmmexlvistech.business.datasource.models.ActivityCallData
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsEvent
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallDetailsScreen(
    state: CallsState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onRelatedToClick: () -> Unit,
    onRemoveHeadFromQueue: () -> Unit = {},
    onTryAgain: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
        progressBarState = if (state.isLoadingDetails || state.isDeletingCall) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = onTryAgain,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Call Details", color = Color.White, fontWeight = FontWeight.SemiBold) },
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
                    state.call != null -> CallDetailsContent(state.call, onRelatedToClick)
                    !state.isLoadingDetails -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No call found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Call") },
            text = { Text("Are you sure you want to delete this call? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteConfirmed()
                }) { Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CallDetailsContent(
    call: ActivityCallData,
    onRelatedToClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(48.dp).background(Color(0xFFDCFCE7), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF16A34A))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(call.subject, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    PendingBadge(call.status.statusName)
                    Spacer(Modifier.height(6.dp))
                    Text(call.description, color = Color.Gray, fontSize = 13.sp)
                }
            }
        }

        // Details card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                DetailRow(
                    icon = Icons.Default.Groups,
                    label = "Related To",
                    value = "Lead • ${call.lead.leadName} (ID: ${call.lead.leadId})",
                    valueColor = Color(0xFF3B82F6),
                    trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray) },
                    onClick = onRelatedToClick
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(
                    icon = Icons.Default.CheckCircleOutline,
                    label = "Priority",
                    value = "",
                    trailing = { PriorityValue(call.priority.priorityName) }
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(
                    icon = Icons.Default.CheckCircleOutline,
                    label = "Status",
                    value = "",
                    trailing = { PendingBadge(call.status.statusName) }
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Person, "Assigned To", call.assignedTo.fullName)
            }
        }

        // Date/time card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                DetailRow(Icons.Default.CalendarToday, "Start Date & Time", formatDate(call.startDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CalendarToday, "End Date & Time", formatDate(call.endDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Notifications, "Reminder", formatDate(call.reminderDateTime))
            }
        }

        // Description card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF16A34A))
                    Spacer(Modifier.width(8.dp))
                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(call.description, color = Color.Gray, fontSize = 14.sp)
            }
        }

        // Created / Modified footer card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(Modifier.padding(16.dp).fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Created On", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(formatDate(call.createddatetime), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column(Modifier.weight(1f)) {
                    Text("Last Modified", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(formatDate(call.modifieddatetime ?: call.createddatetime), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun PendingBadge(text: String) {
    Box(Modifier.background(Color(0xFFFEF3C7), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, color = Color(0xFFB45309), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PriorityValue(priority: String) {
    val color = when (priority.lowercase()) {
        "low" -> Color(0xFF22C55E)
        "medium" -> Color(0xFFF59E0B)
        "high" -> Color(0xFFEF4444)
        else -> Color.Gray
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(priority, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.DarkGray,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp)
        }
        if (trailing != null) trailing() else Text(value, fontWeight = FontWeight.Medium, color = valueColor)
    }
}