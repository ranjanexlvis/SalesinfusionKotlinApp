package com.example.kmmexlvistech.presentation.ui.leads

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Search
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
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.models.LeadDetailDto
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailsScreen(
    state: LeadsState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,   // renamed from onDeleteClick — now fires only after confirmation
    onAssignedToClick: () -> Unit,
    onRemoveHeadFromQueue: () -> Unit = {},
    onTryAgain: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
        progressBarState = if (state.isLoadingDetails || state.isDeletingLead) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = onTryAgain,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Lead Details", color = Color.White, fontWeight = FontWeight.SemiBold) },
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
                        Box(
                            Modifier
                                .background(Color(0xFFEF4444), RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
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
                    state.lead != null -> LeadDetailsContent(state.lead, onAssignedToClick)
                    !state.isLoadingDetails -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No lead found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Lead") },
            text = { Text("Are you sure you want to delete this lead? This action cannot be undone.") },
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
//

@Composable
private fun LeadDetailsContent(
    lead: LeadDetailDto,
    onAssignedToClick: () -> Unit
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
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(48.dp).background(Color(0xFF7C5CFC), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(lead.fullName.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold) // avatar initials
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(lead.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            StatusBadge(mapLeadStatus(lead.leadStatusId))
                        }
                        lead.leadCompanyName?.let { Text(it, color = Color.Gray, fontSize = 13.sp) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                lead.email?.let { ContactRow(Icons.Default.Email, it) }
                lead.phone?.let { ContactRow(Icons.Default.Call, it) }
                lead.mobile?.let { ContactRow(Icons.Default.PhoneAndroid, it) }
            }
        }

        // Lead ID / Status / Source / Owner
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    InfoField("Lead ID", lead.leadId.toString(), Modifier.weight(1f))
                    InfoField("Lead Status", modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Dot(Color(0xFF22C55E))
                            Spacer(Modifier.width(6.dp))
                            Text(mapLeadStatus(lead.leadStatusId), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth()) {
                    InfoField("Lead Source", modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Dot(Color(0xFF3B82F6))
                            Spacer(Modifier.width(6.dp))
                            Text(mapLeadSource(lead.leadSourceId), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    InfoField(
                        "Lead Owner",
                        "User #${lead.leadOwner}",
                        Modifier.weight(1f)
                    ) // placeholder until user lookup exists
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
                DetailRow(Icons.Default.CalendarToday, "Created Date", formatDate(lead.createddatetime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.CalendarToday, "Last Modified", formatDate(lead.modifieddatetime))
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(
                    Icons.Default.Person, "Assigned To", "User #${lead.assignedTo}",
                    trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    onClick = onAssignedToClick
                )
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(Icons.Default.Campaign, "Campaign", lead.campaignId?.let { "Campaign #$it" } ?: "-")
                Divider(Modifier.padding(vertical = 10.dp))
                DetailRow(
                    icon = Icons.Default.PowerSettingsNew,
                    label = "Active",
                    value = "",
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Dot(if (lead.isactive) Color(0xFF22C55E) else Color(0xFFEF4444))
                            Spacer(Modifier.width(6.dp))
                            Text(if (lead.isactive) "Active" else "Inactive", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
        }

        // Activities summary
        Column {
            Text("Activities Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActivityCard(
                    "Tasks",
                    0,
                    Icons.Default.CheckBox,
                    Color(0xFF3B82F6),
                    Modifier.weight(1f)
                )
                ActivityCard("Calls", 0, Icons.Default.Call, Color(0xFF22C55E), Modifier.weight(1f))
                ActivityCard(
                    "Meetings",
                    0,
                    Icons.Default.Groups,
                    Color(0xFF7C5CFC),
                    Modifier.weight(1f)
                )
                ActivityCard(
                    "Notes",
                    0,
                    Icons.Default.Description,
                    Color(0xFFF59E0B),
                    Modifier.weight(1f)
                )
            }
        }

        // About lead
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEBFF))) {
            Column(Modifier.padding(16.dp)) {
                Text("About Lead", fontWeight = FontWeight.Bold, color = Color(0xFF4C3AA8))
                Spacer(Modifier.height(6.dp))
                Text(lead.id?.takeIf { it.isNotBlank() } ?: "No description available.", color = Color.Gray)
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
private fun ContactRow(icon: ImageVector, text: String) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
private fun InfoField(label: String, value: String? = null, modifier: Modifier = Modifier, content: (@Composable () -> Unit)? = null) {
    Column(modifier) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        if (content != null) content() else Text(value ?: "-", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Dot(color: Color) {
    Box(Modifier.size(8.dp).background(color, CircleShape))
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
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
        if (trailing != null) trailing() else Text(value, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
}

@Composable
private fun ActivityCard(label: String, count: Int, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(12.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
        }
    }
}

fun formatDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    return try {
        val instant = kotlinx.datetime.LocalDateTime.parse(raw.substringBefore("."))
        val day = instant.dayOfMonth
        val month = instant.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val year = instant.year
        val hour = if (instant.hour % 12 == 0) 12 else instant.hour % 12
        val amPm = if (instant.hour < 12) "AM" else "PM"
        val minute = instant.minute.toString().padStart(2, '0')
        "$day $month $year, $hour:$minute $amPm"
    } catch (e: Exception) {
        raw
    }
}

fun mapLeadStatus(id: Int): String = when (id) {
    1 -> "New"
    2 -> "Contacted"
    3 -> "Qualified"
    4 -> "Converted"
    else -> "Unknown"
}

fun mapLeadSource(id: Int): String = when (id) {
    1 -> "Website"
    2 -> "Referral"
    3 -> "Cold Call"
    else -> "Unknown"
}