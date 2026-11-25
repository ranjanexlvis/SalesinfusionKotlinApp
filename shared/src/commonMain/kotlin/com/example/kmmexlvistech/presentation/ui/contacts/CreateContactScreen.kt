package com.example.kmmexlvistech.presentation.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsEvent
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
// + ContactsState / ContactsEvent imports

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactScreen(
    state: ContactsState,
    events: (ContactsEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    val isEdit = state.isEditMode
    val saving = state.isSaving
    val v = state.showValidation

    LaunchedEffect(state.contactSaved) { if (state.contactSaved) onSaved() }
    // always leave with a clean form (system back, drawer, cancel)
    DisposableEffect(Unit) { onDispose { events(ContactsEvent.ResetContactForm) } }

    val submit: () -> Unit = { if (!saving) events(ContactsEvent.SaveContact(token)) }
    val emailOk = state.email.isBlank() || Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(state.email.trim())

    val accountItems = state.accountOptions.map { SelectItem(it.id, it.label, it.code) }
    val roleItems = state.roleOptions.map { SelectItem(it.id.toString(), it.label, it.description) }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(ContactsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (saving) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Contact" else "Create Contact", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = c.primary, titleContentColor = c.onPrimary, navigationIconContentColor = c.onPrimary,
                ),
            )

            Column(
                Modifier.fillMaxWidth().weight(1f).background(c.background)
                    .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SelectField(
                    label = "Account", required = true, placeholder = "Select account",
                    sheetTitle = "Select Account", searchHint = "Search accounts...",
                    items = accountItems, selectedKey = state.accountId, fallbackLabel = state.editAccountName,
                    itemIcon = Icons.Default.Business,
                    error = if (v && state.accountId == null) "Required" else null,
                    onClear = { events(ContactsEvent.AccountCleared) },
                    onSelect = { events(ContactsEvent.AccountChanged(it)) },
                )

                ContactTextField(
                    "First Name", state.firstName, { events(ContactsEvent.FirstNameChanged(it)) },
                    required = true, error = if (v && state.firstName.isBlank()) "Required" else null,
                )
                ContactTextField(
                    "Last Name", state.lastName, { events(ContactsEvent.LastNameChanged(it)) },
                    required = true, error = if (v && state.lastName.isBlank()) "Required" else null,
                )
                ContactTextField(
                    "Email", state.email, { events(ContactsEvent.EmailChanged(it)) },
                    icon = Icons.Default.Email, placeholder = "name@company.com", keyboardType = KeyboardType.Email,
                    error = if (v && !emailOk) "Enter a valid email" else null,
                )
                ContactTextField(
                    "Phone", state.phone, { events(ContactsEvent.PhoneChanged(it)) },
                    icon = Icons.Default.Phone, placeholder = "022999999", keyboardType = KeyboardType.Phone,
                    enabled = !isEdit,    // the Update API has no phone field
                )
                ContactTextField(
                    "Mobile", state.mobile, { events(ContactsEvent.MobileChanged(it)) },
                    required = true, icon = Icons.Default.Call, placeholder = "9999999999",
                    keyboardType = KeyboardType.Phone,
                    error = if (v && state.mobile.length < 7) "Enter a valid mobile number" else null,
                )

                SelectField(
                    label = "Contact Role", required = true, placeholder = "Select role",
                    sheetTitle = "Select Contact Role", searchHint = "Search roles...",
                    items = roleItems, selectedKey = state.contactRoleId?.toString(), fallbackLabel = null,
                    itemIcon = Icons.Default.Badge,
                    error = if (v && state.contactRoleId == null) "Required" else null,
                    onClear = { events(ContactsEvent.RoleCleared) },
                    onSelect = { events(ContactsEvent.RoleChanged(it.toInt())) },
                )

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Primary Contact", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = c.onSurface, modifier = Modifier.weight(1f))
                    Switch(
                        checked = state.isPrimaryContact,
                        onCheckedChange = { events(ContactsEvent.PrimaryChanged(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = c.onPrimary, checkedTrackColor = c.primary,
                            uncheckedThumbColor = c.outline, uncheckedTrackColor = c.surfaceVariant,
                            uncheckedBorderColor = c.outline,
                        ),
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // pinned bottom action
            Surface(color = c.surface, shadowElevation = 8.dp) {
                Button(
                    onClick = submit,
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp).navigationBarsPadding(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = c.primary, contentColor = c.onPrimary,
                        disabledContainerColor = c.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = c.onSurface.copy(alpha = 0.38f),
                    ),
                ) { Text(if (isEdit) "Update Contact" else "Create Contact", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}