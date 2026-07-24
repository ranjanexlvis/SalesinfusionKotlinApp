package com.example.kmmexlvistech.presentation.ui.leads

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsEvent
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsState

private data class StatusFilter(val id: Int?, val label: String)
private val statusFilters = listOf(
    StatusFilter(null, "All"),
    StatusFilter(1, "New"),
    StatusFilter(2, "Contacted"),
    StatusFilter(3, "Qualified"),
    StatusFilter(4, "Proposal"),
)

@Composable
fun LeadsScreen(
    leadsState: LeadsState,
    events: (LeadsEvent) -> Unit,
    systemToken: String,
    userCode: Int,
    onLeadClick: (LeadDto) -> Unit = {},
    onLeadMenuClick: (LeadDto) -> Unit = {},
) {
    LaunchedEffect(systemToken, userCode) {
        if (systemToken.isNotBlank()) {
            events(LeadsEvent.GetLeads(systemToken, userCode))
        }
    }

    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(listState, leadsState.leads.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && leadsState.hasMore && !leadsState.isLoadingMore) {
                events(LeadsEvent.LoadNextPage(systemToken, userCode))
            }
        }
    }

    DefaultUiScreen(
        queue = leadsState.errorQueue,
        onRemoveHeadFromQueue = { events(LeadsEvent.OnRemoveHeadFromQueue) },
        progressBarState = leadsState.progressBarState,
    ) {
        val colors = MaterialTheme.colorScheme

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DashboardBackground)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    events(LeadsEvent.SearchQueryChanged(it, systemToken, userCode))
                },
                placeholder = { Text("Search leads...") },
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

            // Filter chips row
            LazyRowFilterChips(
                selectedStatusId = leadsState.selectedStatusId,
                onSelect = { statusId ->
                    events(LeadsEvent.StatusFilterChanged(statusId, systemToken, userCode))
                },
            )

            Spacer(Modifier.height(12.dp))

            if (leadsState.leads.isEmpty() && leadsState.progressBarState == ProgressBarState.Idle) {
                EmptyLeadsState()
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    itemsIndexed(leadsState.leads, key = { _, lead -> lead.id }) { _, lead ->
                        LeadCard(
                            lead = lead,
                            onClick = { onLeadClick(lead) },
                            onMenuClick = { onLeadMenuClick(lead) },
                        )
                    }
                    if (leadsState.isLoadingMore) {
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
private fun LazyRowFilterChips(selectedStatusId: Int?, onSelect: (Int?) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        statusFilters.forEach { filter ->
            val selected = filter.id == selectedStatusId
            FilterChip(
                selected = selected,
                onClick = { onSelect(filter.id) },
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
}

@Composable
private fun LeadCard(lead: LeadDto, onClick: () -> Unit, onMenuClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val statusColor = statusColorFor(lead.leadStatus.statusName)
    val initials = "${lead.firstName.firstOrNull() ?: ' '}${lead.lastName.firstOrNull() ?: ' '}".uppercase()

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
                .background(statusColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${lead.firstName} ${lead.lastName}",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                StatusPill(label = lead.leadStatus.statusName, color = statusColor)
            }

            if (!lead.email.isNullOrBlank()) {
                Text(lead.email, fontSize = 12.5.sp, color = colors.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
            }
            if (!lead.mobile.isNullOrBlank()) {
                Text(lead.mobile, fontSize = 12.5.sp, color = colors.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
            }
            Text(
                "Stage: ${lead.leadStatus.statusName}",
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

@Composable
private fun EmptyLeadsState() {
    val colors = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
        Text("No leads found.", fontSize = 13.sp, color = colors.onSurface.copy(alpha = 0.5f))
    }
}

/**
 * Categorical color per status name — same principle as the dashboard's
 * widget-code color mapping. Add new statuses here as the backend introduces
 * them; unmapped names fall back to a neutral blue rather than crashing.
 */
private fun statusColorFor(statusName: String): Color = when (statusName.lowercase()) {
    "new" -> Color(0xFF7C4DFF)
    "contacted" -> Color(0xFF2196F3)
    "qualified" -> Color(0xFF4CAF50)
    "proposal" -> Color(0xFFFF9800)
    "closed won" -> Color(0xFF7C4DFF)
    "closed lost" -> Color(0xFFDC2626)
    else -> Color(0xFF607D8B)
}