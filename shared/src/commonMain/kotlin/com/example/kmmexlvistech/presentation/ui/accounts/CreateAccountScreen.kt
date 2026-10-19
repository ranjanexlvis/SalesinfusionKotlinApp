package com.example.kmmexlvistech.presentation.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountOption
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsEvent
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen

// imports: same set as AccountsScreen (+ KeyboardOptions, KeyboardType, ImeAction, BorderStroke)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    state: AccountsState,
    events: (AccountsEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    val isEdit = state.isEditMode
    val saving = state.isSaving
    val v = state.showValidation

    LaunchedEffect(state.accountSaved) { if (state.accountSaved) onSaved() }
    // always leave with a clean form (system back, drawer, cancel)
    DisposableEffect(Unit) { onDispose { events(AccountsEvent.ResetAccountForm) } }

    val submit: () -> Unit = { if (!saving) events(AccountsEvent.SaveAccount(token)) }

    fun List<AccountOption>.labelOf(id: Int?) = firstOrNull { it.id == id }?.label

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(AccountsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (saving) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Account" else "Create Account", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = c.primary, titleContentColor = c.onPrimary, navigationIconContentColor = c.onPrimary,
                ),
            )

            Column(
                Modifier.fillMaxWidth().weight(1f).background(c.background)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
            ) {
                if (!isEdit) {
                    FieldLabel("Account Code", required = true)
                    OutlinedTextField(
                        value = state.accountCode, onValueChange = { events(AccountsEvent.CodeChanged(it)) },
                        placeholder = { Text("ACC001") }, singleLine = true, shape = RoundedCornerShape(12.dp),
                        isError = v && state.accountCode.isBlank(),
                        supportingText = if (v && state.accountCode.isBlank()) { { Text("Required") } } else null,
                        colors = accountFieldColors(), modifier = Modifier.fillMaxWidth(),
                    )
                }

                FieldLabel("Account Name", required = true)
                OutlinedTextField(
                    value = state.accountName, onValueChange = { events(AccountsEvent.NameChanged(it)) },
                    placeholder = { Text("Enter account name") }, singleLine = true, shape = RoundedCornerShape(12.dp),
                    isError = v && state.accountName.isBlank(),
                    supportingText = if (v && state.accountName.isBlank()) { { Text("Required") } } else null,
                    colors = accountFieldColors(), modifier = Modifier.fillMaxWidth(),
                )

                FieldLabel("Account Type", required = true)
                PickerField(
                    "Select account type", state.typeOptions.map { it.label }, state.typeOptions.labelOf(state.accountTypeId),
                    error = if (v && state.accountTypeId == null) "Required" else null,
                ) { events(AccountsEvent.TypeChanged(state.typeOptions[it].id)) }

                FieldLabel("Industry", required = true)
                PickerField(
                    "Select industry", state.industryOptions.map { it.label }, state.industryOptions.labelOf(state.industryId),
                    error = if (v && state.industryId == null) "Required" else null,
                ) { events(AccountsEvent.IndustryChanged(state.industryOptions[it].id)) }

                FieldLabel("Website")
                OutlinedTextField(
                    value = state.website, onValueChange = { events(AccountsEvent.WebsiteChanged(it)) },
                    placeholder = { Text("https://example.com") }, singleLine = true, shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    colors = accountFieldColors(), modifier = Modifier.fillMaxWidth(),
                )

                FieldLabel("Owner", required = true)
                PickerField(
                    "Select owner", state.ownerOptions.map { it.label }, state.ownerOptions.labelOf(state.ownerUserId),
                    error = if (v && state.ownerUserId == null) "Required" else null,
                ) { events(AccountsEvent.OwnerChanged(state.ownerOptions[it].id)) }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = submit,
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = c.primary, contentColor = c.onPrimary,
                        disabledContainerColor = c.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = c.onSurface.copy(alpha = 0.38f),
                    ),
                ) { Text(if (isEdit) "Update Account" else "Create Account", fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}