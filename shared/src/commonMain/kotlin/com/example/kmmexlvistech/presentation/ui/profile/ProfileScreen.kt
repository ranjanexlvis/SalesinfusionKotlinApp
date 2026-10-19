package com.example.kmmexlvistech.presentation.ui.profile

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.models.*
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileEvent
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    events: (ProfileEvent) -> Unit,
    systemToken: String,
    stats: MyWorkDTO? = null,              // reused from the dashboard API; hidden when null
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit,
    onChangePasswordClick: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(systemToken) {
        if (systemToken.isNotBlank()) events(ProfileEvent.GetProfile(systemToken))
    }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(ProfileEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isLoading) ProgressBarState.Loading else ProgressBarState.Idle,
        onTryAgain = { if (systemToken.isNotBlank()) events(ProfileEvent.GetProfile(systemToken)) },
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                },
                actions = {
                    IconButton(onClick = onEditClick, enabled = state.profile != null) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary,
                    actionIconContentColor = colors.onPrimary,
                ),
            )

            Box(Modifier.fillMaxWidth().weight(1f).background(colors.background)) {
                val profile = state.profile
                when {
                    profile != null -> ProfileContent(profile, stats, onChangePasswordClick)
                    state.loadError != null && !state.isLoading -> Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(state.loadError, color = colors.error, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { events(ProfileEvent.GetProfile(systemToken)) }) { Text("Retry") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(p: ProfileDto, stats: MyWorkDTO?, onChangePasswordClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val emp = p.employeeDetails

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // ---------- Header ----------
        ProfileCard {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(84.dp).background(colors.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(p.initials, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onPrimaryContainer)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            p.displayName, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = colors.onSurface,
                            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false),
                        )
                        Spacer(Modifier.width(8.dp))
                        StatusPill(if (p.isUserActive) "Active" else "Inactive", if (p.isUserActive) Kind.GREEN else Kind.RED)
                    }
                    val designation = emp?.designation.displayName() ?: p.role?.roleName
                    designation?.let {
                        Text(it, fontSize = 13.sp, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                    }
                    emp?.department.displayName()?.let {
                        Text(it, fontSize = 13.sp, color = colors.primary, modifier = Modifier.padding(top = 2.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    p.emailId?.takeIf { it.isNotBlank() }?.let {
                        ContactLine(Icons.Default.Email, it, p.isEmailVerified)
                    }
                    p.mobileNumber?.takeIf { it.isNotBlank() }?.let {
                        ContactLine(Icons.Default.Call, it, p.isMobileVerified)
                    }
                }
            }
        }

        // ---------- Stats ----------
        if (stats != null) {
            ProfileCard {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(vertical = 14.dp)) {
                    StatCell("Leads", stats.leads, "Assigned", Icons.Default.Groups, Kind.PURPLE, Modifier.weight(1f))
                    StatDivider()
                    StatCell("Tasks", stats.tasks, "Pending", Icons.Default.CheckCircle, Kind.GREEN, Modifier.weight(1f))
                    StatDivider()
                    StatCell("Calls", stats.calls, "Today", Icons.Default.Call, Kind.BLUE, Modifier.weight(1f))
                    StatDivider()
                    StatCell("Meetings", stats.meetings, "Today", Icons.Default.CalendarMonth, Kind.ORANGE, Modifier.weight(1f))
                }
            }
        }

        // ---------- Work details ----------
        ProfileCard {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                val rows = listOf(
                    Triple(Icons.Default.Badge, "Role", p.role?.roleName),
                    Triple(Icons.Default.Business, "Department", emp?.department.displayName()),
                    Triple(Icons.Default.Work, "Designation", emp?.designation.displayName()),
                    Triple(Icons.Default.AccountTree, "Branch", p.branch.displayName()),
                    Triple(Icons.Default.SupervisorAccount, "Reporting To", emp?.reportingTo.displayName()),
                    Triple(Icons.Default.CalendarMonth, "Date of Joining", emp?.dateOfJoining?.let { formatProfileDate(it) }),
                    Triple(Icons.Default.Badge, "Employee Code", emp?.employmentCode.text() ?: emp?.employeeId.text()),
                )
                rows.forEachIndexed { i, (icon, label, value) ->
                    if (i > 0) HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
                    InfoRow(icon, label, value ?: "-")
                }
            }
        }

        // ---------- Preferences ----------
        ProfileCard {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, null, tint = colors.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.primary)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    PrefCell("Time Zone", p.timeZone?.timeZoneName ?: "-", Modifier.weight(1f))
                    StatDivider()
                    PrefCell("Language", p.preferredLanguage?.displayName ?: p.preferredLanguage?.nativeName ?: "-", Modifier.weight(1f))
                    StatDivider()
                    PrefCell(
                        "Currency",
                        p.preferredCurrency?.let { "${it.currencyCode.orEmpty()} (${it.symbol.orEmpty()})" } ?: "-",
                        Modifier.weight(1f),
                    )
                }
            }
        }

        // ---------- Change password ----------
        ProfileCard(onClick = onChangePasswordClick) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, tint = colors.primary)
                Spacer(Modifier.width(12.dp))
                Text("Change Password", fontSize = 15.sp, color = colors.onSurface, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = colors.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ---------------- Building blocks ----------------

@Composable
private fun ProfileCard(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) { content() }
}

@Composable
private fun ContactLine(icon: ImageVector, text: String, verified: Boolean) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text, fontSize = 13.sp, color = colors.onSurface, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false),
        )
        Spacer(Modifier.width(8.dp))
        StatusPill(if (verified) "Verified" else "Not verified", if (verified) Kind.GREEN else Kind.AMBER, small = true)
    }
}

@Composable
private fun StatusPill(text: String, kind: Kind, small: Boolean = false) {
    val t = tone(kind)
    Box(
        Modifier.background(t.bg, RoundedCornerShape(20.dp))
            .padding(horizontal = if (small) 6.dp else 10.dp, vertical = if (small) 2.dp else 4.dp)
    ) {
        Text(text, color = t.fg, fontSize = if (small) 10.sp else 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun StatCell(label: String, count: Int, caption: String, icon: ImageVector, kind: Kind, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val t = tone(kind)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = t.fg, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 1)
        Text(count.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text(caption, fontSize = 11.sp, color = colors.onSurfaceVariant, maxLines = 1)
    }
}

@Composable
private fun StatDivider() {
    Box(
        Modifier.width(1.dp).fillMaxHeight()
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    )
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = colors.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = colors.onSurface)
        Spacer(Modifier.width(12.dp))
        Text(
            value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface,
            textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PrefCell(label: String, value: String, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier.padding(horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = colors.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(
            value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.onSurface,
            textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis,
        )
    }
}

// ---------------- Theme-aware tones ----------------

private enum class Kind { PURPLE, BLUE, GREEN, ORANGE, AMBER, RED }
private data class Tone(val fg: Color, val bg: Color)

@Composable
private fun tone(kind: Kind): Tone {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return when (kind) {
        Kind.PURPLE -> if (dark) Tone(Color(0xFFC4B5FD), Color(0xFF2E2547)) else Tone(Color(0xFF7C3AED), Color(0xFFF1E8FF))
        Kind.BLUE -> if (dark) Tone(Color(0xFF93C5FD), Color(0xFF172A4A)) else Tone(Color(0xFF2563EB), Color(0xFFE5EEFF))
        Kind.GREEN -> if (dark) Tone(Color(0xFF86EFAC), Color(0xFF14352A)) else Tone(Color(0xFF16A34A), Color(0xFFE3F6EA))
        Kind.ORANGE -> if (dark) Tone(Color(0xFFFDBA74), Color(0xFF3F2A17)) else Tone(Color(0xFFEA580C), Color(0xFFFFEDE0))
        Kind.AMBER -> if (dark) Tone(Color(0xFFFCD34D), Color(0xFF3A2F10)) else Tone(Color(0xFFD97706), Color(0xFFFEF3C7))
        Kind.RED -> if (dark) Tone(Color(0xFFFCA5A5), Color(0xFF3F1D1D)) else Tone(Color(0xFFDC2626), Color(0xFFFEE2E2))
    }
}