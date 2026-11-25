package com.example.kmmexlvistech.di

import com.example.kmmexlvistech.AppContext
import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.business.core.AppDataStoreManager
import com.example.kmmexlvistech.business.core.KtorHttpClient
import com.example.kmmexlvistech.business.datasource.impl.AccountsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.CallsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.CentralCrmServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.ContactsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.DashboardServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.HomeServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.LeadsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.MeetingsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.ProfileServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.SplashServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.TaskServiceImpl
import com.example.kmmexlvistech.business.datasource.models.GeoInfoProvider
import com.example.kmmexlvistech.business.datasource.services.AccountsApiServices
import com.example.kmmexlvistech.business.datasource.services.ActivityReportService
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
import com.example.kmmexlvistech.business.datasource.services.CentralCrmApiServices
import com.example.kmmexlvistech.business.datasource.services.ContactsApiServices
import com.example.kmmexlvistech.business.datasource.services.DashboardApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.MeetingsApiServices
import com.example.kmmexlvistech.business.datasource.services.ProfileApiServices
import com.example.kmmexlvistech.business.datasource.services.SplashApiServices
import com.example.kmmexlvistech.business.datasource.services.TaskApiServices
import com.example.kmmexlvistech.business.repos.AccountsRepository
import com.example.kmmexlvistech.business.repos.CallsRepository
import com.example.kmmexlvistech.business.repos.ContactsRepository
import com.example.kmmexlvistech.business.repos.DashboardRepository
import com.example.kmmexlvistech.business.repos.HomeRepository
import com.example.kmmexlvistech.business.repos.LeadsRepository
import com.example.kmmexlvistech.business.repos.LoginRepository
import com.example.kmmexlvistech.business.repos.MeetingsRepository
import com.example.kmmexlvistech.business.repos.ProfileRepository
import com.example.kmmexlvistech.business.repos.TaskRepository
import com.example.kmmexlvistech.common.DeviceInfoProvider
import com.example.kmmexlvistech.presentation.ui.accounts.viewmodel.AccountsViewModel
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsViewModel
import com.example.kmmexlvistech.presentation.ui.contacts.viewmodel.ContactsViewModel
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.DashboardViewModel
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskViewModel
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeViewModel
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsViewModel
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsViewModel
import com.example.kmmexlvistech.presentation.ui.profile.viewmodel.ProfileViewModel
import com.example.kmmexlvistech.presentation.ui.report.viewmodel.ActivityReportViewModel
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginViewModel
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun appModule(context: AppContext) = module {
    single { Json { isLenient = true; ignoreUnknownKeys = true } }
    single<AppDataStore> { AppDataStoreManager(context) }
    single<SplashApiServices> { SplashServiceImpl(get()) }
    single<TaskApiServices> { TaskServiceImpl(get()) }
    single<LeadsApiServices> { LeadsServiceImpl(get()) }
    single<HomeApiServices> { HomeServiceImpl(get()) }
    single<CallsApiServices> { CallsServiceImpl(get()) }
    single<CentralCrmApiServices> { CentralCrmServiceImpl(get()) }
    single<MeetingsApiServices> { MeetingsServiceImpl(get()) }
    single<DashboardApiServices> { DashboardServiceImpl(get()) }
    single<ProfileApiServices> { ProfileServiceImpl(get()) }
    single<AccountsApiServices> { AccountsServiceImpl(get()) }
    single<ContactsApiServices> { ContactsServiceImpl(get()) }
    single { KtorHttpClient.httpClient() }
    single { LoginRepository(get(), get()) }
    single { MeetingsRepository(get(), get()) }
    single { ProfileRepository(get()) }
    single { ContactsRepository(get(), get()) }

    single { LeadsRepository(get(), get()) }
    single { AccountsRepository(get(), get()) }
    single { TaskRepository(get(), get()) }
    single { HomeRepository(get()) }
    single { CallsRepository(get(), get()) }
    single { DashboardRepository(get()) }
    single { GeoInfoProvider(get()) }
    factory { HomeViewModel(get()) }
    single { LeadsViewModel(get()) }
    single { TaskViewModel(get()) }
    single { CallsViewModel(get()) }
    single { MeetingsViewModel(get()) }
    single { DashboardViewModel(get()) }
    single { ProfileViewModel(get()) }
    single { AccountsViewModel(get()) }
    single { ContactsViewModel(get()) }
    factory { LoginViewModel(get(), get(), get()) }

    single { ActivityReportService(get(), BASE_URL_SERVER_ONE) }
    single { ActivityReportViewModel(get()) }   // or factory/viewModel, matching your other VMs
}

