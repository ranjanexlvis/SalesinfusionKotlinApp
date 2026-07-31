package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.presentation.ui.calls.CallDetailsScreen
import com.example.kmmexlvistech.presentation.ui.calls.CallsScreen
import com.example.kmmexlvistech.presentation.ui.calls.CreateCallScreen
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsEvent
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsViewModel
import com.example.kmmexlvistech.presentation.ui.tasks.TaskScreen
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskViewModel
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeViewModel
import com.example.kmmexlvistech.presentation.ui.leads.CreateLeadScreen
import com.example.kmmexlvistech.presentation.ui.leads.LeadDetailsScreen
import com.example.kmmexlvistech.presentation.ui.leads.LeadsScreen
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsEvent
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsViewModel
import com.example.kmmexlvistech.presentation.ui.meetings.CreateMeetingScreen
import com.example.kmmexlvistech.presentation.ui.meetings.MeetingDetailsScreen
import com.example.kmmexlvistech.presentation.ui.meetings.MeetingsScreen
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsEvent
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsViewModel
import com.example.kmmexlvistech.presentation.ui.navigation.MainScaffold
import com.example.kmmexlvistech.presentation.ui.tasks.CreateTaskScreen
import com.example.kmmexlvistech.presentation.ui.tasks.TaskDetailsScreen
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TasksEvent
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject

@Composable
fun DashboardScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMyWork: () -> Unit,
    onNavigateToActivityLog: () -> Unit,
    onLogout: () -> Unit,
) {
    val tabNavigator = rememberNavigator()
    val appDataStoreManager: AppDataStore = koinInject()

    val currentEntry by tabNavigator.currentEntry.collectAsState(null)
    val currentRoute = currentEntry?.route?.route
    val tabRoutes = remember { BottomNavDestination.entries.map { it.route }.toSet() }

    var notificationCount by remember { mutableStateOf(0) }

      val CALLS_ROUTE = "calls_screen"
    val MEETINGS_ROUTE = "meetings_screen"


    fun routeForWidgetCode(widgetCode: String): String? = when (widgetCode) {
        "LEADS" -> BottomNavDestination.LEADS.route
        "TASKS", "TODAYS_TASKS" -> BottomNavDestination.TASKS.route
        "CALLS", "PENDING_CALLS" -> CALLS_ROUTE
        "MEETINGS", "UPCOMING_MEETINGS" -> MEETINGS_ROUTE
        else -> null
    }

    fun routeForPath(rawPath: String): String? {
        val normalized = rawPath.removePrefix("/").removePrefix("crm/")
        return when (normalized) {
            "leads/create" -> "leads/create"
            "leads" -> BottomNavDestination.LEADS.route
            "tasks/create" -> "tasks/create"
            "tasks" -> BottomNavDestination.TASKS.route
            "meetings/create" -> "meetings/create"
            "meetings" -> MEETINGS_ROUTE
            "calls/create" -> "calls/create"
            "calls" -> CALLS_ROUTE
            else -> null
        }
    }


    MainScaffold(
        currentRoute = currentRoute,
        showBottomBar = currentRoute in tabRoutes,
        onBottomNavSelected = { destination ->
            tabNavigator.navigate(destination.route)
        },
        onDrawerItemClick = { item ->
            when (item.id) {
                "home" -> tabNavigator.navigate(BottomNavDestination.HOME.route)
                "leads" -> tabNavigator.navigate(BottomNavDestination.LEADS.route)
                "tasks" -> tabNavigator.navigate(BottomNavDestination.TASKS.route)
                "calendar" -> tabNavigator.navigate(BottomNavDestination.CALENDAR.route)
                "profile" -> onNavigateToProfile()
                "logout" -> onLogout()
                else -> Unit
            }
        },
        topBar = { openDrawer ->
            // Shared across every tab — Home, Leads, Tasks, Calendar, More all
            // render under this same bar, since it lives at the parent level now.
            DashboardTopBar(
                notificationCount = notificationCount,
                onMenuClick = openDrawer,
                onNotificationClick = onNavigateToNotifications,
                onProfileClick = onNavigateToProfile,
            )
        },
    ) { openDrawer, padding ->
        NavHost(
            navigator = tabNavigator,
            initialRoute = BottomNavDestination.HOME.route,
            modifier = Modifier.padding(padding),
        ) {
            scene(route = BottomNavDestination.HOME.route) {
                val homeViewModel: HomeViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                val leadsViewModel: LeadsViewModel = koinInject()
                HomeScreen(
                    homeState = homeViewModel.state.value,
                    events = homeViewModel::onEvent,
                    systemToken = systemToken,
                    onWidgetClick = { widget ->
                        val target = routeForWidgetCode(widget.widgetCode)
                            ?: widget.routePath?.let { routeForPath(it) }
                            ?: widget.moduleCode?.let { routeForPath(it) }
                        if (!target.isNullOrBlank()) {
                            tabNavigator.navigate(target)
                        } else {
                            println("AppDebug: no route mapping found for widget=${widget.widgetCode}, path=${widget.routePath}")
                        }
                    },
                    onQuickActionClick = { action ->
                        val target = action.routePath?.let { routeForPath(it) }
                        if (target == "leads/create") {
                            leadsViewModel.onEvent(LeadsEvent.ResetCreateLeadForm)
                        }
                        if (!target.isNullOrBlank()) {
                            tabNavigator.navigate(target)
                        }
                    },
                )
            }

            scene(route = CALLS_ROUTE) {
                val callsViewModel: CallsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                CallsScreen(
                    callsState = callsViewModel.state.value,
                    events = callsViewModel::onEvent,
                    systemToken = systemToken,
                    userCode = 1021, // same static-placeholder pattern as Leads/Tasks for now
                    onCallClick = { call ->
                        callsViewModel.onEvent(CallsEvent.SelectCall(call.id))
                        tabNavigator.navigate("calls/details")
                    },
                    onDialClick = { call -> /* TODO: dialer intent — CallDto has no phone field yet */ },
                    onCallMenuClick = { call -> /* TODO: reschedule / mark complete / cancel */ },
                )
            }
            scene(route = BottomNavDestination.LEADS.route) {
                val leadsViewModel: LeadsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                LeadsScreen(
                    leadsState = leadsViewModel.state.value,
                    events = leadsViewModel::onEvent,
                    systemToken = systemToken,
                    userCode = 1021,
                    onLeadClick = { lead ->
                        leadsViewModel.onEvent(LeadsEvent.SelectLead(lead.id))
                        tabNavigator.navigate("leads/details")
                    },
                    onLeadMenuClick = { lead -> /* TODO */ },
                    onCreateLeadClick = {
                        leadsViewModel.onEvent(LeadsEvent.ResetCreateLeadForm)
                        tabNavigator.navigate("leads/create")
                    },
                )
            }
            scene(route = "leads/create") {
                val leadsViewModel: LeadsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    // No reset here anymore — the caller (Create button or Edit button) is now
                    // solely responsible for setting up state before navigating in.
                }
                LaunchedEffect(systemToken) {
                    if (systemToken.isNotEmpty()) {
                        leadsViewModel.onEvent(LeadsEvent.LoadCreateLeadLookups(systemToken))
                    }
                }

                CreateLeadScreen(
                    state = leadsViewModel.state.value,
                    events = leadsViewModel::onEvent,
                    token = systemToken,
                    onBackClick = { tabNavigator.goBack() },
                    onSaved = {
                        leadsViewModel.onEvent(LeadsEvent.ResetCreateLeadForm) // clean up after finishing, either mode
                        tabNavigator.goBack()
                    }
                )
            }

            scene(route = "leads/details") {
                val leadsViewModel: LeadsViewModel = koinInject()
                val leadId = leadsViewModel.state.value.selectedLeadId ?: ""
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }
                LaunchedEffect(systemToken, leadId) {
                    if (systemToken.isNotEmpty() && leadId.isNotEmpty()) {
                        leadsViewModel.onEvent(LeadsEvent.LoadLeadDetails(systemToken, leadId))
                    }
                }
                LaunchedEffect(leadsViewModel.state.value.leadDeleted) {
                    if (leadsViewModel.state.value.leadDeleted) {
                        leadsViewModel.onEvent(LeadsEvent.ResetDeleteLeadFlag)
                        tabNavigator.goBack()
                    }
                }

                LeadDetailsScreen(
                    state = leadsViewModel.state.value,
                    onBackClick = { tabNavigator.goBack() },
                    onEditClick = {
                        leadsViewModel.state.value.lead?.let { lead ->
                            leadsViewModel.onEvent(LeadsEvent.LoadLeadForEdit(lead))
                        }
                        tabNavigator.navigate("leads/create")
                    },
                    onDeleteConfirmed = { leadsViewModel.onEvent(LeadsEvent.DeleteLead(systemToken, leadId)) },
                    onAssignedToClick = { /* TODO */ },
                    onRemoveHeadFromQueue = { leadsViewModel.onEvent(LeadsEvent.OnRemoveHeadFromQueue) },
                )
            }


            scene(route = BottomNavDestination.TASKS.route) {
                val taskViewModel: TaskViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }
                var userCode by remember { mutableStateOf(0) }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""

                }

                TaskScreen(
                    taskState = taskViewModel.state.value,
                    events = taskViewModel::onEvent,
                    systemToken = systemToken,
                    userCode = 1021,
                    onTaskClick = { task ->
                        taskViewModel.onEvent(TasksEvent.SelectTask(task.id.toString()))
                        tabNavigator.navigate("tasks/details")
                    },
                    onTaskMenuClick = { task -> /* TODO: mark complete / edit / delete */ },
                )


            }
            scene(route = "tasks/create") {
                val taskViewModel: TaskViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    // No unconditional reset here — Create button and Edit button are each
                    // responsible for setting up state before navigating in (same fix as Leads).
                }
                LaunchedEffect(systemToken) {
                    if (systemToken.isNotEmpty()) {
                        taskViewModel.onEvent(TasksEvent.LoadCreateTaskLookups(systemToken))
                    }
                }

                CreateTaskScreen(
                    state = taskViewModel.state.value,
                    events = taskViewModel::onEvent,
                    token = systemToken,
                    onBackClick = { tabNavigator.goBack() },
                    onSaved = {
                        taskViewModel.onEvent(TasksEvent.ResetCreateTaskForm)
                        tabNavigator.goBack()
                    }
                )
            }

            scene(route = "meetings/create") {
                val meetingsViewModel: MeetingsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    meetingsViewModel.onEvent(MeetingsEvent.ResetCreateMeetingForm)
                }
                LaunchedEffect(systemToken) {
                    if (systemToken.isNotEmpty()) {
                        meetingsViewModel.onEvent(MeetingsEvent.LoadCreateMeetingLookups(systemToken))
                    }
                }

                CreateMeetingScreen(
                    state = meetingsViewModel.state.value,
                    events = meetingsViewModel::onEvent,
                    token = systemToken,
                    onBackClick = { tabNavigator.goBack() },
                    onSaved = { tabNavigator.goBack() }
                )
            }

            scene(route = "calls/details") {
                val callsViewModel: CallsViewModel = koinInject()
                val callId = callsViewModel.state.value.selectedCallId ?: ""
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }
                LaunchedEffect(systemToken, callId) {
                    if (systemToken.isNotEmpty() && callId.isNotEmpty()) {
                        callsViewModel.onEvent(CallsEvent.LoadCallDetails(systemToken, callId))
                    }
                }
                LaunchedEffect(callsViewModel.state.value.callDeleted) {
                    if (callsViewModel.state.value.callDeleted) {
                        callsViewModel.onEvent(CallsEvent.ResetDeleteCallFlag)
                        tabNavigator.goBack()
                    }
                }

                CallDetailsScreen(
                    state = callsViewModel.state.value,
                    onBackClick = { tabNavigator.goBack() },
                    onEditClick = { /* TODO */ },
                    onDeleteConfirmed = { callsViewModel.onEvent(CallsEvent.DeleteCall(systemToken, callId)) },
                    onRelatedToClick = { tabNavigator.navigate("leads/details") },
                    onRemoveHeadFromQueue = { callsViewModel.onEvent(CallsEvent.OnRemoveHeadFromQueue) },
                )
            }

            scene(route = "tasks/details") {
                val taskViewModel: TaskViewModel = koinInject()
                val taskId = taskViewModel.state.value.selectedTaskId ?: ""
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }
                LaunchedEffect(systemToken, taskId) {
                    if (systemToken.isNotEmpty() && taskId.isNotEmpty()) {
                        taskViewModel.onEvent(TasksEvent.LoadTaskDetails(systemToken, taskId))
                    }
                }
                LaunchedEffect(taskViewModel.state.value.taskDeleted) {
                    if (taskViewModel.state.value.taskDeleted) {
                        taskViewModel.onEvent(TasksEvent.ResetDeleteTaskFlag)
                        tabNavigator.goBack()
                    }
                }

                // In "tasks/details" scene — wire the edit click
                TaskDetailsScreen(
                    state = taskViewModel.state.value,
                    onBackClick = { tabNavigator.goBack() },
                    onEditClick = {
                        taskViewModel.state.value.task?.let { task ->
                            taskViewModel.onEvent(TasksEvent.LoadTaskForEdit(task))
                        }
                        tabNavigator.navigate("tasks/create")
                    },
                    onDeleteConfirmed = { taskViewModel.onEvent(TasksEvent.DeleteTask(systemToken, taskId)) },
                    onRemoveHeadFromQueue = { taskViewModel.onEvent(TasksEvent.OnRemoveHeadFromQueue) },
                )
            }

            scene(route = MEETINGS_ROUTE) {
                val meetingsViewModel: MeetingsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                MeetingsScreen(
                    meetingsState = meetingsViewModel.state.value,
                    events = meetingsViewModel::onEvent,
                    systemToken = systemToken,
                    userCode = 1021, // same placeholder as Leads/Tasks/Calls
                    onMeetingClick = { meeting ->
                        println("@@@meeting${meeting.id}")
                        meetingsViewModel.onEvent(MeetingsEvent.SelectMeeting(meeting.id))
                        tabNavigator.navigate("meetings/details")
                    },
                    onJoinClick = { meeting -> /* TODO: open meeting.meetingLink in browser/webview */ },
                    onMeetingMenuClick = { meeting -> /* TODO: reschedule / cancel */ },
                )
            }

            scene(route = "calls/create") {
                val callsViewModel: CallsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    callsViewModel.onEvent(CallsEvent.ResetCreateCallForm)
                }
                LaunchedEffect(systemToken) {
                    if (systemToken.isNotEmpty()) {
                        callsViewModel.onEvent(CallsEvent.LoadCreateCallLookups(systemToken))
                    }
                }

                CreateCallScreen(
                    state = callsViewModel.state.value,
                    events = callsViewModel::onEvent,
                    token = systemToken,
                    onBackClick = { tabNavigator.goBack() },
                    onSaved = { tabNavigator.goBack() }
                )
            }

            scene(route = "meetings/details") {
                val meetingsViewModel: MeetingsViewModel = koinInject()
                val meetingId = meetingsViewModel.state.value.selectedMeetingId ?: ""
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }
                LaunchedEffect(systemToken, meetingId) {
                    if (systemToken.isNotEmpty() && meetingId.isNotEmpty()) {
                        meetingsViewModel.onEvent(MeetingsEvent.LoadMeetingDetails(systemToken, meetingId))
                    }
                }
                LaunchedEffect(meetingsViewModel.state.value.meetingDeleted) {
                    if (meetingsViewModel.state.value.meetingDeleted) {
                        meetingsViewModel.onEvent(MeetingsEvent.ResetDeleteMeetingFlag)
                        tabNavigator.goBack()
                    }
                }

                MeetingDetailsScreen(
                    state = meetingsViewModel.state.value,
                    onBackClick = { tabNavigator.goBack() },
                    onEditClick = { /* TODO */ },
                    onDeleteConfirmed = { meetingsViewModel.onEvent(MeetingsEvent.DeleteMeeting(systemToken, meetingId)) },
                    onRelatedToClick = { tabNavigator.navigate("leads/details") },
                    onRemoveHeadFromQueue = { meetingsViewModel.onEvent(MeetingsEvent.OnRemoveHeadFromQueue) },
                )
            }

            scene(route = BottomNavDestination.CALENDAR.route) {
                PlaceholderTabScreen("Calendar")
            }
            scene(route = BottomNavDestination.MORE.route) {
                PlaceholderTabScreen("More")
            }
        }
    }
}



@Composable
private fun PlaceholderTabScreen(label: String) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        androidx.compose.material3.Text("$label — coming soon")
    }
}