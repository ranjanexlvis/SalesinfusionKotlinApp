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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeetingScreen(
    state: MeetingsState,
    events: (MeetingsEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    LaunchedEffect(state.meetingSaved) { if (state.meetingSaved) onSaved() }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(MeetingsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isSavingMeeting) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Create Meeting", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { events(MeetingsEvent.SaveMeeting(token)) }) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E2A5E))
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color(0xFFF5F6FA))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SectionHeader(Icons.Default.Groups, "Meeting Information")

                RequiredLabel("Subject")
                FormTextField(state.subject, { events(MeetingsEvent.SubjectChanged(it)) }, "Enter meeting subject", Icons.Default.Person)

                Label("Description")
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { if (it.length <= 500) events(MeetingsEvent.DescriptionChanged(it)) },
                    placeholder = { Text("Enter meeting description", color = Color.Gray) },
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${state.description.length}/500", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))

                Spacer(Modifier.height(12.dp))
                Text("Related Record", fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))

                StepLabel(1, "Select Record Type", "Select the type of record you want to relate.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    RecordTypeChipColored("Lead", Icons.Default.Person, state.relatedRecordType == "LEAD", Color(0xFF3B5BFF)) {
                        events(MeetingsEvent.RelatedRecordTypeChanged("LEAD"))
                    }
                    RecordTypeChipColored("Account", Icons.Default.Business, state.relatedRecordType == "ACCOUNT", Color(0xFF3B5BFF)) {
                        events(MeetingsEvent.RelatedRecordTypeChanged("ACCOUNT"))
                    }
                    RecordTypeChipColored("Contact", Icons.Default.Person, state.relatedRecordType == "CONTACT", Color(0xFF3B5BFF)) {
                        events(MeetingsEvent.RelatedRecordTypeChanged("CONTACT"))
                    }
                }

                Spacer(Modifier.height(12.dp))
                StepLabel(2, "Select Lead", "Search and select a lead")
                OutlinedTextField(
                    value = state.leadSearchQuery,
                    onValueChange = { events(MeetingsEvent.LeadSearchQueryChanged(it, token)) },
                    placeholder = { Text("Search lead by name, email or phone...", color = Color.Gray, fontSize = 13.sp) },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.leadSearchResults.isNotEmpty()) {
                    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))) {
                        state.leadSearchResults.forEach { lead ->
                            Text(lead.firstName, modifier = Modifier.fillMaxWidth().clickable { events(MeetingsEvent.LeadSelected(lead)) }.padding(12.dp))
                            Divider()
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Meeting Details", fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)

                RequiredLabel("Meeting Type")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToggleChip("Offline", state.meetingType == "OFFLINE", Modifier.weight(1f)) {
                        events(MeetingsEvent.MeetingTypeChanged("OFFLINE"))
                    }
                    ToggleChip("Online", state.meetingType == "ONLINE", Modifier.weight(1f)) {
                        events(MeetingsEvent.MeetingTypeChanged("ONLINE"))
                    }
                }

                if (state.meetingType == "OFFLINE") {
                    RequiredLabel("Location")
                    FormTextField(state.location, { events(MeetingsEvent.LocationChanged(it)) }, "Enter meeting location", Icons.Default.LocationOn)
                } else {
                    Label("Meeting Link")
                    FormTextField(state.meetingLink, { events(MeetingsEvent.MeetingLinkChanged(it)) }, "Enter meeting link", Icons.Default.Link)
                }

                RequiredLabel("Priority")
                DropdownField("Select priority", Icons.Default.LocationOn, state.priorityOptions, state.priorityId) {
                    events(MeetingsEvent.MeetingPriorityChanged(it))
                }

                RequiredLabel("Status")
                DropdownField("Select status", Icons.Default.CheckCircleOutline, state.statusOptions, state.statusId) {
                    events(MeetingsEvent.MeetingStatusChanged(it))
                }

                RequiredLabel("Assigned To")
                DropdownField("Select user", Icons.Default.Person, state.userOptions, state.assignedToUserId) {
                    events(MeetingsEvent.MeetingAssignedToChanged(it))
                }

                Spacer(Modifier.height(12.dp))
                Text("Schedule", fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)

                RequiredLabel("Start Date & Time")
                DateTimePickerRow(state.startDateTimeMillis, { events(MeetingsEvent.StartDateTimeChanged(it)) }, accentColor = Color(0xFF3B5BFF))

                RequiredLabel("End Date & Time")
                DateTimePickerRow(state.endDateTimeMillis, { events(MeetingsEvent.EndDateTimeChanged(it)) }, accentColor = Color(0xFF3B5BFF))

                Label("Reminder")
                DateTimePickerRow(state.reminderDateTimeMillis, { events(MeetingsEvent.ReminderDateTimeChanged(it)) }, accentColor = Color(0xFF3B5BFF))

                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(10.dp)) {
                        Text("Cancel", color = Color(0xFF1E2A5E))
                    }
                    Button(
                        onClick = { events(MeetingsEvent.SaveMeeting(token)) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF))
                    ) { Text("Save Meeting", color = Color.White, fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .background(if (selected) Color(0xFFEDEBFF) else Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) Color(0xFF3B5BFF) else Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) Color(0xFF3B5BFF) else Color.DarkGray, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
fun Label(text: String) {
    Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}
