package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class LeadsRepository(
            private val service: LeadsApiServices,
        ) {
            fun getLeads(systemToken:String, leadsRequestDto: LeadsRequestDto
            ): Flow<DataState<LeadsListResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    val response = service.getLeadsList(systemToken, leadsRequestDto = leadsRequestDto)
                        emit(DataState.Data(response, response.status.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }

            fun getLeadsDetails(systemToken:String, id: String
            ): Flow<DataState<LeadDetailsResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    val response = service.getLeadsDetails(systemToken, id = id)
                        emit(DataState.Data(response, response.status.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }


        }