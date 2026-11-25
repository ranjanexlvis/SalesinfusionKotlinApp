package com.example.kmmexlvistech.presentation.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.business.datasource.models.MenuItemDTO
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.presentation.ui.accounts.AccountDetailsScreen
import com.example.kmmexlvistech.presentation.ui.accounts.AccountsScreen
import com.example.kmmexlvistech.presentation.ui.accounts.CreateAccountScreen
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsEvent
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsViewModel
import com.example.kmmexlvistech.presentation.ui.calls.CallDetailsScreen
import com.example.kmmexlvistech.presentation.ui.calls.CallsScreen
import com.example.kmmexlvistech.presentation.ui.calls.CreateCallScreen
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsEvent
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsViewModel
import com.example.kmmexlvistech.presentation.ui.contacts.ContactDetailsScreen
import com.example.kmmexlvistech.presentation.ui.contacts.ContactsScreen
import com.example.kmmexlvistech.presentation.ui.contacts.CreateContactScreen
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsEvent
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsViewModel
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.DashboardEvent
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.DashboardViewModel
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
import com.example.kmmexlvistech.presentation.ui.profile.EditProfileScreen
import com.example.kmmexlvistech.presentation.ui.profile.ProfileScreen
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileEvent
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileViewModel
import com.example.kmmexlvistech.presentation.ui.report.ActivityReportScreen
import com.example.kmmexlvistech.presentation.ui.report.viewmodel.ActivityReportViewModel
import com.example.kmmexlvistech.presentation.ui.tasks.CreateTaskScreen
import com.example.kmmexlvistech.presentation.ui.tasks.TaskDetailsScreen
import com.example.kmmexlvistech.presentation.ui.tasks.TaskScreen
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskViewModel
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TasksEvent
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject


private const val CALLS_ROUTE = "calls_screen"
private const val MEETINGS_ROUTE = "meetings_screen"     // const, and private like the others
private const val ACCOUNTS_ROUTE = "accounts"

private const val CONTACTS_ROUTE = "contacts"
private const val CONTACT_DETAILS_ROUTE = "contacts/details"
private const val CREATE_CONTACT_ROUTE = "contacts/create"
private const val ACTIVITY_REPORT_ROUTE = "activity_reports"
private  val PROFILE_ROUTE = BottomNavDestination.MORE.route

private fun routeForWidgetCode(widgetCode: String): String? = when (widgetCode) {
    "LEADS" -> BottomNavDestination.LEADS.route
    "TASKS", "TODAYS_TASKS" -> BottomNavDestination.TASKS.route
    "CALLS", "PENDING_CALLS" -> CALLS_ROUTE
    "MEETINGS", "UPCOMING_MEETINGS" -> MEETINGS_ROUTE
    "PROFILE" -> PROFILE_ROUTE
    "ACCOUNTS" -> ACCOUNTS_ROUTE
    else -> null
}


private fun routeForPath(rawPath: String): String? {
    val normalized = rawPath.removePrefix("/").removePrefix("crm/").removePrefix("activities/")
    return when (normalized) {
        "leads/create" -> "leads/create"
        "leads" -> BottomNavDestination.LEADS.route
        "tasks/create" -> "tasks/create"
        "tasks" -> BottomNavDestination.TASKS.route
        "meetings/create" -> "meetings/create"
        "meetings" -> MEETINGS_ROUTE
        "calls/create" -> "calls/create"
        "calls" -> CALLS_ROUTE
        "accounts" -> ACCOUNTS_ROUTE
        else -> null
    }
}

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
    val menuViewModel: DashboardViewModel = koinInject()

    val currentEntry by tabNavigator.currentEntry.collectAsState(null)
    val currentRoute = currentEntry?.route?.route
    val tabRoutes = remember { BottomNavDestination.entries.map { it.route }.toSet() }

    var notificationCount by remember { mutableStateOf(0) }
    val routesWithOwnTopBar = setOf(
        BottomNavDestination.MORE.route,
        "profile/edit",
        ACTIVITY_REPORT_ROUTE
    )



    // ---- Dynamic drawer menu ----
    LaunchedEffect(Unit) {
        val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
        println("@@token$token")
        if (token.isNotBlank()) menuViewModel.onEvent(DashboardEvent.GetDashboardMenu(token))
    }

    val menuResponse = menuViewModel.state.value.getDashboard
    val drawerMenus = remember(menuResponse) { menuResponse.data.forDrawer() }
    println("@@@@@menuResponse"+drawerMenus)

    val selectedMenuCode = when (currentRoute) {
        ACTIVITY_REPORT_ROUTE -> "ACTIVITY_REPORTS"
        BottomNavDestination.HOME.route -> "DASHBOARD"
        BottomNavDestination.LEADS.route, "leads/details", "leads/create" -> "CRM_LEADS"
        ACCOUNTS_ROUTE, "accounts/details", "accounts/create" -> "CRM_ACCOUNTS"
        CONTACTS_ROUTE, "contacts/details", "contacts/create" -> "CRM_CONTACTS"
        BottomNavDestination.TASKS.route, "tasks/details", "tasks/create" -> "TASKS"
        MEETINGS_ROUTE, "meetings/details", "meetings/create" -> "MEETINGS"
        CALLS_ROUTE, "calls/details", "calls/create" -> "CALLS"
        BottomNavDestination.MORE.route, "profile/edit" -> "MY_PROFILE"
        else -> null
    }

    MainScaffold(
        currentRoute = currentRoute,
        showBottomBar = currentRoute in tabRoutes,
        onBottomNavSelected = { destination -> tabNavigator.navigate(destination.route) },
        drawerContent = { closeDrawer ->
            DynamicDrawerContent(
                menus = drawerMenus,
                selectedCode = selectedMenuCode,
                onItemClick = { item ->
                    closeDrawer()
                    println("@@@@@@"+item)
                    when (item.menuCode) {
                        "ACTIVITY_REPORTS" -> tabNavigator.navigate(ACTIVITY_REPORT_ROUTE)
                        "DASHBOARD" -> tabNavigator.navigate(BottomNavDestination.HOME.route)
                        "CRM_LEADS" -> tabNavigator.navigate(BottomNavDestination.LEADS.route)
                        "TASKS" -> tabNavigator.navigate(BottomNavDestination.TASKS.route)
                        "MEETINGS" -> tabNavigator.navigate(MEETINGS_ROUTE)
                        "CALLS" -> tabNavigator.navigate(CALLS_ROUTE)
                        "MY_PROFILE" -> tabNavigator.navigate(PROFILE_ROUTE)
                        "LOGOUT" -> onLogout()
                        "CRM_ACCOUNTS" -> tabNavigator.navigate(ACCOUNTS_ROUTE)
                        "CRM_CONTACTS" -> tabNavigator.navigate(CONTACTS_ROUTE)
                        else -> Unit
                    }
                },
            )
        },

    topBar = { openDrawer ->
        if (currentRoute !in routesWithOwnTopBar) {
            DashboardTopBar(
                notificationCount = notificationCount,
                onMenuClick = openDrawer,
                onNotificationClick = onNavigateToNotifications,
                onProfileClick = { tabNavigator.navigate(BottomNavDestination.MORE.route) },
            )
        }
    },
    ) { openDrawer, padding ->
        NavHost(
            navigator = tabNavigator,
            initialRoute = BottomNavDestination.HOME.route,
            modifier = Modifier.padding(padding),
        ) {
            scene(route = BottomNavDestination.HOME.route) {
                val homeViewModel: HomeViewModel = koinInject()
                val leadsViewModel: LeadsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                HomeScreen(
                    homeState = homeViewModel.state.value,
                    events = homeViewModel::onEvent,
                    systemToken = systemToken,
                    onCodeClick = { code ->
                        val target = routeForWidgetCode(code)
                        if (!target.isNullOrBlank()) {
                            tabNavigator.navigate(target)
                        } else {
                            println("AppDebug: no route mapping found for code=$code")
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

            scene(route = CONTACTS_ROUTE) {
                val contactsViewModel: ContactsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken =
                        appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                LaunchedEffect(systemToken) {
                    if (systemToken.isNotBlank()) {
                        contactsViewModel.onEvent(
                            ContactsEvent.GetContacts(systemToken)
                        )
                        contactsViewModel.onEvent(
                            ContactsEvent.LoadLookups(systemToken)
                        )
                    }
                }

                ContactsScreen(
                    state = contactsViewModel.state.value,
                    events = contactsViewModel::onEvent,
                    systemToken = systemToken,
                    onMenuClick = openDrawer,
                    onContactClick = { contact ->
                        contactsViewModel.onEvent(
                            ContactsEvent.SelectContact(contact.id)
                        )
                        tabNavigator.navigate(CONTACT_DETAILS_ROUTE)
                    },
                    onCreateClick = {
                        contactsViewModel.onEvent(
                            ContactsEvent.ResetContactForm
                        )
                        tabNavigator.navigate(CREATE_CONTACT_ROUTE)
                    },
                    onEditClick = { contact ->
                        contactsViewModel.onEvent(
                            ContactsEvent.LoadContactForEdit(contact)
                        )
                        tabNavigator.navigate(CREATE_CONTACT_ROUTE)
                    }
                )
            }

            scene(route = CREATE_CONTACT_ROUTE) {
                val contactsViewModel: ContactsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken =
                        appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                LaunchedEffect(systemToken) {
                    if (systemToken.isNotBlank()) {
                        contactsViewModel.onEvent(
                            ContactsEvent.LoadLookups(systemToken)
                        )
                    }
                }

                CreateContactScreen(
                    state = contactsViewModel.state.value,
                    events = contactsViewModel::onEvent,
                    token = systemToken,
                    onBackClick = {
                        tabNavigator.goBack()
                    },
                    onSaved = {
                        contactsViewModel.onEvent(
                            ContactsEvent.ResetContactForm
                        )
                        tabNavigator.goBack()
                        contactsViewModel.onEvent(
                            ContactsEvent.GetContacts(systemToken)
                        )
                    }
                )
            }

            scene(route = CONTACT_DETAILS_ROUTE) {
                val contactsViewModel: ContactsViewModel = koinInject()
                val contactId =
                    contactsViewModel.state.value.selectedContactId ?: ""

                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken =
                        appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                LaunchedEffect(systemToken, contactId) {
                    if (systemToken.isNotBlank() && contactId.isNotBlank()) {
                        contactsViewModel.onEvent(
                            ContactsEvent.LoadContactDetails(
                                token = systemToken,
                                id = contactId
                            )
                        )
                    }
                }

                ContactDetailsScreen(
                    state = contactsViewModel.state.value,
                    onBackClick = {
                        tabNavigator.goBack()
                    },
                    onEditClick = {
                        contactsViewModel.state.value.contact?.let { contact ->
                            contactsViewModel.onEvent(
                                ContactsEvent.LoadContactForEdit(contact)
                            )
                            tabNavigator.navigate(CREATE_CONTACT_ROUTE)
                        }
                    },
                    onDeleteConfirmed = {
                        if (systemToken.isNotBlank() && contactId.isNotBlank()) {
                            contactsViewModel.onEvent(
                                ContactsEvent.DeleteContact(systemToken, contactId)
                            )
                        }
                    },
                    onRemoveHeadFromQueue = {
                        contactsViewModel.onEvent(
                            ContactsEvent.OnRemoveHeadFromQueue
                        )
                    },
                    onTryAgain = {
                        if (systemToken.isNotBlank() && contactId.isNotBlank()) {
                            contactsViewModel.onEvent(
                                ContactsEvent.LoadContactDetails(
                                    systemToken,
                                    contactId
                                )
                            )
                        }
                    }
                )
            }

            scene(route = ACCOUNTS_ROUTE) {
                val vm: AccountsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }
                LaunchedEffect(Unit) { systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: "" }

                AccountsScreen(
                    state = vm.state.value,
                    events = vm::onEvent,
                    systemToken = systemToken,
                    onMenuClick = openDrawer,
                    onAccountClick = { account ->
                        vm.onEvent(AccountsEvent.SelectAccount(account.id))
                        tabNavigator.navigate("accounts/details")
                    },
                    onCreateClick = {
                        vm.onEvent(AccountsEvent.ResetAccountForm)
                        tabNavigator.navigate("accounts/create")
                    },
                    onEditClick = { account ->
                        vm.onEvent(AccountsEvent.LoadAccountForEdit(account))
                        tabNavigator.navigate("accounts/create")
                    },
                )
            }

            scene(route = ACTIVITY_REPORT_ROUTE) {
                val reportViewModel: ActivityReportViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                if (systemToken.isNotBlank()) {
                    ActivityReportScreen(
                        token = systemToken,
                        viewModel = reportViewModel,
                        // lookups = FilterLookups(...)  // see below
                        onBack = { tabNavigator.goBack() },
                        onViewReport = { type, filters ->
                            // later: navigate to the detailed report list
                            println("@@view report: $type $filters")
                        }
                    )
                }
            }

            scene(route = "accounts/create") {
                val vm: AccountsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    // never wipe data that Edit just loaded
                    if (!vm.state.value.isEditMode) vm.onEvent(AccountsEvent.ResetAccountForm)
                }
                LaunchedEffect(systemToken) {
                    if (systemToken.isNotEmpty()) vm.onEvent(AccountsEvent.LoadLookups(systemToken))
                }

                CreateAccountScreen(
                    state = vm.state.value,
                    events = vm::onEvent,
                    token = systemToken,
                    onBackClick = { tabNavigator.goBack() },
                    onSaved = { tabNavigator.goBack() },
                )
            }

            scene(route = "accounts/details") {
                val vm: AccountsViewModel = koinInject()
                val accountId = vm.state.value.selectedAccountId ?: ""
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) { systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: "" }
                LaunchedEffect(systemToken, accountId) {
                    if (systemToken.isNotEmpty() && accountId.isNotEmpty()) {
                        vm.onEvent(AccountsEvent.LoadAccountDetails(systemToken, accountId))
                    }
                }
                LaunchedEffect(vm.state.value.accountDeleted) {
                    if (vm.state.value.accountDeleted) {
                        vm.onEvent(AccountsEvent.ResetDeleteFlag)
                        tabNavigator.goBack()
                    }
                }

                AccountDetailsScreen(
                    state = vm.state.value,
                    onBackClick = { tabNavigator.goBack() },
                    onEditClick = {
                        vm.state.value.account?.let { vm.onEvent(AccountsEvent.LoadAccountForEdit(it)) }
                        tabNavigator.navigate("accounts/create")
                    },
                    onDeleteConfirmed = { vm.onEvent(AccountsEvent.DeleteAccount(systemToken, accountId)) },
                    onRemoveHeadFromQueue = { vm.onEvent(AccountsEvent.OnRemoveHeadFromQueue) },
                    onTryAgain = { vm.onEvent(AccountsEvent.LoadAccountDetails(systemToken, accountId)) },
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
                    userCode = 1021,
                    onCallClick = { call ->
                        callsViewModel.onEvent(CallsEvent.SelectCall(call.id))
                        tabNavigator.navigate("calls/details")
                    },
                    onDialClick = { /* TODO: dialer intent */ },
                    onCallMenuClick = { /* TODO */ },
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
                    onLeadMenuClick = { /* TODO */ },
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
                        leadsViewModel.onEvent(LeadsEvent.ResetCreateLeadForm)
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
                    onTaskMenuClick = { /* TODO */ },
                )
            }

            scene(route = "tasks/create") {
                val taskViewModel: TaskViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
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
                    userCode = 1021,
                    onMeetingClick = { meeting ->
                        meetingsViewModel.onEvent(MeetingsEvent.SelectMeeting(meeting.id))
                        tabNavigator.navigate("meetings/details")
                    },
                    onJoinClick = { /* TODO: open meeting link */ },
                    onMeetingMenuClick = { /* TODO */ },
                )
            }

            scene(route = "meetings/create") {
                val meetingsViewModel: MeetingsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    if (!meetingsViewModel.state.value.isEditMode) {
                        meetingsViewModel.onEvent(MeetingsEvent.ResetCreateMeetingForm)
                    }
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
                    onSaved = { tabNavigator.goBack() },   // the screen resets the form itself
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
                    onEditClick = {
                        meetingsViewModel.state.value.meeting?.let { meeting ->
                            meetingsViewModel.onEvent(MeetingsEvent.LoadMeetingForEdit(meeting))
                        }
                        tabNavigator.navigate("meetings/create")
                    },
                    onDeleteConfirmed = { meetingsViewModel.onEvent(MeetingsEvent.DeleteMeeting(systemToken, meetingId)) },
                    onRelatedToClick = { tabNavigator.navigate("leads/details") },
                    onRemoveHeadFromQueue = { meetingsViewModel.onEvent(MeetingsEvent.OnRemoveHeadFromQueue) },
                )
            }

            scene(route = "calls/create") {
                val callsViewModel: CallsViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                    // Reset only when creating. Never wipe data that Edit just loaded.
                    if (!callsViewModel.state.value.isEditMode) {
                        callsViewModel.onEvent(CallsEvent.ResetCreateCallForm)
                    }
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
                    onSaved = {
                        callsViewModel.onEvent(CallsEvent.ResetCreateCallForm)
                        tabNavigator.goBack()
                    },
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
                    onEditClick = {
                        callsViewModel.state.value.call?.let { call ->
                            callsViewModel.onEvent(CallsEvent.LoadCallForEdit(call))
                        }
                        tabNavigator.navigate("calls/create")
                    },
                    onDeleteConfirmed = { callsViewModel.onEvent(CallsEvent.DeleteCall(systemToken, callId)) },
                    onRelatedToClick = { tabNavigator.navigate("leads/details") },
                    onRemoveHeadFromQueue = { callsViewModel.onEvent(CallsEvent.OnRemoveHeadFromQueue) },
                )
            }

            scene(route = BottomNavDestination.CALENDAR.route) { PlaceholderTabScreen("Calendar") }
            scene(route = BottomNavDestination.MORE.route) {
                val profileViewModel: ProfileViewModel = koinInject()
                val homeViewModel: HomeViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                ProfileScreen(
                    state = profileViewModel.state.value,
                    events = profileViewModel::onEvent,
                    systemToken = systemToken,
                    // ASSUMPTION: the field name from the HomeScreen change earlier
                    stats = homeViewModel.state.value.getDashboard.data?.myWork,
                    onMenuClick = openDrawer,
                    onEditClick = {
                        profileViewModel.onEvent(ProfileEvent.LoadProfileForEdit)
                        tabNavigator.navigate("profile/edit")
                    },
                    onChangePasswordClick = { /* TODO: change password screen */ },
                )
            }

            scene(route = "profile/edit") {
                val profileViewModel: ProfileViewModel = koinInject()
                var systemToken by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    systemToken = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
                }

                EditProfileScreen(
                    state = profileViewModel.state.value,
                    events = profileViewModel::onEvent,
                    token = systemToken,
                    onBackClick = { tabNavigator.goBack() },
                    onSaved = {
                        profileViewModel.onEvent(ProfileEvent.ResetProfileSaved)
                        tabNavigator.goBack()
                    },
                )
            }
        }
    }
}

// ======================= Scaffold with drawer =======================

@Composable
fun MainScaffold(
    currentRoute: String?,
    showBottomBar: Boolean,
    onBottomNavSelected: (BottomNavDestination) -> Unit,
    drawerContent: @Composable (closeDrawer: () -> Unit) -> Unit,
    topBar: @Composable (openDrawer: () -> Unit) -> Unit,
    content: @Composable (openDrawer: () -> Unit, padding: PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val open: () -> Unit = { scope.launch { drawerState.open() } }
    val close: () -> Unit = { scope.launch { drawerState.close() } }
    val colors = MaterialTheme.colorScheme

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = colors.surface,
                drawerContentColor = colors.onSurface,
            ) { drawerContent(close) }
        },
    ) {
        Scaffold(
            containerColor = colors.background,
            contentColor = colors.onBackground,
            topBar = { topBar(open) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = colors.surface,
                        contentColor = colors.onSurface,
                    ) {
                        BottomNavDestination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = currentRoute == destination.route,
                                onClick = { onBottomNavSelected(destination) },
                                // ASSUMPTION: BottomNavDestination has `icon` (ImageVector) and `label`.
                                // Rename to match your enum if different.
                                icon = { Icon(destination.selectedIcon, contentDescription = destination.label) },
                                label = { Text(destination.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colors.onPrimaryContainer,
                                    selectedTextColor = colors.primary,
                                    indicatorColor = colors.primaryContainer,
                                    unselectedIconColor = colors.onSurfaceVariant,
                                    unselectedTextColor = colors.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            },
        ) { padding -> content(open, padding) }
    }
}

// ======================= Drawer UI =======================

@Composable
fun DynamicDrawerContent(
    menus: List<MenuItemDTO>,
    selectedCode: String?,
    onItemClick: (MenuItemDTO) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val expanded = remember { mutableStateMapOf<Int, Boolean>() }

    // Auto-open the group containing the selected item
    LaunchedEffect(menus, selectedCode) {
        fun open(items: List<MenuItemDTO>): Boolean = items.any { item ->
            val hit = item.menuCode == selectedCode || open(item.children)
            if (hit && item.children.isNotEmpty()) expanded[item.menuId] = true
            hit
        }
        open(menus)
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).background(colors.primary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("SI", color = colors.onPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text("Sales Infusion", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = colors.onSurface)
        }
        HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            menus.forEach { item ->
                DrawerItem(item, depth = 0, expanded = expanded, selectedCode = selectedCode, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
private fun DrawerItem(
    item: MenuItemDTO,
    depth: Int,
    expanded: MutableMap<Int, Boolean>,
    selectedCode: String?,
    onItemClick: (MenuItemDTO) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val isGroup = item.children.isNotEmpty()
    val isOpen = expanded[item.menuId] == true
    val isSelected = !isGroup && item.menuCode == selectedCode
    val chevron by animateFloatAsState(if (isOpen) 180f else 0f, label = "chevron")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.primaryContainer else Color.Transparent)
            .clickable { if (isGroup) expanded[item.menuId] = !isOpen else onItemClick(item) }
            .padding(start = (12 + depth * 16).dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            iconForMenu(item.icon),
            contentDescription = null,
            tint = if (isSelected) colors.onPrimaryContainer else colors.onSurfaceVariant,
            modifier = Modifier.size(if (depth == 0) 22.dp else 18.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            item.menuName,
            modifier = Modifier.weight(1f),
            fontSize = if (depth == 0) 15.sp else 14.sp,
            fontWeight = if (isSelected || (isGroup && isOpen)) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) colors.onPrimaryContainer else colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (isGroup) {
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = if (isOpen) "Collapse" else "Expand",
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp).rotate(chevron),
            )
        }
    }

    if (isGroup) {
        AnimatedVisibility(
            visible = isOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item.children.forEach { child ->
                    DrawerItem(child, depth + 1, expanded, selectedCode, onItemClick)
                }
            }
        }
    }
}



fun List<MenuItemDTO>.forDrawer(): List<MenuItemDTO> =
    filter { item ->
        item.isVisible && item.showOnMobile
    }.mapNotNull { item ->
        if (item.children.isNotEmpty()) {
            val visibleChildren = item.children.forDrawer()

            if (visibleChildren.isNotEmpty()) {
                item.copy(children = visibleChildren)
            } else {
                null
            }
        } else {
            // Show every API menu item that is enabled for mobile.
            item
        }
    }

fun iconForMenu(icon: String?): ImageVector = when (icon) {
    "fa fa-gauge-high" -> Icons.Default.Dashboard
    "fa fa-users" -> Icons.Default.Groups
    "fa fa-user-plus" -> Icons.Default.PersonAdd
    "fa fa-building" -> Icons.Default.Business
    "fa fa-address-book" -> Icons.Default.Contacts
    "fa fa-bullhorn" -> Icons.Default.Campaign
    "fa fa-list-check" -> Icons.Default.Checklist
    "fa fa-square-check" -> Icons.Default.CheckBox
    "fa fa-calendar-days" -> Icons.Default.CalendarMonth
    "fa fa-phone" -> Icons.Default.Call
    "fa fa-user-gear" -> Icons.Default.ManageAccounts
    "fa fa-id-card" -> Icons.Default.Badge
    "fa fa-gear" -> Icons.Default.Settings
    "fa fa-right-from-bracket" -> Icons.Default.Logout
    "fa fa-chart-line", "fa fa-chart-pie" -> Icons.Default.BarChart
    "fa fa-user-shield" -> Icons.Default.AdminPanelSettings
    "fa fa-box-open", "fa fa-box" -> Icons.Default.Inventory2
    "fa fa-file-invoice-dollar", "fa fa-file-invoice" -> Icons.Default.Receipt
    else -> Icons.Default.FiberManualRecord
}

@Composable
private fun PlaceholderTabScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text("$label — coming soon", color = MaterialTheme.colorScheme.onBackground)
    }
}