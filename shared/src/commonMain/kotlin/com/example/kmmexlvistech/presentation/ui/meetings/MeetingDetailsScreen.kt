package com.example.kmmexlvistech.presentation.ui.meetings

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
import com.example.kmmexlvistech.business.datasource.dto.MeetingDto
import com.example.kmmexlvistech.business.datasource.models.ActivityMeetingData
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsEvent
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDetailsScreen(
    state: MeetingsState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRelatedToClick: () -> Unit,
    onRemoveHeadFromQueue: () -> Unit = {},
    onTryAgain: () -> Unit = {},
) {
    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
        progressBarState = if (state.isLoadingDetails) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = onTryAgain,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Meeting Details", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                    }
                    IconButton(onClick = onDeleteClick) {
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
                    state.meeting != null -> MeetingDetailsContent(state.meeting, onRelatedToClick)
                    !state.isLoadingDetails -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No meeting found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MeetingDetailsContent(
    meeting: ActivityMeetingData,
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
                    Modifier.size(48.dp).background(Color(0xFFEDEBFF), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF7C5CFC))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(meeting.subject, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        PendingBadge(meeting.status.statusName)
                        Spacer(Modifier.width(8.dp))
                        Text(meeting.description, color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        }

        // Details card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                DetailRow(
                    icon = Icons.Default.Groups,
                    label = "Related To",
                    value = "Lead • ${meeting.lead.leadName} (ID: ${meeting.lead.leadId})",
                    valueColor = Color(0xFF3B82F6),
                    trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray) },
                    onClick = onRelatedToClick
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Groups, "Meeting Type", meeting.meetingType.lowercase().replaceFirstChar { it.uppercase() })
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.LocationOn, "Location", meeting.meetingLocation ?: "-")
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(
                    icon = Icons.Default.CheckCircleOutline,
                    label = "Priority",
                    value = "",
                    trailing = { PriorityValue(meeting.priority.priorityName) }
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(
                    icon = Icons.Default.CheckCircleOutline,
                    label = "Status",
                    value = "",
                    trailing = { PendingBadge(meeting.status.statusName) }
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Person, "Assigned To", meeting.assignedTo.fullName)
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Link, "Meeting Link", meeting.meetingLink ?: "-")
            }
        }

        // Date/time card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                DetailRow(Icons.Default.CalendarToday, "Start Date & Time", formatDate(meeting.startDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CalendarToday, "End Date & Time", formatDate(meeting.endDateTime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Notifications, "Reminder", formatDate(meeting.reminderDateTime))
            }
        }

        // Description card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF7C5CFC))
                    Spacer(Modifier.width(8.dp))
                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(meeting.description, color = Color.Gray, fontSize = 14.sp)
            }
        }

        // Created / Modified footer card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(Modifier.padding(16.dp).fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Created On", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(formatDate(meeting.createddatetime), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column(Modifier.weight(1f)) {
                    Text("Last Modified", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(formatDate(meeting.modifieddatetime ?: meeting.createddatetime), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
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