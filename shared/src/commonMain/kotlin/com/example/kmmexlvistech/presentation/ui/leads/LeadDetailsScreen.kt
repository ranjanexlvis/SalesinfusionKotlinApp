package com.example.kmmexlvistech.presentation.ui.leads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.models.LeadDetailDto
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsState

data class LeadActivityCounts(
    val tasks: Int = 0,
    val calls: Int = 0,
    val meetings: Int = 0,
    val notes: Int = 0,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailsScreen(
    state: LeadsState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onAssignedToClick: () -> Unit,
    onRemoveHeadFromQueue: () -> Unit = {},
    onTryAgain: () -> Unit = {},
    activityCounts: LeadActivityCounts = LeadActivityCounts(), // wire to real data when available
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
        progressBarState = if (state.isLoadingDetails || state.isDeletingLead) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = onTryAgain,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Lead Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Box(
                            Modifier
                                .background(colors.error, RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = colors.onError,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary,
                    actionIconContentColor = colors.onPrimary,
                ),
            )

            Box(Modifier.fillMaxWidth().weight(1f).background(colors.background)) {
                when {
                    state.detailsError != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.detailsError, color = colors.error)
                    }
                    state.lead != null -> LeadDetailsContent(
                        lead = state.lead,
                        counts = activityCounts,
                        onAssignedToClick = onAssignedToClick,
                    )
                    !state.isLoadingDetails -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No lead found", color = colors.onSurfaceVariant)
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
                    Text("Delete", color = colors.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun LeadDetailsContent(
    lead: LeadDetailDto,
    counts: LeadActivityCounts,
    onAssignedToClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val statusName = lead.leadStatus?.name ?: "Unknown"
    val statusTone = detailTone(statusKind(statusName))
    val avatarTone = detailTone(DetailKind.PURPLE)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ---------- Header ----------
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(80.dp).background(avatarTone.bg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    lead.fullName.take(2).uppercase(),
                    color = avatarTone.fg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        lead.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    Pill(statusName, statusTone)
                }
                lead.leadCompanyName?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = colors.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                }
                Spacer(Modifier.height(8.dp))
                lead.email?.takeIf { it.isNotBlank() }?.let { ContactRow(Icons.Default.Email, it) }
                lead.mobile?.takeIf { it.isNotBlank() }?.let { ContactRow(Icons.Default.Call, it) }
                lead.phone?.takeIf { it.isNotBlank() }?.let { ContactRow(Icons.Default.PhoneAndroid, it) }
            }
        }

        // ---------- Lead ID / Status / Source / Owner ----------
        SectionCard {
            Column {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    InfoField("Lead ID", Modifier.weight(1f).padding(16.dp)) {
                        Text(lead.leadId.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    }
                    Box(Modifier.width(1.dp).fillMaxHeight().background(colors.outlineVariant.copy(alpha = 0.6f)))
                    InfoField("Lead Status", Modifier.weight(1f).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Dot(statusTone.fg)
                            Spacer(Modifier.width(6.dp))
                            Text(statusName, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
                        }
                    }
                }
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    InfoField("Lead Source", Modifier.weight(1f).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Dot(detailTone(DetailKind.BLUE).fg)
                            Spacer(Modifier.width(6.dp))
                            Text(lead.leadSource?.name ?: "-", fontWeight = FontWeight.SemiBold, color = colors.onSurface)
                        }
                    }
                    Box(Modifier.width(1.dp).fillMaxHeight().background(colors.outlineVariant.copy(alpha = 0.6f)))
                    InfoField("Lead Owner", Modifier.weight(1f).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Groups, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                lead.leadOwner?.name ?: "-",
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }

        // ---------- Details ----------
        SectionCard {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, null, tint = colors.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.onSurface)
                }
                Spacer(Modifier.height(12.dp))
                DetailRow(Icons.Default.CalendarToday, "Created Date", formatDate(lead.createdDateTime))
                RowDivider()
                DetailRow(Icons.Default.CalendarToday, "Last Modified", formatDate(lead.modifiedDateTime))
                RowDivider()
                DetailRow(
                    Icons.Default.Person, "Assigned To", lead.assignedTo?.name ?: "-",
                    trailing = {
                        Text(lead.assignedTo?.name ?: "-", fontWeight = FontWeight.Medium, color = colors.onSurfaceVariant)
                        Icon(Icons.Default.ChevronRight, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    },
                    onClick = onAssignedToClick,
                )
                RowDivider()
                DetailRow(Icons.Default.Campaign, "Campaign", lead.campaign?.name ?: "-")
                RowDivider()
                DetailRow(
                    Icons.Default.PowerSettingsNew, "Active", "",
                    trailing = {
                        val tone = detailTone(if (lead.isActive) DetailKind.GREEN else DetailKind.RED)
                        Dot(tone.fg)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (lead.isActive) "Active" else "Inactive",
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface,
                        )
                    },
                )
            }
        }

        // ---------- Items ----------
        if (lead.leadItems.isNotEmpty()) {
            SectionCard {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, null, tint = colors.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Items", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.onSurface)
                    }
                    lead.leadItems.forEachIndexed { i, item ->
                        if (i > 0) RowDivider() else Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    item.itemName ?: "Item",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = colors.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    "Qty ${item.quantity} × ${formatAmount(item.unitPrice)}",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant,
                                )
                            }
                            Text(
                                formatAmount(item.totalAmount),
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurface,
                            )
                        }
                    }
                }
            }
        }

        // ---------- Activities summary ----------
        Column {
            Text(
                "Activities Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = colors.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActivityCard("Tasks", counts.tasks, Icons.Default.CheckBox, DetailKind.BLUE, Modifier.weight(1f))
                ActivityCard("Calls", counts.calls, Icons.Default.Call, DetailKind.GREEN, Modifier.weight(1f))
                ActivityCard("Meetings", counts.meetings, Icons.Default.Groups, DetailKind.PURPLE, Modifier.weight(1f))
                ActivityCard("Notes", counts.notes, Icons.Default.Description, DetailKind.AMBER, Modifier.weight(1f))
            }
        }

        // ---------- About ----------
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.primaryContainer.copy(alpha = 0.5f))
                .border(1.dp, colors.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("About Lead", fontWeight = FontWeight.Bold, color = colors.primary)
            Spacer(Modifier.height(6.dp))
            Text(
                lead.description?.takeIf { it.isNotBlank() } ?: "No description available.",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ---------------- Building blocks ----------------

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), shape)
    ) { content() }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    )
}

@Composable
private fun Pill(text: String, tone: DetailTone) {
    Box(Modifier.background(tone.bg, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, color = tone.fg, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 14.sp, color = colors.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun InfoField(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        content()
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
    trailing: (@Composable RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp, color = colors.onSurface)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailing != null) trailing() else Text(value, fontWeight = FontWeight.Medium, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun ActivityCard(label: String, count: Int, icon: ImageVector, kind: DetailKind, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val tone = detailTone(kind)
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tone.bg)
            .padding(12.dp)
    ) {
        Text(label, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 1)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
            Spacer(Modifier.weight(1f))
            Icon(icon, null, tint = tone.fg, modifier = Modifier.size(16.dp))
        }
    }
}

// ---------------- Theme-aware tones ----------------

private enum class DetailKind { PURPLE, BLUE, GREEN, AMBER, RED, NEUTRAL }
private data class DetailTone(val fg: Color, val bg: Color)

private fun statusKind(name: String) = when (name.lowercase()) {
    "new" -> DetailKind.PURPLE
    "contacted" -> DetailKind.BLUE
    "qualified", "converted", "closed won" -> DetailKind.GREEN
    "proposal" -> DetailKind.AMBER
    "closed lost" -> DetailKind.RED
    else -> DetailKind.NEUTRAL
}

@Composable
private fun detailTone(kind: DetailKind): DetailTone {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return when (kind) {
        DetailKind.PURPLE -> if (dark) DetailTone(Color(0xFFC4B5FD), Color(0xFF2E2547)) else DetailTone(Color(0xFF7C3AED), Color(0xFFF1E8FF))
        DetailKind.BLUE -> if (dark) DetailTone(Color(0xFF93C5FD), Color(0xFF172A4A)) else DetailTone(Color(0xFF2563EB), Color(0xFFE5EEFF))
        DetailKind.GREEN -> if (dark) DetailTone(Color(0xFF86EFAC), Color(0xFF14352A)) else DetailTone(Color(0xFF16A34A), Color(0xFFE3F6EA))
        DetailKind.AMBER -> if (dark) DetailTone(Color(0xFFFCD34D), Color(0xFF3A2F10)) else DetailTone(Color(0xFFD97706), Color(0xFFFEF3C7))
        DetailKind.RED -> if (dark) DetailTone(Color(0xFFFCA5A5), Color(0xFF3F1D1D)) else DetailTone(Color(0xFFDC2626), Color(0xFFFEE2E2))
        DetailKind.NEUTRAL -> if (dark) DetailTone(Color(0xFFB0BEC5), Color(0xFF263238)) else DetailTone(Color(0xFF546E7A), Color(0xFFECEFF1))
    }
}

private fun formatAmount(v: Double): String {
    val whole = v.toLong()
    return "₹" + if (v == whole.toDouble()) whole.toString() else v.toString()
}

// Keep these if other files (create/edit lead) still use them
fun formatDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    return try {
        val dt = kotlinx.datetime.LocalDateTime.parse(raw.substringBefore("."))
        val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val hour = if (dt.hour % 12 == 0) 12 else dt.hour % 12
        val amPm = if (dt.hour < 12) "AM" else "PM"
        "${dt.dayOfMonth} $month ${dt.year}, $hour:${dt.minute.toString().padStart(2, '0')} $amPm"
    } catch (e: Exception) {
        raw
    }
}

fun mapLeadStatus(id: Int): String = when (id) {
    1 -> "New"; 2 -> "Contacted"; 3 -> "Qualified"; 4 -> "Converted"; else -> "Unknown"
}

fun mapLeadSource(id: Int): String = when (id) {
    1 -> "Website"; 2 -> "Referral"; 3 -> "Cold Call"; else -> "Unknown"
}
