package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmSortDto
import com.example.kmmexlvistech.business.datasource.dto.CreateLeadRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateLeadResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadSourcesResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadStatusesResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.DeleteLeadResponse
import com.example.kmmexlvistech.business.datasource.dto.UpdateLeadRequestDto
import com.example.kmmexlvistech.business.datasource.dto.UpdateLeadResponse
import com.example.kmmexlvistech.business.datasource.models.LeadDetailsResponse
import com.example.kmmexlvistech.business.datasource.services.CentralCrmApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class LeadsRepository(
            private val service: LeadsApiServices,
            private val centralCrmApiServices: CentralCrmApiServices,
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

             fun getLeadStatuses(systemToken: String): Flow<DataState<LeadStatusesResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    val requestDto = CentralCrmRequestDto(sort = CentralCrmSortDto(field = "statusCode"))
                    emit(DataState.Data(centralCrmApiServices.getLeadStatuses(systemToken, requestDto)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun getLeadSources(systemToken: String): Flow<DataState<LeadSourcesResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    val requestDto = CentralCrmRequestDto(sort = CentralCrmSortDto(field = "sourceName"))
                    emit(DataState.Data(centralCrmApiServices.getLeadSources(systemToken, requestDto)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun createLead(systemToken: String, createLeadRequestDto: CreateLeadRequestDto): Flow<DataState<CreateLeadResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    emit(DataState.Data(service.createLead(systemToken, createLeadRequestDto)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }


            fun deleteLead(systemToken: String, leadId: String): Flow<DataState<DeleteLeadResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    emit(DataState.Data(service.deleteLead(systemToken, leadId)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun updateLead(systemToken: String, requestDto: UpdateLeadRequestDto): Flow<DataState<UpdateLeadResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    emit(DataState.Data(service.updateLead(systemToken, requestDto)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }



        }