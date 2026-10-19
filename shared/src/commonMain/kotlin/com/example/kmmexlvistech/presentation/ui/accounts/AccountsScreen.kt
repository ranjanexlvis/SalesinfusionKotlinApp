package com.example.kmmexlvistech.presentation.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.AccountDto
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsEvent
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate
// + AccountsState / AccountsEvent imports

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    state: AccountsState,
    events: (AccountsEvent) -> Unit,
    systemToken: String,
    onMenuClick: () -> Unit,
    onAccountClick: (AccountDto) -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (AccountDto) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var showFilters by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<AccountDto?>(null) }
    var searchText by remember { mutableStateOf(state.searchQuery) }
    val listState = rememberLazyListState()

    LaunchedEffect(systemToken) {
        if (systemToken.isNotBlank()) {
            events(AccountsEvent.LoadLookups(systemToken))
            events(AccountsEvent.GetAccounts(systemToken))
        }
    }
    // a delete started from the list menu must not leave the flag set for the details screen
    LaunchedEffect(state.accountDeleted) { if (state.accountDeleted) events(AccountsEvent.ResetDeleteFlag) }

    LaunchedEffect(listState, state.accounts.size) {
        snapshotFlow {
            val info = listState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && state.hasMore && !state.isLoadingMore && systemToken.isNotBlank()) {
                events(AccountsEvent.LoadNextPage(systemToken))
            }
        }
    }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(AccountsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isDeleting) ProgressBarState.Loading else state.progressBarState,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Accounts", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
                actions = {
                    IconButton(onClick = { showFilters = true }) { Icon(Icons.Default.Search, "Search and filter") }
                    IconButton(onClick = onCreateClick) { Icon(Icons.Default.Add, "Create account") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary, titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary, actionIconContentColor = colors.onPrimary,
                ),
            )

            Column(Modifier.fillMaxWidth().weight(1f).background(colors.background)) {
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it; events(AccountsEvent.SearchQueryChanged(it, systemToken)) },
                    placeholder = { Text("Search accounts...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = if (searchText.isNotEmpty()) {
                        { IconButton(onClick = { searchText = ""; events(AccountsEvent.SearchQueryChanged("", systemToken)) }) {
                            Icon(Icons.Default.Close, "Clear") } }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = accountFieldColors(),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(10.dp))

                // type chips come from the Account Types API
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        FilterPill("All", state.selectedTypeName == null) {
                            events(AccountsEvent.TypeChipSelected(null, systemToken))
                        }
                    }
                    items(state.typeOptions, key = { it.id }) { opt ->
                        FilterPill(opt.label, state.selectedTypeName == opt.label) {
                            events(AccountsEvent.TypeChipSelected(opt.label, systemToken))
                        }
                    }
                    item {
                        IconButton(onClick = { showFilters = true }) {
                            Icon(
                                Icons.Default.FilterAlt, "Filters",
                                tint = if (state.hasActiveFilters) colors.primary else colors.onSurfaceVariant,
                            )
                        }
                    }
                }

                Text(
                    "Total ${state.totalRecords} Accounts",
                    fontSize = 13.sp, color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )

                if (state.accounts.isEmpty() && state.progressBarState == ProgressBarState.Idle) {
                    Column(Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Business, null, tint = colors.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No accounts found.", fontSize = 13.sp, color = colors.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    ) {
                        items(state.accounts, key = { it.id }) { account ->
                            AccountCard(account, { onAccountClick(account) }, { onEditClick(account) }, { pendingDelete = account })
                        }
                        if (state.isLoadingMore) item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = colors.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        FilterSheet(
            state = state,
            onDismiss = { showFilters = false },
            onApply = { search, type, industry, active ->
                showFilters = false
                searchText = search
                events(AccountsEvent.ApplyFilters(systemToken, search, type, industry, active))
            },
        )
    }

    pendingDelete?.let { acct ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete Account") },
            text = { Text("Delete \"${acct.accountName.orEmpty()}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    pendingDelete = null
                    events(AccountsEvent.DeleteAccount(systemToken, acct.id))
                }) { Text("Delete", color = colors.error, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Box(
        Modifier.height(34.dp).clip(shape)
            .background(if (selected) c.primary else c.surface)
            .border(1.dp, if (selected) c.primary else c.outlineVariant, shape)
            .clickable(onClick = onClick).padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label, fontSize = 12.5.sp, maxLines = 1,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) c.onPrimary else c.onSurfaceVariant,
        )
    }
}

@Composable
private fun AccountCard(account: AccountDto, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    var menuOpen by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth().clip(shape).background(c.surface)
            .border(1.dp, c.outlineVariant.copy(alpha = 0.6f), shape)
            .clickable(onClick = onClick).padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.size(48.dp).background(c.primaryContainer, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Business, null, tint = c.onPrimaryContainer)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    account.accountName ?: "-", fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    color = c.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Text(account.accountCode ?: "", fontSize = 12.5.sp, color = c.onSurfaceVariant)
                Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    account.accountType?.typeName?.takeIf { it.isNotBlank() }?.let { TypePill(it) }
                    Spacer(Modifier.width(10.dp))
                    ActiveDot(account.isActive)
                }
            }
            Box {
                IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.MoreVert, "More options", tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") }, leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { menuOpen = false; onEdit() },
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = c.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = c.error) },
                        onClick = { menuOpen = false; onDelete() },
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        account.industry?.industryName?.takeIf { it.isNotBlank() }?.let { InfoLine(Icons.Default.Domain, it) }
        account.owner?.fullName?.takeIf { it.isNotBlank() }?.let { InfoLine(Icons.Default.Person, it) }
        account.website?.takeIf { it.isNotBlank() }?.let { InfoLine(Icons.Default.Language, it, link = true) }
        Spacer(Modifier.height(8.dp))
        Text(
            account.modifiedDateTime?.let { "Modified ${formatDate(it)}" } ?: "Created ${formatDate(account.createdDateTime)}",
            fontSize = 12.sp, color = c.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoLine(icon: ImageVector, text: String, link: Boolean = false) {
    val c = MaterialTheme.colorScheme
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = c.onSurfaceVariant, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.5.sp, color = if (link) c.primary else c.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(
    state: AccountsState,
    onDismiss: () -> Unit,
    onApply: (search: String, type: String?, industry: String?, active: Boolean?) -> Unit,
) {
    val c = MaterialTheme.colorScheme
    var search by remember { mutableStateOf(state.searchQuery) }
    var type by remember { mutableStateOf(state.selectedTypeName) }
    var industry by remember { mutableStateOf(state.selectedIndustryName) }
    var active by remember { mutableStateOf(state.isActiveFilter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = c.surface,
    ) {
        Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp).navigationBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                Text("Filters", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = c.onSurface, modifier = Modifier.weight(1f))
                TextButton(onClick = { search = ""; type = null; industry = null; active = true }) { Text("Reset") }
            }

            FieldLabel("Search")
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                placeholder = { Text("Search accounts...") }, singleLine = true,
                shape = RoundedCornerShape(12.dp), colors = accountFieldColors(), modifier = Modifier.fillMaxWidth(),
            )

            FieldLabel("Account Type")
            PickerField("Select account type", state.typeOptions.map { it.label }, type) { type = state.typeOptions[it].label }

            FieldLabel("Industry")
            PickerField("Select industry", state.industryOptions.map { it.label }, industry) { industry = state.industryOptions[it].label }

            FieldLabel("Status")
            PickerField(
                "Status", listOf("Active", "Inactive", "All"),
                when (active) { true -> "Active"; false -> "Inactive"; null -> "All" },
            ) { active = when (it) { 0 -> true; 1 -> false; else -> null } }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onApply(search.trim(), type, industry, active) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary),
            ) { Text("Apply Filters", fontWeight = FontWeight.SemiBold) }
        }
    }
}