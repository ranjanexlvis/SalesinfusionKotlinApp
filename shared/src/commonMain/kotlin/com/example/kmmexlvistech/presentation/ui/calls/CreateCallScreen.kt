package com.example.kmmexlvistech.presentation.ui.calls

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
import com.example.kmmexlvistech.business.datasource.dto.CallDto
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsEvent
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsState
import com.example.kmmexlvistech.presentation.ui.common.DateTimePickerRow
import com.example.kmmexlvistech.presentation.ui.common.DropdownField
import com.example.kmmexlvistech.presentation.ui.common.FormTextField
import com.example.kmmexlvistech.presentation.ui.common.RequiredLabel
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCallScreen(
    state: CallsState,
    events: (CallsEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    LaunchedEffect(state.callSaved) { if (state.callSaved) onSaved() }

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(CallsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isSavingCall) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Create Call", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { events(CallsEvent.SaveCall(token)) }) {
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
                SectionHeaderColored(Icons.Default.Call, "Call Information", Color(0xFF16A34A))

                RequiredLabel("Subject")
                FormTextField(state.subject, { events(CallsEvent.SubjectChanged(it)) }, "Enter call subject", Icons.Default.Person)

                Label("Description")
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { if (it.length <= 500) events(CallsEvent.DescriptionChanged(it)) },
                    placeholder = { Text("Enter call description", color = Color.Gray) },
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${state.description.length}/500", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))

                Spacer(Modifier.height(12.dp))
                Text("Related Record", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))

                StepLabelColored(1, "Select Record Type", "Select the type of record you want to relate.", Color(0xFF16A34A))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    RecordTypeChipColored("Lead", Icons.Default.Person, state.relatedRecordType == "LEAD", Color(0xFF16A34A)) {
                        events(CallsEvent.RelatedRecordTypeChanged("LEAD"))
                    }
                    RecordTypeChipColored("Account", Icons.Default.Business, state.relatedRecordType == "ACCOUNT", Color(0xFF16A34A)) {
                        events(CallsEvent.RelatedRecordTypeChanged("ACCOUNT"))
                    }
                    RecordTypeChipColored("Contact", Icons.Default.Person, state.relatedRecordType == "CONTACT", Color(0xFF16A34A)) {
                        events(CallsEvent.RelatedRecordTypeChanged("CONTACT"))
                    }
                }

                Spacer(Modifier.height(12.dp))
                StepLabelColored(2, "Select Lead", "Search and select a lead", Color(0xFF16A34A))
                OutlinedTextField(
                    value = state.leadSearchQuery,
                    onValueChange = { events(CallsEvent.LeadSearchQueryChanged(it, token)) },
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
                            Text(lead.firstName, modifier = Modifier.fillMaxWidth().clickable { events(CallsEvent.LeadSelected(lead)) }.padding(12.dp))
                            Divider()
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Call Details", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), fontSize = 15.sp)

                RequiredLabel("Priority")
                DropdownField("Select priority", Icons.Default.Assignment, state.priorityOptions, state.priorityId) {
                    events(CallsEvent.CallPriorityChanged(it))
                }

                RequiredLabel("Status")
                DropdownField("Select status", Icons.Default.CheckCircleOutline, state.statusOptions, state.statusId) {
                    events(CallsEvent.CallStatusChanged(it))
                }

                RequiredLabel("Assigned To")
                DropdownField("Select user", Icons.Default.Person, state.userOptions, state.assignedToUserId) {
                    events(CallsEvent.CallAssignedToChanged(it))
                }

                Spacer(Modifier.height(12.dp))
                Text("Schedule", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), fontSize = 15.sp)

                RequiredLabel("Start Date & Time")
                DateTimePickerRow(state.startDateTimeMillis, { events(CallsEvent.StartDateTimeChanged(it)) })

                RequiredLabel("End Date & Time")
                DateTimePickerRow(state.endDateTimeMillis, { events(CallsEvent.EndDateTimeChanged(it)) })

                Label("Reminder")
                DateTimePickerRow(state.reminderDateTimeMillis, { events(CallsEvent.ReminderDateTimeChanged(it)) })

                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(10.dp)) {
                        Text("Cancel", color = Color(0xFF1E2A5E))
                    }
                    Button(
                        onClick = { events(CallsEvent.SaveCall(token)) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) { Text("Save Call", color = Color.White, fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionHeaderColored(icon: ImageVector, title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 15.sp)
    }
}

@Composable
private fun StepLabelColored(step: Int, title: String, subtitle: String?, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(Modifier.size(20.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
            Text(step.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(" *", color = Color.Red, fontSize = 13.sp)
            }
            subtitle?.let { Text(it, color = Color.Gray, fontSize = 11.sp) }
        }
    }
}

@Composable
private fun RecordTypeChipColored(label: String, icon: ImageVector, selected: Boolean, color: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .background(if (selected) color.copy(alpha = 0.1f) else Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) color else Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) color else Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = if (selected) color else Color.DarkGray, fontSize = 13.sp)
    }
}

@Composable
fun Label(text: String) {
    Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}