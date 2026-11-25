package com.example.kmmexlvistech.presentation.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.ContactDto
import com.example.kmmexlvistech.presentation.ui.accounts.ActiveDot
import com.example.kmmexlvistech.presentation.ui.accounts.FieldLabel
import com.example.kmmexlvistech.presentation.ui.accounts.PickerField
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsEvent
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    state: ContactsState,
    events: (ContactsEvent) -> Unit,
    systemToken: String,
    onMenuClick: () -> Unit,
    onContactClick: (ContactDto) -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (ContactDto) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var showFilters by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<ContactDto?>(null) }
    var searchText by remember { mutableStateOf(state.searchQuery) }
    val listState = rememberLazyListState()
    val searchFocus = remember { FocusRequester() }
    val visible = state.visibleContacts

    LaunchedEffect(systemToken) {
        if (systemToken.isNotBlank()) {
            events(ContactsEvent.LoadLookups(systemToken))
            events(ContactsEvent.GetContacts(systemToken))
        }
    }
    LaunchedEffect(state.contactDeleted) { if (state.contactDeleted) events(ContactsEvent.ResetDeleteFlag) }

    LaunchedEffect(listState, state.contacts.size) {
        snapshotFlow {
            val info = listState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && state.hasMore && !state.isLoadingMore && systemToken.isNotBlank()) {
                events(ContactsEvent.LoadNextPage(systemToken))
            }
        }
    }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(ContactsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isDeleting) ProgressBarState.Loading else state.progressBarState,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Contacts", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { runCatching { searchFocus.requestFocus() } }) { Icon(Icons.Default.Search, "Search") }
                    IconButton(onClick = onCreateClick) { Icon(Icons.Default.Add, "Create contact") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary, titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary, actionIconContentColor = colors.onPrimary,
                ),
            )

            Column(Modifier.fillMaxWidth().weight(1f).background(colors.background)) {
                Spacer(Modifier.height(12.dp))

                // ---------- search + filter ----------
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it; events(ContactsEvent.SearchQueryChanged(it, systemToken)) },
                        placeholder = { Text("Search contacts...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = if (searchText.isNotEmpty()) {
                            { IconButton(onClick = { searchText = ""; events(ContactsEvent.SearchQueryChanged("", systemToken)) }) {
                                Icon(Icons.Default.Close, "Clear") } }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = contactFieldColors(),
                        modifier = Modifier.weight(1f).focusRequester(searchFocus),
                    )
                    IconButton(onClick = { showFilters = true }) {
                        Icon(
                            Icons.Default.FilterAlt, "Filters",
                            tint = if (state.hasActiveFilters) colors.primary else colors.onSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ---------- quick chips ----------
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterPill("All", !state.primaryOnly && state.isActiveFilter == true) {
                        events(ContactsEvent.QuickFilterSelected(false, true, systemToken))
                    }
                    FilterPill("Primary", state.primaryOnly) {
                        events(ContactsEvent.QuickFilterSelected(true, true, systemToken))
                    }
                    FilterPill("Inactive", !state.primaryOnly && state.isActiveFilter == false) {
                        events(ContactsEvent.QuickFilterSelected(false, false, systemToken))
                    }
                }

                // ---------- total + sort ----------
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        if (state.primaryOnly) "${visible.size} Primary Contacts" else "Total ${state.totalRecords} Contacts",
                        fontSize = 13.sp, color = colors.onSurfaceVariant, modifier = Modifier.weight(1f),
                    )
                    Row(
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .clickable { events(ContactsEvent.ToggleSort(systemToken)) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Name", fontSize = 13.sp, color = colors.onSurface)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.SwapVert, "Toggle sort order", tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }

                // ---------- list / empty ----------
                if (visible.isEmpty() && state.progressBarState == ProgressBarState.Idle && !state.isLoadingMore) {
                    EmptyContacts(onCreateClick)
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    ) {
                        items(visible, key = { it.id }) { contact ->
                            ContactCard(contact, { onContactClick(contact) }, { onEditClick(contact) }, { pendingDelete = contact })
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
        ContactFilterSheet(
            state = state,
            onDismiss = { showFilters = false },
            onApply = { search, accountName, active, ascending ->
                showFilters = false
                searchText = search
                events(ContactsEvent.ApplyFilters(systemToken, search, accountName, active, ascending))
            },
        )
    }

    pendingDelete?.let { contact ->
        ConfirmDeleteDialog(
            title = "Delete Contact?",
            message = "Are you sure you want to delete this contact? This action cannot be undone.",
            onConfirm = {
                pendingDelete = null
                events(ContactsEvent.DeleteContact(systemToken, contact.id))
            },
            onDismiss = { pendingDelete = null },
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
            .clickable(onClick = onClick).padding(horizontal = 18.dp),
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
private fun ContactCard(contact: ContactDto, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    var menuOpen by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth().clip(shape).background(c.surface)
            .border(1.dp, c.outlineVariant.copy(alpha = 0.6f), shape)
            .clickable(onClick = onClick).padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.size(50.dp).background(c.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Text(contact.initials, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = c.onPrimaryContainer)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    contact.displayName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = c.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                contact.roleLabel?.let { Text(it, fontSize = 12.5.sp, color = c.onSurfaceVariant) }
                contact.accountLabel?.let {
                    Text(
                        it, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = c.onSurface,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp),
                    )
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
        Spacer(Modifier.height(8.dp))
        contact.email?.takeIf { it.isNotBlank() }?.let { InfoLine(Icons.Default.Email, it, link = true) }
        (contact.mobile ?: contact.phone)?.takeIf { it.isNotBlank() }?.let { InfoLine(Icons.Default.Call, it) }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (contact.isPrimaryContact) {
                StatusChip("Primary", ChipKind.GREEN)
                Spacer(Modifier.width(12.dp))
            }
            ActiveDot(contact.isActive)
        }
    }
}

@Composable
private fun InfoLine(icon: ImageVector, text: String, link: Boolean = false) {
    val c = MaterialTheme.colorScheme
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = c.onSurfaceVariant, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text, fontSize = 13.5.sp, color = if (link) c.primary else c.onSurface,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EmptyContacts(onAddClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().padding(top = 48.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(84.dp).background(c.primaryContainer.copy(alpha = 0.6f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PersonSearch, null, tint = c.onPrimaryContainer, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("No contacts found", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = c.onSurface)
        Spacer(Modifier.height(6.dp))
        Text(
            "Try adjusting your search or filters or add a new contact.",
            fontSize = 13.sp, color = c.onSurfaceVariant, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onAddClick, shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary),
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Add Contact", fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactFilterSheet(
    state: ContactsState,
    onDismiss: () -> Unit,
    onApply: (search: String, accountName: String?, active: Boolean?, ascending: Boolean) -> Unit,
) {
    val c = MaterialTheme.colorScheme
    var search by remember { mutableStateOf(state.searchQuery) }
    var account by remember { mutableStateOf(state.selectedAccountName) }
    var active by remember { mutableStateOf(state.isActiveFilter) }
    var ascending by remember { mutableStateOf(state.sortAscending) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = c.surface,
    ) {
        Column(
            Modifier.fillMaxWidth().fillMaxHeight(0.92f).padding(horizontal = 16.dp).navigationBarsPadding(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                Text("Filters", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = c.onSurface, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                TextButton(onClick = { search = ""; account = null; active = true; ascending = true }) { Text("Reset") }
            }

            FieldLabel("Search")
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                placeholder = { Text("Search contacts...") }, singleLine = true,
                shape = RoundedCornerShape(10.dp), colors = contactFieldColors(), modifier = Modifier.fillMaxWidth(),
            )

            FieldLabel("Account")
            PickerField("Select account", state.accountOptions.map { it.label }, account) {
                account = state.accountOptions[it].label
            }

            FieldLabel("Status")
            PickerField(
                "Status", listOf("Active", "Inactive", "All"),
                when (active) { true -> "Active"; false -> "Inactive"; null -> "All" },
            ) { active = when (it) { 0 -> true; 1 -> false; else -> null } }

            FieldLabel("Sort By")
            PickerField(
                "Sort by", listOf("First Name (A – Z)", "First Name (Z – A)"),
                if (ascending) "First Name (A – Z)" else "First Name (Z – A)",
            ) { ascending = it == 0 }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onApply(search.trim(), account, active, ascending) },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 0.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary),
            ) { Text("Apply Filters", fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(12.dp))
        }
    }
}