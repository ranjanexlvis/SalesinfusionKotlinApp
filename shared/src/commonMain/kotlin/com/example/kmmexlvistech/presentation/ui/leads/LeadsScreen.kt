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
    import androidx.compose.ui.graphics.luminance
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.kmmexlvistech.business.core.ProgressBarState
    import com.example.kmmexlvistech.business.datasource.dto.LeadDto
    import com.example.kmmexlvistech.presentation.theme.DashboardBackground
    import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
    import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsEvent
    import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsState
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.lazy.itemsIndexed
    import androidx.compose.foundation.text.BasicTextField
    import androidx.compose.material.icons.filled.*
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.SolidColor
    import androidx.compose.ui.graphics.luminance
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.text.style.TextOverflow

    private data class StatusFilter(val id: Int?, val label: String)

    private val statusFilters = listOf(
        StatusFilter(null, "All"),
        StatusFilter(1, "New"),
        StatusFilter(2, "Contacted"),
        StatusFilter(3, "Qualified"),
        StatusFilter(4, "Proposal"),
        // add more here, e.g. StatusFilter(5, "Closed Won"), StatusFilter(6, "Closed Lost")
    )

    @Composable
    fun LeadsScreen(
        leadsState: LeadsState,
        events: (LeadsEvent) -> Unit,
        systemToken: String,
        userCode: Int,
        onLeadClick: (LeadDto) -> Unit,
        onLeadMenuClick: (LeadDto) -> Unit,
        onCreateLeadClick: () -> Unit,
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

            Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(Modifier.height(12.dp))

                    // ---------- Search ----------
                    LeadsSearchBar(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            events(LeadsEvent.SearchQueryChanged(it, systemToken, userCode))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    Spacer(Modifier.height(12.dp))

                    // ---------- Scrollable filter chips ----------
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(statusFilters, key = { it.label }) { filter ->
                            FilterPill(
                                label = filter.label,
                                selected = filter.id == leadsState.selectedStatusId,
                                onClick = {
                                    events(LeadsEvent.StatusFilterChanged(filter.id, systemToken, userCode))
                                },
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ---------- List ----------
                    if (leadsState.leads.isEmpty() && leadsState.progressBarState == ProgressBarState.Idle) {
                        EmptyLeadsState()
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
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
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = colors.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = onCreateLeadClick,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create lead")
                }
            }
        }
    }

// ---------------- Search ----------------

    @Composable
    private fun LeadsSearchBar(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val colors = MaterialTheme.colorScheme
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surfaceVariant.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text("Search leads...", fontSize = 14.sp, color = colors.onSurfaceVariant)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (value.isNotEmpty()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Clear search",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable { onValueChange("") },
                )
            }
        }
    }

// ---------------- Chips ----------------

    @Composable
    private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
        val colors = MaterialTheme.colorScheme
        val shape = RoundedCornerShape(50)
        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(shape)
                .background(if (selected) colors.primary else colors.surface)
                .border(
                    width = 1.dp,
                    color = if (selected) colors.primary else colors.outlineVariant,
                    shape = shape,
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                label,
                fontSize = 12.5.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }

// ---------------- Card ----------------

    @Composable
    private fun LeadCard(lead: LeadDto, onClick: () -> Unit, onMenuClick: () -> Unit) {
        val colors = MaterialTheme.colorScheme
        val tone = statusTone(lead.leadStatus.statusName)
        val initials = "${lead.firstName.firstOrNull() ?: ' '}${lead.lastName.firstOrNull() ?: ' '}"
            .trim().uppercase()
        val shape = RoundedCornerShape(16.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.surface)
                .border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), shape)
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(tone.bg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(initials, color = tone.fg, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${lead.firstName} ${lead.lastName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusPill(label = lead.leadStatus.statusName, tone = tone)
                }

                // If LeadDto has a company field, add it here:
                // lead.companyName?.takeIf { it.isNotBlank() }?.let { InfoLine(Icons.Default.Business, it) }

                if (!lead.email.isNullOrBlank()) {
                    InfoLine(Icons.Default.Email, lead.email, Modifier.padding(top = 6.dp))
                }
                if (!lead.mobile.isNullOrBlank()) {
                    InfoLine(Icons.Default.Call, lead.mobile, Modifier.padding(top = 3.dp))
                }
                InfoLine(
                    Icons.Default.Flag,
                    "Stage: ${lead.leadStatus.statusName}",
                    Modifier.padding(top = 3.dp),
                )
            }

            IconButton(onClick = onMenuClick, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    @Composable
    private fun InfoLine(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
        val colors = MaterialTheme.colorScheme
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(13.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text,
                fontSize = 12.5.sp,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    @Composable
    private fun StatusPill(label: String, tone: StatusTone) {
        Box(
            modifier = Modifier
                .background(tone.bg, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp),
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = tone.fg, maxLines = 1)
        }
    }

    @Composable
    private fun EmptyLeadsState() {
        val colors = MaterialTheme.colorScheme
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.PersonSearch,
                contentDescription = null,
                tint = colors.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text("No leads found.", fontSize = 13.sp, color = colors.onSurfaceVariant)
        }
    }

// ---------------- Theme-aware status colors ----------------

    private data class StatusTone(val fg: Color, val bg: Color)

    @Composable
    private fun statusTone(statusName: String): StatusTone {
        val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
        return when (statusName.lowercase()) {
            "new", "closed won" ->
                if (dark) StatusTone(Color(0xFFC4B5FD), Color(0xFF2E2547)) else StatusTone(Color(0xFF7C3AED), Color(0xFFF1E8FF))
            "contacted" ->
                if (dark) StatusTone(Color(0xFF93C5FD), Color(0xFF172A4A)) else StatusTone(Color(0xFF2563EB), Color(0xFFE5EEFF))
            "qualified" ->
                if (dark) StatusTone(Color(0xFF86EFAC), Color(0xFF14352A)) else StatusTone(Color(0xFF16A34A), Color(0xFFE3F6EA))
            "proposal" ->
                if (dark) StatusTone(Color(0xFFFCD34D), Color(0xFF3A2F10)) else StatusTone(Color(0xFFD97706), Color(0xFFFEF3C7))
            "closed lost" ->
                if (dark) StatusTone(Color(0xFFFCA5A5), Color(0xFF3F1D1D)) else StatusTone(Color(0xFFDC2626), Color(0xFFFEE2E2))
            else ->
                if (dark) StatusTone(Color(0xFFB0BEC5), Color(0xFF263238)) else StatusTone(Color(0xFF546E7A), Color(0xFFECEFF1))
        }
    }