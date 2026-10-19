package com.example.kmmexlvistech.presentation.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.AccountDto
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate

// imports: as above, plus androidx.compose.ui.platform.LocalUriHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    state: AccountsState,
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
                title = { Text("Account Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onEditClick, enabled = state.account != null) { Icon(Icons.Default.Edit, "Edit") }
                    IconButton(onClick = { showDelete = true }, enabled = state.account != null) {
                        Box(Modifier.background(c.error, RoundedCornerShape(8.dp)).padding(6.dp)) {
                            Icon(Icons.Default.Delete, "Delete", tint = c.onError, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = c.primary, titleContentColor = c.onPrimary,
                    navigationIconContentColor = c.onPrimary, actionIconContentColor = c.onPrimary,
                ),
            )

            Box(Modifier.fillMaxWidth().weight(1f).background(c.background)) {
                val a = state.account
                when {
                    state.detailsError != null -> Text(state.detailsError, color = c.error, modifier = Modifier.align(
                        Alignment.Center))
                    a != null -> AccountDetailsContent(a)
                    !state.isLoadingDetails -> Text("No account found", color = c.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete this account? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDelete = false; onDeleteConfirmed() }) {
                    Text("Delete", color = c.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun AccountDetailsContent(a: AccountDto) {
    val c = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    val shape = RoundedCornerShape(16.dp)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(72.dp).background(c.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Business, null, tint = c.onPrimaryContainer, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(a.accountName ?: "-", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = c.onBackground)
                Text(a.accountCode ?: "", fontSize = 13.sp, color = c.onSurfaceVariant)
                Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    a.accountType?.typeName?.takeIf { it.isNotBlank() }?.let { TypePill(it) }
                    Spacer(Modifier.width(10.dp))
                    ActiveDot(a.isActive)
                }
            }
        }

        Column(
            Modifier.fillMaxWidth().clip(shape).background(c.surface)
                .border(1.dp, c.outlineVariant.copy(alpha = 0.6f), shape).padding(horizontal = 16.dp),
        ) {
            val rows = listOf(
                Triple(Icons.Default.Business, "Account Name", a.accountName),
                Triple(Icons.Default.Tag, "Account Code", a.accountCode),
                Triple(Icons.Default.Label, "Account Type", a.accountType?.typeName),
                Triple(Icons.Default.Domain, "Industry", a.industry?.industryName),
                Triple(Icons.Default.Language, "Website", a.website),
                Triple(Icons.Default.Person, "Owner", a.owner?.fullName?.let { n -> "$n (${a.owner.userId})" }),
                Triple(Icons.Default.CalendarMonth, "Created Date", a.createdDateTime?.let { formatDate(it) }),
                Triple(Icons.Default.CalendarMonth, "Modified Date", a.modifiedDateTime?.let { formatDate(it) }),
            )
            rows.forEachIndexed { i, (icon, label, value) ->
                if (i > 0) HorizontalDivider(color = c.outlineVariant.copy(alpha = 0.6f))
                val isLink = label == "Website" && !value.isNullOrBlank()
                Row(
                    Modifier.fillMaxWidth()
                        .then(if (isLink) Modifier.clickable { runCatching { uriHandler.openUri(value!!) } } else Modifier)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(icon, null, tint = c.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(label, fontSize = 12.sp, color = c.onSurfaceVariant)
                        Text(
                            value?.takeIf { it.isNotBlank() } ?: "-",
                            fontSize = 14.sp, fontWeight = FontWeight.Medium,
                            color = if (isLink) c.primary else c.onSurface,
                        )
                    }
                }
            }
        }
    }
}