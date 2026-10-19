package com.example.kmmexlvistech.presentation.ui.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.models.displayName
import com.example.kmmexlvistech.business.datasource.models.formatProfileDate
import com.example.kmmexlvistech.business.datasource.models.text
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.PickerOption
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileEvent
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileState

// imports: same as ProfileScreen, plus
// androidx.compose.foundation.BorderStroke, androidx.compose.foundation.text.KeyboardOptions,
// androidx.compose.ui.text.input.KeyboardType, ImeAction, PickerOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: ProfileState,
    events: (ProfileEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val p = state.profile
    val emp = p?.employeeDetails
    val saving = state.isSaving
    val v = state.showValidation

    LaunchedEffect(state.profileSaved) { if (state.profileSaved) onSaved() }

    val submit: () -> Unit = { if (!saving) events(ProfileEvent.SaveProfile(token)) }
    val emailValid = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(state.emailId.trim())

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(ProfileEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (saving) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    TextButton(onClick = submit, enabled = !saving) {
                        Text("Save", fontWeight = FontWeight.SemiBold, color = colors.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary,
                    actionIconContentColor = colors.onPrimary,
                ),
            )

            Column(
                Modifier.fillMaxWidth().weight(1f).background(colors.background)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // ---------- Photo (display only: no upload API yet) ----------
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(72.dp).background(colors.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(p?.initials ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.onPrimaryContainer)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(p?.displayName ?: "", fontWeight = FontWeight.SemiBold, color = colors.onSurface)
                        Text("@${p?.userName.orEmpty()}", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    }
                }

                SectionTitle("Personal Information")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        "First Name", state.firstName, { events(ProfileEvent.FirstNameChanged(it)) },
                        required = true, error = if (v && state.firstName.isBlank()) "Required" else null,
                        modifier = Modifier.weight(1f),
                    )
                    FormField(
                        "Last Name", state.lastName, { events(ProfileEvent.LastNameChanged(it)) },
                        required = true, error = if (v && state.lastName.isBlank()) "Required" else null,
                        modifier = Modifier.weight(1f),
                    )
                }

                ReadOnlyField("Username", p?.userName ?: "-", trailingLock = true)

                FormField(
                    "Email Address", state.emailId, { events(ProfileEvent.EmailChanged(it)) },
                    required = true, keyboardType = KeyboardType.Email, icon = Icons.Default.Email,
                    error = if (v && !emailValid) "Enter a valid email" else null,
                )
                FormField(
                    "Mobile Number", state.mobileNumber, { events(ProfileEvent.MobileChanged(it)) },
                    required = true, keyboardType = KeyboardType.Phone, icon = Icons.Default.Call,
                    error = if (v && state.mobileNumber.length !in 7..15) "Enter a valid mobile number" else null,
                )

                SectionTitle("Work Information", suffix = " (Read Only)")
                val work = listOf(
                    "Employment Code" to (emp?.employmentCode.text() ?: emp?.employeeId.text() ?: "-"),
                    "Department" to (emp?.department.displayName() ?: "-"),
                    "Designation" to (emp?.designation.displayName() ?: "-"),
                    "Employee Role" to (emp?.employeeRole.displayName() ?: p?.role?.roleName ?: "-"),
                    "Employment Type" to (emp?.employmentType.displayName() ?: "-"),
                    "Reporting To" to (emp?.reportingTo.displayName() ?: "-"),
                    "Branch" to (p?.branch.displayName() ?: "-"),
                    "Date of Joining" to formatProfileDate(emp?.dateOfJoining),
                )
                work.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { (label, value) -> ReadOnlyField(label, value, Modifier.weight(1f)) }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                SectionTitle("Preferences")
                PickerField(
                    "Time Zone", state.timeZoneOptions, state.timeZoneId?.toString(),
                    onSelect = { events(ProfileEvent.TimeZoneChanged(it.toInt())) },
                )
                PickerField(
                    "Preferred Language", state.languageOptions, state.languageCode.ifBlank { null },
                    onSelect = { events(ProfileEvent.LanguageChanged(it)) },
                )
                PickerField(
                    "Preferred Currency", state.currencyOptions, state.currencyId?.toString(),
                    onSelect = { events(ProfileEvent.CurrencyChanged(it.toInt())) },
                )

                SectionTitle("Verification Status")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VerifyCard(
                        "Email", p?.emailId.orEmpty(), p?.isEmailVerified == true,
                        Icons.Default.Email, Modifier.weight(1f),
                    )
                    VerifyCard(
                        "Mobile", p?.mobileNumber.orEmpty(), p?.isMobileVerified == true,
                        Icons.Default.Call, Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colors.outline),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                    ) { Text("Cancel") }
                    Button(
                        onClick = submit,
                        enabled = !saving,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary,
                            disabledContainerColor = colors.onSurface.copy(alpha = 0.12f),
                            disabledContentColor = colors.onSurface.copy(alpha = 0.38f),
                        ),
                    ) { Text("Save Changes", fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ---------------- Building blocks ----------------

@Composable
private fun SectionTitle(title: String, suffix: String? = null) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.padding(top = 10.dp)) {
        Row {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.primary)
            suffix?.let { Text(it, fontSize = 12.sp, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp)) }
        }
        HorizontalDivider(Modifier.padding(top = 6.dp), color = colors.outlineVariant.copy(alpha = 0.6f))
    }
}

@Composable
private fun FieldLabel(text: String, required: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.padding(bottom = 4.dp)) {
        Text(text, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
        if (required) Text(" *", fontSize = 12.5.sp, color = colors.error)
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: ImageVector? = null,
) {
    val c = MaterialTheme.colorScheme
    Column(modifier) {
        FieldLabel(label, required)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            isError = error != null,
            leadingIcon = icon?.let { { Icon(it, contentDescription = null) } },
            supportingText = error?.let { { Text(it, fontSize = 11.sp) } },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = c.onSurface, unfocusedTextColor = c.onSurface, errorTextColor = c.onSurface,
                focusedContainerColor = c.surface, unfocusedContainerColor = c.surface, errorContainerColor = c.surface,
                focusedBorderColor = c.primary, unfocusedBorderColor = c.outlineVariant, errorBorderColor = c.error,
                focusedLeadingIconColor = c.primary, unfocusedLeadingIconColor = c.onSurfaceVariant,
                errorLeadingIconColor = c.error, cursorColor = c.primary,
                errorSupportingTextColor = c.error,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String, modifier: Modifier = Modifier, trailingLock: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth()) {
        FieldLabel(label)
        Row(
            Modifier.fillMaxWidth().heightIn(min = 44.dp)
                .background(colors.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                value, fontSize = 13.sp, color = colors.onSurfaceVariant, maxLines = 1,
                overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f),
            )
            if (trailingLock) Icon(Icons.Default.Lock, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun PickerField(label: String, options: List<PickerOption>, selectedKey: String?, onSelect: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { androidx.compose.runtime.mutableStateOf(false) }
    // Derived on every recomposition, so options loaded late still show the right label
    val selectedLabel = options.firstOrNull { it.key == selectedKey }?.label

    Column(Modifier.fillMaxWidth()) {
        FieldLabel(label)
        Box {
            Row(
                Modifier.fillMaxWidth().height(52.dp)
                    .background(colors.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, colors.outlineVariant, RoundedCornerShape(12.dp))
                    .clickable(enabled = options.isNotEmpty()) { expanded = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    selectedLabel ?: "Select",
                    color = if (selectedLabel != null) colors.onSurface else colors.onSurfaceVariant,
                    fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = colors.onSurfaceVariant)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = { expanded = false; onSelect(option.key) },
                    )
                }
            }
        }
    }
}

@Composable
private fun VerifyCard(title: String, value: String, verified: Boolean, icon: ImageVector, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < 0.5f
    val fg = if (verified) (if (dark) Color(0xFF86EFAC) else Color(0xFF16A34A)) else (if (dark) Color(0xFFFCD34D) else Color(0xFFD97706))
    val bg = if (verified) (if (dark) Color(0xFF14352A) else Color(0xFFE3F6EA)) else (if (dark) Color(0xFF3A2F10) else Color(0xFFFEF3C7))
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier.background(colors.surface, shape).border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), shape).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(34.dp).background(bg, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                if (verified) "$title Verified" else "$title Not Verified",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface, maxLines = 1,
            )
            Text(value, fontSize = 11.sp, color = colors.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}