package com.example.kmmexlvistech.presentation.ui.meetings


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.MeetingDto
import com.example.kmmexlvistech.business.datasource.models.ActivityMeetingData
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.common.DateTimePickerRow
import com.example.kmmexlvistech.presentation.ui.common.DropdownField
import com.example.kmmexlvistech.presentation.ui.common.FormTextField
import com.example.kmmexlvistech.presentation.ui.common.RecordTypeChipColored
import com.example.kmmexlvistech.presentation.ui.common.RequiredLabel
import com.example.kmmexlvistech.presentation.ui.common.SectionHeader
import com.example.kmmexlvistech.presentation.ui.common.StepLabel
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsEvent
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsState


import androidx.compose.foundation.BorderStroke


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeetingScreen(
    state: MeetingsState,
    events: (MeetingsEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val isEdit = state.isEditMode
    val saving = state.isSavingMeeting

    LaunchedEffect(state.meetingSaved) { if (state.meetingSaved) onSaved() }

    // Always leave with a clean form (covers system back and drawer navigation too)
    DisposableEffect(Unit) {
        onDispose { events(MeetingsEvent.ResetCreateMeetingForm) }
    }

    val submit: () -> Unit = { if (!saving) events(MeetingsEvent.SaveMeeting(token)) }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(MeetingsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (saving) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(if (isEdit) "Edit Meeting" else "Create Meeting", fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = submit, enabled = !saving) {
                        Text(
                            if (isEdit) "Update" else "Save",
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onPrimary,
                        )
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
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colors.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                MeetingSectionHeader(Icons.Default.Groups, "Meeting Information")

                MeetingFieldLabel("Subject", required = true)
                MeetingTextField(
                    value = state.subject,
                    onValueChange = { events(MeetingsEvent.SubjectChanged(it)) },
                    placeholder = "Enter meeting subject",
                    icon = Icons.Default.Person,
                )

                Label("Description")
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { if (it.length <= 500) events(MeetingsEvent.DescriptionChanged(it)) },
                    placeholder = { Text("Enter meeting description") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    colors = meetingFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "${state.description.length}/500",
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End),
                )

                Spacer(Modifier.height(12.dp))
                MeetingSectionHeader(null, "Related Record")

                MeetingStepLabel(1, "Select Record Type", "Select the type of record you want to relate.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    MeetingRecordChip("Lead", Icons.Default.Person, state.relatedRecordType == "LEAD") {
                        events(MeetingsEvent.RelatedRecordTypeChanged("LEAD"))
                    }
                    MeetingRecordChip("Account", Icons.Default.Business, state.relatedRecordType == "ACCOUNT") {
                        events(MeetingsEvent.RelatedRecordTypeChanged("ACCOUNT"))
                    }
                    MeetingRecordChip("Contact", Icons.Default.Person, state.relatedRecordType == "CONTACT") {
                        events(MeetingsEvent.RelatedRecordTypeChanged("CONTACT"))
                    }
                }

                Spacer(Modifier.height(12.dp))
                MeetingStepLabel(2, "Select Lead", "Search and select a lead")
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.leadSearchQuery,
                    onValueChange = { events(MeetingsEvent.LeadSearchQueryChanged(it, token)) },
                    placeholder = { Text("Search lead by name, email or phone...", fontSize = 13.sp) },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = meetingFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (state.leadSearchResults.isNotEmpty()) {
                    val shape = RoundedCornerShape(12.dp)
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(colors.surface, shape)
                            .border(1.dp, colors.outlineVariant, shape),
                    ) {
                        state.leadSearchResults.forEachIndexed { i, lead ->
                            if (i > 0) HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
                            Text(
                                "${lead.firstName} ${lead.lastName}".trim(),
                                color = colors.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { events(MeetingsEvent.LeadSelected(lead)) }
                                    .padding(12.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                MeetingSectionHeader(null, "Meeting Details")

                MeetingFieldLabel("Meeting Type", required = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToggleChip("Offline", state.meetingType == "OFFLINE", Modifier.weight(1f)) {
                        events(MeetingsEvent.MeetingTypeChanged("OFFLINE"))
                    }
                    ToggleChip("Online", state.meetingType == "ONLINE", Modifier.weight(1f)) {
                        events(MeetingsEvent.MeetingTypeChanged("ONLINE"))
                    }
                }

                if (state.meetingType == "OFFLINE") {
                    MeetingFieldLabel("Location", required = true)
                    MeetingTextField(
                        state.location, { events(MeetingsEvent.LocationChanged(it)) },
                        "Enter meeting location", Icons.Default.LocationOn,
                    )
                } else {
                    Label("Meeting Link")
                    MeetingTextField(
                        state.meetingLink, { events(MeetingsEvent.MeetingLinkChanged(it)) },
                        "Enter meeting link", Icons.Default.Link,
                    )
                }

                MeetingFieldLabel("Priority", required = true)
                DropdownField("Select priority", Icons.Default.Assignment, state.priorityOptions, state.priorityId) {
                    events(MeetingsEvent.MeetingPriorityChanged(it))
                }

                MeetingFieldLabel("Status", required = true)
                DropdownField("Select status", Icons.Default.CheckCircleOutline, state.statusOptions, state.statusId) {
                    events(MeetingsEvent.MeetingStatusChanged(it))
                }

                MeetingFieldLabel("Assigned To", required = true)
                DropdownField("Select user", Icons.Default.Person, state.userOptions, state.assignedToUserId) {
                    events(MeetingsEvent.MeetingAssignedToChanged(it))
                }

                Spacer(Modifier.height(12.dp))
                MeetingSectionHeader(null, "Schedule")

                MeetingFieldLabel("Start Date & Time", required = true)
                DateTimePickerRow(
                    state.startDateTimeMillis,
                    { events(MeetingsEvent.StartDateTimeChanged(it)) },
                    accentColor = colors.primary,
                )

                MeetingFieldLabel("End Date & Time", required = true)
                DateTimePickerRow(
                    state.endDateTimeMillis,
                    { events(MeetingsEvent.EndDateTimeChanged(it)) },
                    accentColor = colors.primary,
                )

                Label("Reminder")
                DateTimePickerRow(
                    state.reminderDateTimeMillis,
                    { events(MeetingsEvent.ReminderDateTimeChanged(it)) },
                    accentColor = colors.primary,
                )

                Spacer(Modifier.height(24.dp))
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
                    ) {
                        Text(if (isEdit) "Update Meeting" else "Save Meeting", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ---------------- Themed building blocks ----------------

@Composable
private fun meetingFieldColors() = MaterialTheme.colorScheme.let { c ->
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = c.onSurface,
        unfocusedTextColor = c.onSurface,
        focusedContainerColor = c.surface,
        unfocusedContainerColor = c.surface,
        focusedBorderColor = c.primary,
        unfocusedBorderColor = c.outlineVariant,
        focusedPlaceholderColor = c.onSurfaceVariant,
        unfocusedPlaceholderColor = c.onSurfaceVariant,
        focusedLeadingIconColor = c.primary,
        unfocusedLeadingIconColor = c.onSurfaceVariant,
        focusedTrailingIconColor = c.onSurfaceVariant,
        unfocusedTrailingIconColor = c.onSurfaceVariant,
        cursorColor = c.primary,
    )
}

@Composable
private fun MeetingTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = meetingFieldColors(),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun MeetingFieldLabel(text: String, required: Boolean) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = colors.onSurface)
        if (required) Text(" *", color = colors.error, fontSize = 13.sp)
    }
}

@Composable
fun Label(text: String) {
    Text(
        text,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun MeetingSectionHeader(icon: ImageVector?, title: String) {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(title, fontWeight = FontWeight.Bold, color = colors.primary, fontSize = 15.sp)
    }
}

@Composable
private fun MeetingStepLabel(step: Int, title: String, subtitle: String?) {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.Top) {
        Box(Modifier.size(20.dp).background(colors.primary, CircleShape), contentAlignment = Alignment.Center) {
            Text(step.toString(), color = colors.onPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = colors.onSurface)
                Text(" *", color = colors.error, fontSize = 13.sp)
            }
            subtitle?.let { Text(it, color = colors.onSurfaceVariant, fontSize = 11.sp) }
        }
    }
}

@Composable
private fun MeetingRecordChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .background(if (selected) colors.primaryContainer.copy(alpha = 0.6f) else colors.surface, shape)
            .border(1.dp, if (selected) colors.primary else colors.outlineVariant, shape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (selected) colors.primary else colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color = if (selected) colors.primary else colors.onSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .background(if (selected) colors.primaryContainer.copy(alpha = 0.6f) else colors.surface, shape)
            .border(1.dp, if (selected) colors.primary else colors.outlineVariant, shape)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (selected) colors.primary else colors.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}