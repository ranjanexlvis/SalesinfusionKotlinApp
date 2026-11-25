package com.example.kmmexlvistech.presentation.ui.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.datasource.services.ActivityType
import com.example.kmmexlvistech.business.datasource.services.ChartItemDto
import com.example.kmmexlvistech.business.datasource.services.DashboardData
import com.example.kmmexlvistech.business.datasource.services.FilterLookups
import com.example.kmmexlvistech.business.datasource.services.IdLabel
import com.example.kmmexlvistech.business.datasource.services.KpiDto
import com.example.kmmexlvistech.business.datasource.services.PriorityOptions
import com.example.kmmexlvistech.business.datasource.services.ReportFilters
import com.example.kmmexlvistech.presentation.theme.CardWhite
import com.example.kmmexlvistech.presentation.theme.ChartPalette
import com.example.kmmexlvistech.presentation.theme.DangerBg
import com.example.kmmexlvistech.presentation.theme.DangerRed
import com.example.kmmexlvistech.presentation.theme.PaperBg
import com.example.kmmexlvistech.presentation.theme.Primary
import com.example.kmmexlvistech.presentation.theme.PrimaryDeep
import com.example.kmmexlvistech.presentation.theme.PrimaryLight
import com.example.kmmexlvistech.presentation.theme.PriorityColors
import com.example.kmmexlvistech.presentation.theme.Saffron
import com.example.kmmexlvistech.presentation.theme.SaffronLight
import com.example.kmmexlvistech.presentation.theme.SuccessBg
import com.example.kmmexlvistech.presentation.theme.SuccessGreen
import com.example.kmmexlvistech.presentation.theme.TextDark
import com.example.kmmexlvistech.presentation.theme.TextGrey
import com.example.kmmexlvistech.presentation.ui.report.viewmodel.ActivityReportEvent
import com.example.kmmexlvistech.presentation.ui.report.viewmodel.ActivityReportViewModel

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
@Composable
fun ActivityReportScreen(
    token: String,
    lookups: FilterLookups = FilterLookups(),
    viewModel: ActivityReportViewModel = koinInject(),
    onBack: () -> Unit = {},
    onViewReport: (ActivityType, ReportFilters) -> Unit = { _, _ -> }
) {
    val state = viewModel.state
    val tab = state.current
    var showFilters by remember { mutableStateOf(false) }
    var summaryExpanded by remember { mutableStateOf(true) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(token) { viewModel.onEvent(ActivityReportEvent.Load(token)) }
    LaunchedEffect(tab.error) {
        tab.error?.let {
            snackbar.showSnackbar(it)
            viewModel.onEvent(ActivityReportEvent.ErrorShown)
        }
    }

    Scaffold(
        containerColor = PaperBg,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(PrimaryDeep)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                Text("Activity Reports", Modifier.weight(1f), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = { showFilters = true }) { Icon(Icons.Default.FilterAlt, null, tint = Color.White) }
            }
        },
        bottomBar = {
            Button(
                onClick = { onViewReport(state.selected, tab.filters) },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Default.BarChart, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("View Report", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Segmented tabs
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(CardWhite, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                ActivityType.entries.forEach { t ->
                    val sel = t == state.selected
                    Box(
                        Modifier
                            .weight(1f)
                            .background(if (sel) Primary else Color.Transparent, RoundedCornerShape(20.dp))
                            .clickable { viewModel.onEvent(ActivityReportEvent.SelectTab(t, token)) }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(t.tabLabel, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (sel) Color.White else TextDark)
                    }
                }
            }

            Box(Modifier.weight(1f)) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    // Date + filters chips
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFD6DCE8), RoundedCornerShape(10.dp))
                                .background(CardWhite, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarToday, null, tint = Primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("${tab.filters.fromDate} – ${tab.filters.toDate}", fontSize = 12.sp, color = TextDark)
                        }
                        Spacer(Modifier.width(10.dp))
                        Row(
                            Modifier
                                .background(PrimaryLight, RoundedCornerShape(10.dp))
                                .clickable { showFilters = true }
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FilterAlt, null, tint = Primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Filters (${tab.filters.activeCount})", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable { summaryExpanded = !summaryExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(state.selected.summaryTitle, Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Icon(if (summaryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = TextDark)
                    }
                    Spacer(Modifier.height(10.dp))

                    val data = tab.data
                    if (data == null && !tab.isLoading) {
                        Text("No data", color = TextGrey, modifier = Modifier.padding(24.dp))
                    }
                    if (data != null) {
                        KpiGrid(state.selected, data, summaryExpanded)
                        Spacer(Modifier.height(16.dp))
                        ChartCard("${state.selected.tabLabel}s by Status") {
                            StatusDonut(state.selected.centerLabel, data.total, data.charts.byStatus)
                        }
                        Spacer(Modifier.height(12.dp))
                        ChartCard("${state.selected.tabLabel}s by Priority") {
                            PriorityBars(data.charts.byPriority)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
                if (tab.isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = Primary)
                }
            }
        }
    }

    if (showFilters) {
        FilterSheet(
            type = state.selected,
            initial = tab.filters,
            lookups = lookups,
            onDismiss = { showFilters = false },
            onApply = {
                showFilters = false
                viewModel.onEvent(ActivityReportEvent.ApplyFilters(it, token))
            }
        )
    }
}

/* ── KPI cards ───────────────────────────────────────────── */

private data class KpiStyle(val icon: ImageVector, val tint: Color, val bg: Color)

private fun kpiStyle(code: String): KpiStyle {
    val c = code.lowercase()
    return when {
        c.contains("total") -> KpiStyle(Icons.Default.Assignment, Color(0xFF2563EB), Color(0xFFE6EFFE))
        c.contains("complete") -> KpiStyle(Icons.Default.CheckCircleOutline, SuccessGreen, SuccessBg)
        c.contains("pending") || c.contains("scheduled") -> KpiStyle(Icons.Default.Schedule, Saffron, SaffronLight)
        c.contains("progress") || c.contains("resched") -> KpiStyle(Icons.Default.Sync, Color(0xFF7C5CE0), Color(0xFFEEE9FD))
        c.contains("cancel") -> KpiStyle(Icons.Default.Cancel, DangerRed, DangerBg)
        c.contains("high") -> KpiStyle(Icons.Default.Flag, Color(0xFF7C5CE0), Color(0xFFEEE9FD))
        c.contains("answer") -> KpiStyle(Icons.Default.PhoneMissed, Color(0xFF7C5CE0), Color(0xFFEEE9FD))
        else -> KpiStyle(Icons.Default.Info, Primary, PrimaryLight)
    }
}

@Composable
private fun KpiGrid(type: ActivityType, data: DashboardData, expanded: Boolean) {
    val totalCard = KpiDto("total", data.total, type.totalLabel)
    val all = listOf(totalCard) + data.kpis
    val visible = if (expanded) all else all.take(2)
    // 2-column grid without nesting a lazy grid inside a scroll column
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        visible.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { k -> KpiCard(k, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KpiCard(k: KpiDto, modifier: Modifier) {
    val s = kpiStyle(k.code.ifBlank { k.label })
    Row(
        modifier.background(s.bg, RoundedCornerShape(14.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(s.icon, null, tint = s.tint, modifier = Modifier.size(30.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text("${k.count}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text(k.label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

/* ── Charts ──────────────────────────────────────────────── */

@Composable
private fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun StatusDonut(centerLabel: String, total: Int, items: List<ChartItemDto>) {
    val sum = items.sumOf { item -> item.count }.coerceAtLeast(1)

    // Compute colors and sweeps outside the DrawScope
    val slices: List<Pair<Color, Float>> = items.mapIndexed { index, item ->
        val color = ChartPalette[index % ChartPalette.size]
        color to (360f * item.count / sum)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(110.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val strokePx = 22.dp.toPx()
                val inset = strokePx / 2f
                val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
                var startAngle = -90f

                slices.forEach { (sliceColor, sweep) ->
                    drawArc(
                        color = sliceColor,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                    )
                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$total", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Text(centerLabel, fontSize = 11.sp, color = TextGrey)
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { index, item ->
                val pct = item.percentage ?: (item.count * 100.0 / sum)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(9.dp)
                            .background(ChartPalette[index % ChartPalette.size], CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("${item.label} (${item.count})", fontSize = 12.sp, color = TextDark)
                    Spacer(Modifier.width(10.dp))
                    Text("${pct.toInt()}%", fontSize = 12.sp, color = TextGrey)
                }
            }
        }
    }
}

@Composable
private fun PriorityBars(items: List<ChartItemDto>) {
    val max = (items.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
    Row(
        Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        items.forEachIndexed { i, it ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                Text("${it.count}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .width(38.dp)
                        .height((100.dp * it.count / max).coerceAtLeast(4.dp))
                        .background(PriorityColors[i % PriorityColors.size], RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                Spacer(Modifier.height(4.dp))
                Text(it.label, fontSize = 11.sp, color = TextGrey)
            }
        }
    }
}

/* ═════════════════════════════════════════════════════════════
 * 6. Filter bottom sheet
 * ═════════════════════════════════════════════════════════════ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(
    type: ActivityType,
    initial: ReportFilters,
    lookups: FilterLookups,
    onDismiss: () -> Unit,
    onApply: (ReportFilters) -> Unit
) {
    var f by remember { mutableStateOf(initial) }
    var showDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = CardWhite) {
        Column(Modifier.fillMaxHeight(0.95f)) {
            Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = TextDark) }
                Text(type.filterTitle, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                TextButton(onClick = {
                    f = f.copy(
                        search = "", statusIds = emptyList(), priorityIds = emptyList(), assignedToIds = emptyList(),
                        leadIds = emptyList(), accountIds = emptyList(), contactIds = emptyList()
                    )
                }) { Text("Reset", color = Primary) }
            }

            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FieldLabel("Search")
                OutlinedTextField(
                    value = f.search, onValueChange = { f = f.copy(search = it) },
                    placeholder = { Text(type.searchHint) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                )

                FieldLabel("Date Range", required = true)
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("${f.fromDate} – ${f.toDate}", Modifier.weight(1f), color = TextDark)
                    Icon(Icons.Default.ChevronRight, null)
                }

                MultiSelect("Status", type.statusOptions, f.statusIds, "Select status") { f = f.copy(statusIds = it) }
                MultiSelect("Priority", PriorityOptions, f.priorityIds, "Select priority") { f = f.copy(priorityIds = it) }
                MultiSelect("Assigned To", lookups.users, f.assignedToIds, "Select users") { f = f.copy(assignedToIds = it) }
                MultiSelect("Lead", lookups.leads, f.leadIds, "Select leads") { f = f.copy(leadIds = it) }
                MultiSelect("Account", lookups.accounts, f.accountIds, "Select accounts") { f = f.copy(accountIds = it) }
                MultiSelect("Contact", lookups.contacts, f.contactIds, "Select contacts") { f = f.copy(contactIds = it) }
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = { onApply(f) },
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Apply Filters (${f.activeCount})", fontWeight = FontWeight.SemiBold, color = Color.White) }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    enabled = pickerState.selectedStartDateMillis != null && pickerState.selectedEndDateMillis != null,
                    onClick = {
                        val from = pickerState.selectedStartDateMillis!!.toIsoDate()
                        val to = pickerState.selectedEndDateMillis!!.toIsoDate()
                        f = f.copy(fromDate = from, toDate = to)
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DateRangePicker(state = pickerState, modifier = Modifier.height(500.dp))
        }
    }
}

private fun Long.toIsoDate(): String =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date.toString()

@Composable
private fun FieldLabel(text: String, required: Boolean = false) {
    Row {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        if (required) Text(" *", color = DangerRed, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiSelect(
    label: String,
    options: List<IdLabel>,
    selected: List<Int>,
    placeholder: String,
    onChange: (List<Int>) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color(0xFFD6DCE8)), RoundedCornerShape(10.dp))
                    .clickable { open = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (selected.isEmpty()) Text(placeholder, color = TextGrey, fontSize = 14.sp)
                    options.filter { it.id in selected }.forEach { o ->
                        Row(
                            Modifier.background(PrimaryLight, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(o.label, fontSize = 12.sp, color = Primary)
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Close, null, tint = Primary,
                                modifier = Modifier.size(14.dp).clickable { onChange(selected - o.id) }
                            )
                        }
                    }
                }
                Icon(Icons.Default.KeyboardArrowDown, null, tint = TextDark)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                if (options.isEmpty()) DropdownMenuItem(text = { Text("No options") }, onClick = { open = false })
                options.forEach { o ->
                    val checked = o.id in selected
                    DropdownMenuItem(
                        text = { Text(o.label) },
                        leadingIcon = { Checkbox(checked = checked, onCheckedChange = null) },
                        onClick = { onChange(if (checked) selected - o.id else selected + o.id) }
                    )
                }
            }
        }
    }
}