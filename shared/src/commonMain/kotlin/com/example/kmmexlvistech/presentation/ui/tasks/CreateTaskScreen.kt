
package com.example.kmmexlvistech.presentation.ui.tasks


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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.TaskDto
import com.example.kmmexlvistech.business.datasource.models.ActivityTaskData
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.common.DateTimeRow
import com.example.kmmexlvistech.presentation.ui.common.DropdownField
import com.example.kmmexlvistech.presentation.ui.common.FormTextField
import com.example.kmmexlvistech.presentation.ui.common.RecordTypeChip
import com.example.kmmexlvistech.presentation.ui.common.RequiredLabel
import com.example.kmmexlvistech.presentation.ui.common.SectionHeader
import com.example.kmmexlvistech.presentation.ui.common.StepLabel
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskState
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TasksEvent
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    state: TaskState,
    events: (TasksEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    LaunchedEffect(state.taskSaved) { if (state.taskSaved) onSaved() }
    LaunchedEffect(state.taskUpdated) { if (state.taskUpdated) onSaved() }

    val isEdit = state.isEditMode
    val screenTitle = if (isEdit) "Edit Task" else "Create Task"
    val topBarButtonLabel = if (isEdit) "Update" else "Save"
    val bottomButtonLabel = if (isEdit) "Update Task" else "Save Task"

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(TasksEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isSavingTask || state.isUpdatingTask) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(screenTitle, color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (isEdit) events(TasksEvent.UpdateTask(token)) else events(TasksEvent.SaveTask(token))
                    }) {
                        Text(topBarButtonLabel, color = Color.White, fontWeight = FontWeight.SemiBold)
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
                SectionHeader(Icons.Default.CheckBox, "Task Information")

                RequiredLabel("Subject")
                FormTextField(state.subject, { events(TasksEvent.SubjectChanged(it)) }, "Enter task subject", Icons.Default.Person)

                Label("Description")
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { if (it.length <= 500) events(TasksEvent.DescriptionChanged(it)) },
                    placeholder = { Text("Enter task description", color = Color.Gray) },
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${state.description.length}/500", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))

                Spacer(Modifier.height(12.dp))
                Text("Related Record", fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))

                StepLabel(1, "Select Record Type", "Select the type of record you want to relate.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    RecordTypeChip("Lead", Icons.Default.Person, state.relatedRecordType == "LEAD") {
                        events(TasksEvent.RelatedRecordTypeChanged("LEAD"))
                    }
                    RecordTypeChip("Account", Icons.Default.Business, state.relatedRecordType == "ACCOUNT") {
                        events(TasksEvent.RelatedRecordTypeChanged("ACCOUNT"))
                    }
                    RecordTypeChip("Contact", Icons.Default.Person, state.relatedRecordType == "CONTACT") {
                        events(TasksEvent.RelatedRecordTypeChanged("CONTACT"))
                    }
                }

                Spacer(Modifier.height(12.dp))
                when (state.relatedRecordType) {
                    "LEAD" -> {
                        StepLabel(2, "Select Lead", null)
                        Box {
                            OutlinedTextField(
                                value = state.leadSearchQuery,
                                onValueChange = { events(TasksEvent.LeadSearchQueryChanged(it, token)) },
                                placeholder = { Text("Search lead by name, email or phone...", color = Color.Gray, fontSize = 13.sp) },
                                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (state.leadSearchResults.isNotEmpty()) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                            ) {
                                state.leadSearchResults.forEach { lead ->
                                    Text(
                                        lead.firstName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { events(TasksEvent.LeadSelected(lead)) }
                                            .padding(12.dp)
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                    "ACCOUNT" -> {
                        StepLabel(2, "Select Account", null)
                        Text("Account search — coming soon", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                    }
                    "CONTACT" -> {
                        StepLabel(2, "Select Contact", null)
                        Text("Contact search — coming soon", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Task Details", fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)

                RequiredLabel("Priority")
                DropdownField("Select priority", Icons.Default.Assignment, state.priorityOptions, state.priorityId) {
                    events(TasksEvent.TaskPriorityChanged(it))
                }

                RequiredLabel("Status")
                DropdownField("Select status", Icons.Default.CheckCircleOutline, state.statusOptions, state.statusId) {
                    events(TasksEvent.TaskStatusChanged(it))
                }

                RequiredLabel("Assigned To")
                DropdownField("Select user", Icons.Default.Person, state.userOptions, state.assignedToUserId) {
                    events(TasksEvent.TaskAssignedToChanged(it))
                }

                Spacer(Modifier.height(12.dp))
                Text("Schedule", fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)

                RequiredLabel("Start Date & Time")
                DateTimeRow(state.startDate, state.startTime,
                    { events(TasksEvent.StartDateChanged(it)) }, { events(TasksEvent.StartTimeChanged(it)) })

                RequiredLabel("End Date & Time")
                DateTimeRow(state.endDate, state.endTime,
                    { events(TasksEvent.EndDateChanged(it)) }, { events(TasksEvent.EndTimeChanged(it)) })

                RequiredLabel("Due Date & Time")
                DateTimeRow(state.dueDate, state.dueTime,
                    { events(TasksEvent.DueDateChanged(it)) }, { events(TasksEvent.DueTimeChanged(it)) })

                Label("Reminder")
                DateTimeRow(state.reminderDate, state.reminderTime,
                    { events(TasksEvent.ReminderDateChanged(it)) }, { events(TasksEvent.ReminderTimeChanged(it)) })

                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(10.dp)) {
                        Text("Cancel", color = Color(0xFF1E2A5E))
                    }
                    Button(
                        onClick = { events(TasksEvent.SaveTask(token)) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF))
                    ) { Text("Save Task", color = Color.White, fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(16.dp))

                if (isEdit) {
                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Audit Information", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth()) {
                                Column(Modifier.weight(1f)) {
                                    Text("Task ID", color = Color.Gray, fontSize = 11.sp)
                                    Text(state.editingTaskDisplayId ?: "-", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                                Column(Modifier.weight(1f)) {
                                    Text("Last Modified", color = Color.Gray, fontSize = 11.sp)
                                    Text(state.editingTaskLastModified ?: "-", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Created On", color = Color.Gray, fontSize = 11.sp)
                            Text(state.editingTaskCreatedOn ?: "-", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }

                // Single button row — adapts for both Create and Edit modes
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(10.dp)) {
                        Text("Cancel", color = Color(0xFF1E2A5E))
                    }
                        }

                Button(
                    onClick = {
                        if (isEdit) events(TasksEvent.UpdateTask(token)) else events(TasksEvent.SaveTask(token))
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF))
                ) { Text(bottomButtonLabel, color = Color.White, fontWeight = FontWeight.SemiBold) }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun Label(text: String) {
    Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

