package com.example.kmmexlvistech.di

import com.example.kmmexlvistech.AppContext
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.business.core.AppDataStoreManager
import com.example.kmmexlvistech.business.core.KtorHttpClient
import com.example.kmmexlvistech.business.datasource.impl.CallsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.HomeServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.LeadsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.MeetingsServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.SplashServiceImpl
import com.example.kmmexlvistech.business.datasource.impl.TaskServiceImpl
import com.example.kmmexlvistech.business.datasource.models.GeoInfoProvider
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.MeetingsApiServices
import com.example.kmmexlvistech.business.datasource.services.SplashApiServices
import com.example.kmmexlvistech.business.datasource.services.TaskApiServices
import com.example.kmmexlvistech.business.repos.CallsRepository
import com.example.kmmexlvistech.business.repos.HomeRepository
import com.example.kmmexlvistech.business.repos.LeadsRepository
import com.example.kmmexlvistech.business.repos.LoginRepository
import com.example.kmmexlvistech.business.repos.MeetingsRepository
import com.example.kmmexlvistech.business.repos.TaskRepository
import com.example.kmmexlvistech.common.DeviceInfoProvider
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.CallsViewModel
import com.example.kmmexlvistech.presentation.ui.tasks.viewmodel.TaskViewModel
import com.example.kmmexlvistech.presentation.ui.dashboard.viewModel.HomeViewModel
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsViewModel
import com.example.kmmexlvistech.presentation.ui.meetings.viewmodel.MeetingsViewModel
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
    single<MeetingsApiServices> { MeetingsServiceImpl(get()) }
    single { KtorHttpClient.httpClient() }
    single { LoginRepository(get(), get()) }
    single { MeetingsRepository(get()) }

    single { LeadsRepository(get()) }
    single { TaskRepository(get()) }
    single { HomeRepository(get()) }
    single { CallsRepository(get()) }
    single { GeoInfoProvider(get()) }
    factory { HomeViewModel(get()) }
    single { LeadsViewModel(get()) }
    factory { TaskViewModel(get()) }
    factory { CallsViewModel(get()) }
    factory { MeetingsViewModel(get()) }
    factory { LoginViewModel(get(), get(), get()) }
}

