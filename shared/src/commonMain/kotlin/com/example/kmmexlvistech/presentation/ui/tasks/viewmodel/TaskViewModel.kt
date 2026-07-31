package com.example.kmmexlvistech.presentation.ui.tasks.viewmodel


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CreateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.business.datasource.dto.TaskFilterDto
import com.example.kmmexlvistech.business.datasource.dto.TaskRequestDto
import com.example.kmmexlvistech.business.datasource.dto.TaskSortDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateTaskRequestDto
import com.example.kmmexlvistech.business.datasource.models.ActivityTaskData
import com.example.kmmexlvistech.business.datasource.models.SelectedLeadRef
import com.example.kmmexlvistech.business.repos.TaskRepository
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption
import com.example.kmmexlvistech.presentation.ui.leads.formatDate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    val state: MutableState<TaskState> = mutableStateOf(TaskState())

    fun onEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.GetTasks -> {
                state.value = state.value.copy(page = 1, tasks = emptyList())
                fetchTasks(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is TasksEvent.LoadNextPage -> {
                if (state.value.isLoadingMore || !state.value.hasMore) return
                val nextPage = state.value.page + 1
                fetchTasks(token = event.token, userCode = event.userCode, page = nextPage, append = true)
            }
            is TasksEvent.SearchQueryChanged -> {
                state.value = state.value.copy(searchQuery = event.query, page = 1, tasks = emptyList())
                fetchTasks(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is TasksEvent.PriorityFilterChanged -> {
                state.value = state.value.copy(selectedPriorityName = event.priorityName, page = 1, tasks = emptyList())
                fetchTasks(token = event.token, userCode = event.userCode, page = 1, append = false)
            }
            is TasksEvent.Error -> {
                state.value = state.value.copy(progressBarState = ProgressBarState.Idle, isLoadingMore = false)
                appendToMessageQueue(event.uiComponent)
            }
            is TasksEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
            is TasksEvent.SelectTask -> state.value = state.value.copy(selectedTaskId = event.taskId)
            is TasksEvent.LoadTaskDetails -> loadTaskDetails(event.token, event.taskId)

            is TasksEvent.SubjectChanged -> state.value = state.value.copy(subject = event.value)
            is TasksEvent.DescriptionChanged -> state.value = state.value.copy(description = event.value)
            is TasksEvent.RelatedRecordTypeChanged -> state.value = state.value.copy(
                relatedRecordType = event.type, selectedLead = null, leadSearchResults = emptyList()
            )
            is TasksEvent.LeadSearchQueryChanged -> {
                state.value = state.value.copy(leadSearchQuery = event.query)
                searchLeads(event.token, event.query)
            }
            is TasksEvent.LeadSelected -> state.value = state.value.copy(
                selectedLead = SelectedLeadRef(
                    id = event.lead.id,
                    leadId = event.lead.leadId,
                    displayName = "${event.lead.firstName} ${event.lead.lastName}".trim()
                ),
                leadSearchResults = emptyList(),
                leadSearchQuery = "${event.lead.firstName} ${event.lead.lastName}".trim()
            )
            is TasksEvent.TaskPriorityChanged -> state.value = state.value.copy(priorityId = event.id)
            is TasksEvent.TaskStatusChanged -> state.value = state.value.copy(statusId = event.id)
            is TasksEvent.TaskAssignedToChanged -> state.value = state.value.copy(assignedToUserId = event.userId)
            is TasksEvent.StartDateChanged -> state.value = state.value.copy(startDate = event.value)
            is TasksEvent.StartTimeChanged -> state.value = state.value.copy(startTime = event.value)
            is TasksEvent.EndDateChanged -> state.value = state.value.copy(endDate = event.value)
            is TasksEvent.EndTimeChanged -> state.value = state.value.copy(endTime = event.value)
            is TasksEvent.DueDateChanged -> state.value = state.value.copy(dueDate = event.value)
            is TasksEvent.DueTimeChanged -> state.value = state.value.copy(dueTime = event.value)
            is TasksEvent.ReminderDateChanged -> state.value = state.value.copy(reminderDate = event.value)

            is TasksEvent.ReminderTimeChanged -> state.value = state.value.copy(reminderTime = event.value)
            is TasksEvent.LoadCreateTaskLookups -> loadCreateTaskLookups(event.token)
            is TasksEvent.SaveTask -> saveTask(event.token)
            is TasksEvent.ResetCreateTaskForm -> state.value = state.value.copy(
                subject = "", description = "", relatedRecordType = "LEAD",
                leadSearchQuery = "", leadSearchResults = emptyList(), selectedLead = null,
                priorityId = null, statusId = null, assignedToUserId = null,
                startDate = "", startTime = "10:00 AM", endDate = "", endTime = "11:00 AM",
                dueDate = "", dueTime = "06:00 PM", reminderDate = "", reminderTime = "09:30 AM",
                isSavingTask = false, saveTaskError = null, taskSaved = false,
            )
            is TasksEvent.DeleteTask -> deleteTask(event.token, event.taskId)
            is TasksEvent.ResetDeleteTaskFlag -> state.value = state.value.copy(taskDeleted = false)

            is TasksEvent.LoadTaskForEdit -> loadTaskForEdit(event.task)
            is TasksEvent.UpdateTask -> updateTask(event.token)


        }
    }

    private fun loadTaskForEdit(task: ActivityTaskData) {
        state.value = state.value.copy(
            isEditMode = true,
            editingTaskId = task.id,
            subject = task.subject,
            description = task.description,
            relatedRecordType = "LEAD",
            selectedLead = SelectedLeadRef(
                id = task.lead.id,
                leadId = task.lead.leadId,
                displayName = task.lead.leadName
            ),
            leadSearchQuery = task.lead.leadName,
            leadSearchResults = emptyList(),
            priorityId = task.priority.priorityId,
            statusId = task.status.statusId,
            assignedToUserId = task.assignedTo.userid,
            startDate = isoToDisplayDate(task.startDateTime),
            startTime = isoToDisplayTime(task.startDateTime),
            endDate = isoToDisplayDate(task.endDateTime),
            endTime = isoToDisplayTime(task.endDateTime),
            dueDate = isoToDisplayDate(task.dueDateTime),
            dueTime = isoToDisplayTime(task.dueDateTime),
            reminderDate = isoToDisplayDate(task.reminderDateTime),
            reminderTime = isoToDisplayTime(task.reminderDateTime),
            isSavingTask = false, saveTaskError = null, taskSaved = false,
            isUpdatingTask = false, updateTaskError = null, taskUpdated = false,
            editingTaskDisplayId = task.taskId.toString(),
            editingTaskCreatedOn = formatDate(task.createddatetime),
            editingTaskLastModified = formatDate(task.modifieddatetime ?: task.createddatetime),
        )
    }

    private fun updateTask(token: String) {
        val s = state.value
        val taskId = s.editingTaskId

        if (taskId.isNullOrBlank()) {
            appendToMessageQueue(UIComponent.ToastSimple("Missing task id for update"))
            return
        }
        if (s.subject.isBlank() || s.selectedLead == null || s.priorityId == null ||
            s.statusId == null || s.assignedToUserId == null ||
            s.startDate.isBlank() || s.endDate.isBlank() || s.dueDate.isBlank()
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(isUpdatingTask = true, updateTaskError = null)

            val requestDto = UpdateTaskRequestDto(
                id = taskId,
                subject = s.subject,
                description = s.description.ifBlank { null },
                priorityId = s.priorityId,
                statusId = s.statusId,
                startDateTime = displayToIso(s.startDate, s.startTime),
                endDateTime = displayToIso(s.endDate, s.endTime),
                dueDateTime = displayToIso(s.dueDate, s.dueTime),
                completionDateTime = null, // no completion UI yet — leave untouched unless you want a "mark complete" toggle
                reminderDateTime = if (s.reminderDate.isNotBlank()) displayToIso(s.reminderDate, s.reminderTime) else null,
                assignedTo = s.assignedToUserId,
                leadId = if (s.relatedRecordType == "LEAD") s.selectedLead.id else null,
                accountId = null,
                contactId = null,
            )

            taskRepository.updateTask(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isUpdatingTask = false, updateTaskError = "Failed to update task")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(isUpdatingTask = false, taskUpdated = true)
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Task updated successfully"))
                        } else {
                            state.value = state.value.copy(isUpdatingTask = false, updateTaskError = dataState.data?.message ?: "Something went wrong")
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isUpdatingTask = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun deleteTask(token: String, taskId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isDeletingTask = true, deleteTaskError = null)

            taskRepository.deleteTask(token, taskId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isDeletingTask = false, deleteTaskError = "Failed to delete task")
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isDeletingTask = false,
                                taskDeleted = true,
                                tasks = state.value.tasks.filterNot { it.id == taskId },
                            )
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data.message ?: "Task deleted successfully"))
                        } else {
                            state.value = state.value.copy(
                                isDeletingTask = false,
                                deleteTaskError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(isDeletingTask = dataState.progressBarState == ProgressBarState.Loading)
                    }


                }
            }.launchIn(viewModelScope)
        }
    }

    private fun searchLeads(token: String, query: String) {
        if (query.isBlank()) {
            state.value = state.value.copy(leadSearchResults = emptyList())
            return
        }
        viewModelScope.launch {
            taskRepository.searchLeadsForTask(token, query).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    state.value = state.value.copy(leadSearchResults = dataState.data.data)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadCreateTaskLookups(token: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingLookups = true)

            taskRepository.getActivityPriorities(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    val options = dataState.data.data.map { DropdownOption(id = it.priorityId, label = it.name) }
                    state.value = state.value.copy(priorityOptions = options)
                }
            }.launchIn(viewModelScope)

            taskRepository.getActivityStatuses(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    val options = dataState.data.data.map { DropdownOption(id = it.statusId, label = it.name) }
                    state.value = state.value.copy(statusOptions = options)
                }
            }.launchIn(viewModelScope)
            taskRepository.getUserList(token).onEach { dataState ->
                if (dataState is DataState.Data && dataState.data?.status == true) {
                    val options = dataState.data.data.map { DropdownOption(id = it.userId, label = it.fullName) }
                    state.value = state.value.copy(userOptions = options)
                }
            }.launchIn(viewModelScope)

            state.value = state.value.copy(isLoadingLookups = false)
        }
    }

    private fun saveTask(token: String) {
        val s = state.value

        if (s.subject.isBlank() || s.selectedLead == null || s.priorityId == null ||
            s.statusId == null || s.assignedToUserId == null ||
            s.startDate.isBlank() || s.endDate.isBlank() || s.dueDate.isBlank()
        ) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fill all required fields"))
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(isSavingTask = true, saveTaskError = null)

            val requestDto = CreateTaskRequestDto(
                subject = s.subject,
                description = s.description.ifBlank { null },
                relatedRecordType = s.relatedRecordType,
                leadId = s.selectedLead.leadId,
                priorityId = s.priorityId,
                statusId = s.statusId,
                assignedTo = s.assignedToUserId,
                startDateTime = "${s.startDate}T${s.startTime}", // TODO: format to real ISO once date/time pickers are wired
                endDateTime = "${s.endDate}T${s.endTime}",
                dueDateTime = "${s.dueDate}T${s.dueTime}",
                reminderDateTime = if (s.reminderDate.isNotBlank()) "${s.reminderDate}T${s.reminderTime}" else null,
            )
            taskRepository.createTask(token, requestDto).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isSavingTask = false,
                            saveTaskError = "Failed to save task"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }

                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(isSavingTask = false, taskSaved = true)
                            appendToMessageQueue(
                                UIComponent.ToastSimple(
                                    dataState.data.message ?: "Task created successfully"
                                )
                            )
                        } else {
                            state.value = state.value.copy(
                                isSavingTask = false,
                                saveTaskError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(isSavingTask = dataState.progressBarState == ProgressBarState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
            private fun fetchTasks(token: String, userCode: Int, page: Int, append: Boolean) {
        viewModelScope.launch {
            state.value = if (append) {
                state.value.copy(isLoadingMore = true)
            } else {
                state.value.copy(progressBarState = ProgressBarState.Loading)
            }

            val requestDto = TaskRequestDto(
                page = page,
                pageSize = state.value.pageSize,
                filters = TaskFilterDto(
                    search = state.value.searchQuery.ifBlank { null },
                    assignedTo = userCode,
                    priorityName = state.value.selectedPriorityName,
                ),
                sort = TaskSortDto(),
            )

            taskRepository.getTask(
                systemToken = token,
                taskReqDto = requestDto,
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> onEvent(TasksEvent.Error(dataState.uiComponent))
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            progressBarState = ProgressBarState.Idle,
                            isLoadingMore = false,
                        )
                        if (dataState.data?.status == true) {
                            val incoming = dataState.data.data
                            state.value = state.value.copy(
                                tasks = if (append) state.value.tasks + incoming else incoming,
                                page = page,
                                totalRecords = dataState.data.totalRecords,
                            )
                        } else {
                            appendToMessageQueue(UIComponent.ToastSimple(dataState.data?.message.orEmpty()))
                        }
                    }
                    is DataState.Loading -> {
                        if (!append) {
                            state.value = state.value.copy(progressBarState = dataState.progressBarState)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadTaskDetails(token: String, taskId: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoadingDetails = true, detailsError = null)

            taskRepository.getTaskDetails(token, taskId).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(
                            isLoadingDetails = false,
                            detailsError = "Failed to load task details"
                        )
                        appendToMessageQueue(dataState.uiComponent)
                    }
                    is DataState.Data -> {
                        if (dataState.data?.status == true) {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                task = dataState.data.data,
                                detailsError = null
                            )
                        } else {
                            state.value = state.value.copy(
                                isLoadingDetails = false,
                                detailsError = dataState.data?.message ?: "Something went wrong"
                            )
                        }
                    }
                    is DataState.Loading -> {
                        state.value = state.value.copy(
                            isLoadingDetails = dataState.progressBarState == ProgressBarState.Loading
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) return
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove()
            state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("AppDebug: removeHeadMessage: Nothing to remove")
        }
    }

    private fun isoToDisplayDate(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            val dt = kotlinx.datetime.LocalDateTime.parse(iso.substringBefore("."))
            val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            "${dt.dayOfMonth} $month ${dt.year}"
        } catch (e: Exception) { "" }
    }

    private fun isoToDisplayTime(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            val dt = kotlinx.datetime.LocalDateTime.parse(iso.substringBefore("."))
            val hour = if (dt.hour % 12 == 0) 12 else dt.hour % 12
            val amPm = if (dt.hour < 12) "AM" else "PM"
            val hourStr = hour.toString().padStart(2, '0')
            val minuteStr = dt.minute.toString().padStart(2, '0')
            "$hourStr:$minuteStr $amPm"
        } catch (e: Exception) { "" }
    }

    // Rebuilds "yyyy-MM-ddTHH:mm:00" from a display date "10 May 2026" + time "10:00 AM"
    private fun displayToIso(date: String, time: String): String {
        return try {
            val parts = date.trim().split(" ")
            val day = parts[0].toInt()
            val monthNames = listOf("jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec")
            val month = monthNames.indexOf(parts[1].take(3).lowercase()) + 1
            val year = parts[2].toInt()

            val timeParts = time.trim().split(" ")
            val (hh, mm) = timeParts[0].split(":").map { it.toInt() }
            val isPm = timeParts.getOrNull(1)?.uppercase() == "PM"
            val hour24 = when {
                hh == 12 && !isPm -> 0
                hh == 12 && isPm -> 12
                isPm -> hh + 12
                else -> hh
            }

            val monthStr = month.toString().padStart(2, '0')
            val dayStr = day.toString().padStart(2, '0')
            val hourStr = hour24.toString().padStart(2, '0')
            val minStr = mm.toString().padStart(2, '0')
            "$year-$monthStr-${dayStr}T$hourStr:$minStr:00"
        } catch (e: Exception) { "" }
    }
}