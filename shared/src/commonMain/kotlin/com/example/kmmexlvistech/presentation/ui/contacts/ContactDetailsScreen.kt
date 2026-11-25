package com.example.kmmexlvistech.presentation.ui.contacts

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.ContactDto
import com.example.kmmexlvistech.presentation.ui.accounts.acctTone
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate
// + ContactsState import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    state: ContactsState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onRemoveHeadFromQueue: () -> Unit = {},
    onTryAgain: () -> Unit = {},
) {
    val c = MaterialTheme.colorScheme
    var showDelete by remember { mutableStateOf(false) }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
        progressBarState = if (state.isLoadingDetails || state.isDeleting) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = onTryAgain,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Contact Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onEditClick, enabled = state.contact != null) { Icon(Icons.Default.Edit, "Edit") }
                    IconButton(onClick = { showDelete = true }, enabled = state.contact != null) { Icon(Icons.Default.Delete, "Delete") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = c.primary, titleContentColor = c.onPrimary,
                    navigationIconContentColor = c.onPrimary, actionIconContentColor = c.onPrimary,
                ),
            )

            Box(Modifier.fillMaxWidth().weight(1f).background(c.background)) {
                val contact = state.contact
                when {
                    state.detailsError != null -> Text(state.detailsError, color = c.error, modifier = Modifier.align(Alignment.Center))
                    contact != null -> ContactDetailsContent(contact)
                    !state.isLoadingDetails -> Text("No contact found", color = c.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    if (showDelete) {
        ConfirmDeleteDialog(
            title = "Delete Contact?",
            message = "Are you sure you want to delete this contact? This action cannot be undone.",
            onConfirm = { showDelete = false; onDeleteConfirmed() },
            onDismiss = { showDelete = false },
        )
    }
}

@Composable
private fun ContactDetailsContent(contact: ContactDto) {
    val c = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    val shape = RoundedCornerShape(16.dp)
    val amber = Color(0xFFF59E0B)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // ---------- header ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(72.dp).background(c.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Text(contact.initials, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = c.onPrimaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(contact.displayName, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = c.onBackground)
                contact.roleLabel?.let { Text(it, fontSize = 13.sp, color = c.onSurfaceVariant) }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip(if (contact.isActive) "Active" else "Inactive", if (contact.isActive) ChipKind.GREEN else ChipKind.RED)
                    StatusChip(if (contact.isPrimaryContact) "Primary" else "Not Primary",
                        if (contact.isPrimaryContact) ChipKind.GREEN else ChipKind.NEUTRAL)
                }
            }
        }

        // ---------- main details ----------
        DetailCard {
            DetailRow(
                icon = { Icon(Icons.Default.Business, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Account", value = contact.accountLabel, subValue = contact.account?.accountCode,
            )
            DetailDivider()
            DetailRow(
                icon = { Icon(Icons.Default.Email, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Email", value = contact.email, link = contact.email?.let { "mailto:$it" }, onOpen = { uriHandler.openUri(it) },
            )
            DetailDivider()
            DetailRow(
                icon = { Icon(Icons.Default.Phone, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Phone", value = contact.phone, link = contact.phone?.let { "tel:$it" }, onOpen = { uriHandler.openUri(it) },
            )
            DetailDivider()
            DetailRow(
                icon = { Icon(Icons.Default.Call, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Mobile", value = contact.mobile, link = contact.mobile?.let { "tel:$it" }, onOpen = { uriHandler.openUri(it) },
            )
            DetailDivider()
            DetailRow(
                icon = { Icon(Icons.Default.Groups, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Contact Role", value = contact.roleLabel,
            )
            DetailDivider()
            DetailRow(
                icon = { Icon(Icons.Default.Star, null, tint = amber, modifier = Modifier.size(20.dp)) },
                label = "Primary Contact", value = if (contact.isPrimaryContact) "Yes" else "No",
            )
            DetailDivider()
            DetailRow(
                icon = { Box(Modifier.size(14.dp).background(acctTone(contact.isActive).fg, CircleShape)) },
                label = "Status", value = if (contact.isActive) "Active" else "Inactive",
            )
        }

        // ---------- dates ----------
        DetailCard {
            DetailRow(
                icon = { Icon(Icons.Default.CalendarMonth, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Created Date", value = contact.createdDateTime?.let { formatDate(it) },
            )
            DetailDivider()
            DetailRow(
                icon = { Icon(Icons.Default.CalendarMonth, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                label = "Modified Date", value = contact.modifiedDateTime?.let { formatDate(it) },
            )
        }
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(167.dp)
    Column(
        Modifier.fillMaxWidth().clip(shape).background(c.surface)
            .border(1.dp, c.outlineVariant.copy(alpha = 0.6f), shape).padding(horizontal = 16.dp),
        content = content,
    )
}

@Composable
private fun DetailDivider() =
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

@Composable
private fun DetailRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String?,
    subValue: String? = null,
    link: String? = null,
    onOpen: (String) -> Unit = {},
) {
    val c = MaterialTheme.colorScheme
    val openable = link != null && !value.isNullOrBlank()
    Row(
        Modifier.fillMaxWidth()
            .then(if (openable) Modifier.clickable { runCatching { onOpen(link!!) } } else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(24.dp), contentAlignment = Alignment.Center) { icon() }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, fontSize = 12.sp, color = c.onSurfaceVariant)
            Text(
                value?.takeIf { it.isNotBlank() } ?: "-",
                fontSize = 14.sp, fontWeight = FontWeight.Medium,
                color = if (openable) c.primary else c.onSurface,
            )
            subValue?.takeIf { it.isNotBlank() }?.let { Text(it, fontSize = 12.sp, color = c.onSurfaceVariant) }
        }
    }
}