package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsListResponse
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.MeetingsApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class MeetingsRepository(
            private val service: MeetingsApiServices,
        ) {
            fun getMeetings(systemToken:String, meetingsRequestDto: MeetingsRequestDto
            ): Flow<DataState<MeetingsListResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    val response = service.getMeetingsList(systemToken, meetingsRequestDto = meetingsRequestDto)
                        emit(DataState.Data(response, response.status.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }


        }