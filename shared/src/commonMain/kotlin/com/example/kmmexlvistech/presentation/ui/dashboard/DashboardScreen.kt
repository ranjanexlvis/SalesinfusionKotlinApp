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
import com.example.kmmexlvistech.presentation.ui.calls.CallsScreen
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsViewModel
import com.example.kmmexlvistech.presentation.ui.tasks.TaskScreen
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskViewModel
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeViewModel
import com.example.kmmexlvistech.presentation.ui.leads.LeadDetailsScreen
import com.example.kmmexlvistech.presentation.ui.leads.LeadsScreen
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsEvent
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsViewModel
import com.example.kmmexlvistech.presentation.ui.meetings.MeetingsScreen
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsViewModel
import com.example.kmmexlvistech.presentation.ui.navigation.MainScaffold
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

                HomeScreen(
                    homeState = homeViewModel.state.value,
                    events = homeViewModel::onEvent,
                    systemToken = systemToken,
                    onWidgetClick = { widget ->
                        val target = routeForWidgetCode(widget.widgetCode) ?: widget.routePath ?: widget.moduleCode
                        if (!target.isNullOrBlank()) tabNavigator.navigate(target)
                    },
                    onQuickActionClick = { action ->
                        if (!action.routePath.isNullOrBlank()) tabNavigator.navigate(action.routePath)
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
                    onCallClick = { call -> tabNavigator.navigate("calls/${call.callId}") },
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
                        println("AppDebug: setting leadId = ${lead.id}, vm=$leadsViewModel")
                        leadsViewModel.onEvent(LeadsEvent.SelectLead(lead.id.toString()))
                        println("AppDebug: immediately after set, state.selectedLeadId = ${leadsViewModel.state.value.selectedLeadId}")
                        tabNavigator.navigate("leads/details")
                    },
                    onLeadMenuClick = { lead -> /* TODO */ },
                )
            }

            scene(route = "leads/details") {
                val leadsViewModel: LeadsViewModel = koinInject()
                println("AppDebug: details scene vm=$leadsViewModel")
                val leadId = leadsViewModel.state.value.selectedLeadId ?: ""
                println("AppDebug: leadId on details scene = '$leadId'")
                var systemToken by remember { mutableStateOf("") }

                println("AppDebug: leadId on details scene = '$leadId'")

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }
                LaunchedEffect(systemToken, leadId) {
                    if (systemToken.isNotEmpty() && leadId.isNotEmpty()) {
                        leadsViewModel.onEvent(LeadsEvent.LoadLeadDetails(systemToken, leadId))
                    }
                }

                LeadDetailsScreen(
                    state = leadsViewModel.state.value,
                    onBackClick = { tabNavigator.goBack() },
                    onEditClick = { /* TODO */ },
                    onDeleteClick = { /* TODO */ },
                    onAssignedToClick = { /* TODO */ }
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
                    userCode = userCode,
                    onTaskClick = { task -> tabNavigator.navigate("tasks/${task.taskId}") },
                    onTaskMenuClick = { task -> /* TODO: mark complete / edit / delete */ },
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
                    onMeetingClick = { meeting -> tabNavigator.navigate("meetings/${meeting.meetingId}") },
                    onJoinClick = { meeting -> /* TODO: open meeting.meetingLink in browser/webview */ },
                    onMeetingMenuClick = { meeting -> /* TODO: reschedule / cancel */ },
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