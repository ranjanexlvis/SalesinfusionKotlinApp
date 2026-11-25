package com.example.kmmexlvistech.presentation.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kmmexlvistech.presentation.ui.accounts.FieldLabel
import com.example.kmmexlvistech.presentation.ui.accounts.acctTone

internal enum class ChipKind { GREEN, RED, NEUTRAL }

@Composable
internal fun StatusChip(text: String, kind: ChipKind) {
    val c = MaterialTheme.colorScheme
    val fg = when (kind) {
        ChipKind.GREEN -> acctTone(true).fg
        ChipKind.RED -> acctTone(false).fg
        ChipKind.NEUTRAL -> c.onSurfaceVariant
    }
    val bg = when (kind) {
        ChipKind.GREEN -> acctTone(true).bg
        ChipKind.RED -> acctTone(false).bg
        ChipKind.NEUTRAL -> c.surfaceVariant
    }
    Box(Modifier.background(bg, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 3.dp)) {
        Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
internal fun contactFieldColors() = MaterialTheme.colorScheme.let { c ->
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = c.onSurface, unfocusedTextColor = c.onSurface, errorTextColor = c.onSurface,
        disabledTextColor = c.onSurfaceVariant,
        focusedContainerColor = c.surface, unfocusedContainerColor = c.surface, errorContainerColor = c.surface,
        disabledContainerColor = c.surfaceVariant.copy(alpha = 0.5f),
        focusedBorderColor = c.primary, unfocusedBorderColor = c.outlineVariant,
        errorBorderColor = c.error, disabledBorderColor = c.outlineVariant,
        focusedPlaceholderColor = c.onSurfaceVariant, unfocusedPlaceholderColor = c.onSurfaceVariant,
        focusedLeadingIconColor = c.onSurfaceVariant, unfocusedLeadingIconColor = c.onSurfaceVariant,
        disabledLeadingIconColor = c.onSurfaceVariant, errorLeadingIconColor = c.error,
        cursorColor = c.primary, errorSupportingTextColor = c.error,
    )
}

@Composable
internal fun ContactTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    icon: ImageVector? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    enabled: Boolean = true,
) {
    Column(modifier) {
        FieldLabel(label, required)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            enabled = enabled,
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
            leadingIcon = icon?.let { { Icon(it, contentDescription = null) } },
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it, fontSize = 11.sp) } },
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            colors = contactFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---------------- searchable single-select (Select Account / Select Contact Role) ----------------

internal data class SelectItem(val key: String, val title: String, val subtitle: String? = null)

@Composable
internal fun SelectField(
    label: String,
    required: Boolean,
    placeholder: String,
    sheetTitle: String,
    searchHint: String,
    items: List<SelectItem>,
    selectedKey: String?,
    fallbackLabel: String?,        // shown if the selected item isn't in `items` (e.g. outside the first 100 accounts)
    itemIcon: ImageVector,
    error: String? = null,
    onClear: (() -> Unit)? = null,
    onSelect: (String) -> Unit,
) {
    val c = MaterialTheme.colorScheme
    var open by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    // derived on every recomposition, so options that load late still show the right label
    val shown = items.firstOrNull { it.key == selectedKey }?.title
        ?: fallbackLabel?.takeIf { selectedKey != null }

    Column(Modifier.fillMaxWidth()) {
        FieldLabel(label, required)
        Row(
            Modifier.fillMaxWidth().height(54.dp)
                .background(c.surface, shape)
                .border(1.dp, if (error != null) c.error else c.outlineVariant, shape)
                .clickable { open = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                shown ?: placeholder,
                color = if (shown != null) c.onSurface else c.onSurfaceVariant,
                fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (shown != null && onClear != null) {
                Icon(
                    Icons.Default.Close, "Clear", tint = c.onSurfaceVariant,
                    modifier = Modifier.size(20.dp).clickable { onClear() },
                )
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Default.ExpandMore, null, tint = c.onSurfaceVariant)
        }
        error?.let { Text(it, color = c.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp)) }
    }

    if (open) {
        SelectSheet(
            title = sheetTitle, searchHint = searchHint, items = items, selectedKey = selectedKey,
            icon = itemIcon, onDismiss = { open = false },
            onSelect = { open = false; onSelect(it) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectSheet(
    title: String,
    searchHint: String,
    items: List<SelectItem>,
    selectedKey: String?,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val c = MaterialTheme.colorScheme
    var query by remember { mutableStateOf("") }
    val filtered = items.filter {
        query.isBlank() || it.title.contains(query, true) || (it.subtitle?.contains(query, true) == true)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = c.surface,
    ) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.9f).navigationBarsPadding()) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.Close, "Close")
                }
                Text(
                    title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = c.onSurface,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text(searchHint) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true, shape = RoundedCornerShape(10.dp), colors = contactFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )
            if (filtered.isEmpty()) {
                Text(
                    "No results", color = c.onSurfaceVariant, fontSize = 13.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                )
            }
            LazyColumn(Modifier.weight(1f)) {
                items(filtered, key = { it.key }) { item ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onSelect(item.key) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.size(38.dp).background(c.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Icon(icon, null, tint = c.onPrimaryContainer, modifier = Modifier.size(20.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                            item.subtitle?.takeIf { it.isNotBlank() }?.let {
                                Text(it, fontSize = 12.sp, color = c.onSurfaceVariant)
                            }
                        }
                        RadioButton(
                            selected = item.key == selectedKey, onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = c.primary, unselectedColor = c.outline),
                        )
                    }
                    HorizontalDivider(color = c.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

// ---------------- delete confirmation ----------------

@Composable
internal fun ConfirmDeleteDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = c.surface, contentColor = c.onSurface) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(56.dp).background(c.error.copy(alpha = 0.14f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Delete, null, tint = c.error, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = c.onSurface)
                Spacer(Modifier.height(8.dp))
                Text(message, fontSize = 13.sp, color = c.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                    ) { Text("Cancel") }
                    Button(
                        onClick = onConfirm, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = c.error, contentColor = c.onError),
                    ) { Text("Delete", fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}