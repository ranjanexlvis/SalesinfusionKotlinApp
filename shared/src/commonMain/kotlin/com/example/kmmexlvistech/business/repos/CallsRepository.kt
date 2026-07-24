package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class CallsRepository(
            private val service: CallsApiServices,
        ) {
            fun getCalls(systemToken:String, callsRequestDto: CallsRequestDto
            ): Flow<DataState<CallsListResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    val response = service.getCallsList(systemToken, callsRequestDto = callsRequestDto)
                        emit(DataState.Data(response, response.status.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }


        }