package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.DashboardResponse

import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.MenuDashboardResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.services.DashboardApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class DashboardRepository(
            private val service: DashboardApiServices,
        ) {

            @Suppress("SuspiciousIndentation")
            fun getDashboardMenu(systemToken:String
            ): Flow<DataState<MenuDashboardResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))

                    val response = service.getDashboardMenu(systemToken)
                        emit(DataState.Data(response, response.status.toString()))



                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }




        }