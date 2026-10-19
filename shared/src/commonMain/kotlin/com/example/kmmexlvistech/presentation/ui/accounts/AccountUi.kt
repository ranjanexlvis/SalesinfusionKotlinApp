package com.example.kmmexlvistech.presentation.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data class AcctTone(val fg: Color, val bg: Color)

@Composable
internal fun acctTone(positive: Boolean): AcctTone {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (positive) {
        if (dark) AcctTone(Color(0xFF86EFAC), Color(0xFF14352A)) else AcctTone(Color(0xFF16A34A), Color(0xFFE3F6EA))
    } else {
        if (dark) AcctTone(Color(0xFFFCA5A5), Color(0xFF3F1D1D)) else AcctTone(Color(0xFFDC2626), Color(0xFFFEE2E2))
    }
}

@Composable
internal fun TypePill(text: String) {
    val t = acctTone(true)
    Box(Modifier.background(t.bg, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 3.dp)) {
        Text(text, color = t.fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
internal fun ActiveDot(active: Boolean) {
    val t = acctTone(active)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(t.fg, androidx.compose.foundation.shape.CircleShape))
        Spacer(Modifier.width(5.dp))
        Text(if (active) "Active" else "Inactive", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun FieldLabel(text: String, required: Boolean = false) {
    val c = MaterialTheme.colorScheme
    Row(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
        if (required) Text(" *", fontSize = 13.sp, color = c.error)
    }
}

@Composable
internal fun accountFieldColors() = MaterialTheme.colorScheme.let { c ->
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = c.onSurface, unfocusedTextColor = c.onSurface, errorTextColor = c.onSurface,
        focusedContainerColor = c.surface, unfocusedContainerColor = c.surface, errorContainerColor = c.surface,
        focusedBorderColor = c.primary, unfocusedBorderColor = c.outlineVariant, errorBorderColor = c.error,
        focusedPlaceholderColor = c.onSurfaceVariant, unfocusedPlaceholderColor = c.onSurfaceVariant,
        focusedLeadingIconColor = c.primary, unfocusedLeadingIconColor = c.onSurfaceVariant,
        cursorColor = c.primary, errorSupportingTextColor = c.error,
    )
}

/** Selected label is derived by the caller on every recomposition, so late-loaded options still show correctly. */
@Composable
internal fun PickerField(
    placeholder: String,
    labels: List<String>,
    selectedLabel: String?,
    error: String? = null,
    onSelect: (Int) -> Unit,
) {
    val c = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Column(Modifier.fillMaxWidth()) {
        Box {
            Row(
                Modifier.fillMaxWidth().height(54.dp)
                    .background(c.surface, shape)
                    .border(1.dp, if (error != null) c.error else c.outlineVariant, shape)
                    .clickable(enabled = labels.isNotEmpty()) { expanded = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    selectedLabel ?: placeholder,
                    color = if (selectedLabel != null) c.onSurface else c.onSurfaceVariant,
                    fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = c.onSurfaceVariant)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                labels.forEachIndexed { i, label ->
                    DropdownMenuItem(text = { Text(label) }, onClick = { expanded = false; onSelect(i) })
                }
            }
        }
        error?.let { Text(it, color = c.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp)) }
    }
}