package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.presentation.theme.parseHexColor

/**
 * Overlay rendered above everything — scrim + stacked action cards + the FAB
 * itself (morphs between + and X). Call this once at the DashboardScreen
 * level so it floats above the shared bottom bar and top bar too.
 */
@Composable
fun QuickActionOverlay(
    isOpen: Boolean,
    actions: List<QuickActionDTO>,
    onToggle: () -> Unit,
    onActionClick: (QuickActionDTO) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onToggle),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnimatedVisibility(
                visible = isOpen,
                enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 4 },
                exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 4 },
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    actions.forEach { action ->
                        QuickActionRow(action = action, onClick = {
                            onToggle()
                            onActionClick(action)
                        })
                    }
                }
            }

            FloatingActionButton(
                onClick = onToggle,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    if (isOpen) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (isOpen) "Close quick actions" else "Quick actions",
                )
            }
        }
    }
}

@Composable
private fun QuickActionRow(action: QuickActionDTO, onClick: () -> Unit) {
    val accent = parseHexColor(action.color)

    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(accent.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(iconForQuickAction(action.icon), contentDescription = action.actionName, tint = accent, modifier = Modifier.size(18.dp))
        }
        Text(
            action.actionName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
        )
    }
}

/**
 * Maps quick-action icon names (snake_case Material names, e.g. "person_add",
 * "task_alt") to real Compose icons. Separate mapping table from the dashboard
 * widgets' iconForName since the backend uses a different naming convention here.
 */
private fun iconForQuickAction(icon: String?): ImageVector = when (icon) {
    "person_add" -> Icons.Default.PersonAdd
    "task_alt" -> Icons.Default.TaskAlt
    "call" -> Icons.Default.Call
    "event" -> Icons.Default.Event
    "note_add", "description" -> Icons.Default.Description
    else -> Icons.Default.Add
}