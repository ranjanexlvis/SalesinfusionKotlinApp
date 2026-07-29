package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.dto.CallsListResponse
import com.example.kmmexlvistech.business.datasource.dto.CallsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmFilterDto
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CentralCrmSortDto
import com.example.kmmexlvistech.business.datasource.dto.CreateMeetingRequestDto
import com.example.kmmexlvistech.business.datasource.dto.CreateMeetingResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsFilterDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsListResponse
import com.example.kmmexlvistech.business.datasource.dto.LeadsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.LeadsSortDto
import com.example.kmmexlvistech.business.datasource.dto.MeetingsListResponse
import com.example.kmmexlvistech.business.datasource.dto.MeetingsRequestDto
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.ActivityPrioritiesResponse
import com.example.kmmexlvistech.business.datasource.models.ActivityStatusesResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.MeetingDetailResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.models.UserListRequestDto
import com.example.kmmexlvistech.business.datasource.models.UserListResponse
import com.example.kmmexlvistech.business.datasource.services.CallsApiServices
import com.example.kmmexlvistech.business.datasource.services.CentralCrmApiServices
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.LeadsApiServices
import com.example.kmmexlvistech.business.datasource.services.MeetingsApiServices

import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class MeetingsRepository(
            private val service: MeetingsApiServices,
            private val centralCrmApiServices: CentralCrmApiServices,
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

             fun getMeetingDetails(systemToken: String, meetingId: String): Flow<DataState<MeetingDetailResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    emit(DataState.Data(service.getMeetingDetails(systemToken, meetingId)))
                } catch (e: Exception) {
                    emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong")))
                }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun createMeeting(systemToken: String, requestDto: CreateMeetingRequestDto): Flow<DataState<CreateMeetingResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try { emit(DataState.Data(service.createMeeting(systemToken, requestDto))) }
                catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun searchLeadsForMeeting(systemToken: String, search: String): Flow<DataState<LeadsListResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    val requestDto = LeadsRequestDto(
                        page = 1, pageSize = 20,
                        filters = LeadsFilterDto(search = search.ifBlank { null }, 1021),
                        sort = LeadsSortDto(),
                    )
                    emit(DataState.Data(service.searchLeads(systemToken, requestDto)))
                } catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun getMeetingPriorities(systemToken: String): Flow<DataState<ActivityPrioritiesResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    val requestDto = CentralCrmRequestDto(sort = CentralCrmSortDto(field = "sortOrder"))
                    emit(DataState.Data(centralCrmApiServices.getActivityPriorities(systemToken, requestDto)))
                } catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun getMeetingStatuses(systemToken: String): Flow<DataState<ActivityStatusesResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try {
                    val requestDto = CentralCrmRequestDto(
                        filters = CentralCrmFilterDto(activityTypeId = 3), // TODO: confirm actual activityTypeId for Meeting
                        sort = CentralCrmSortDto(field = "sortOrder")
                    )
                    emit(DataState.Data(centralCrmApiServices.getActivityStatuses(systemToken, requestDto)))
                } catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
                emit(DataState.Loading(ProgressBarState.Idle))
            }

             fun getUserList(systemToken: String): Flow<DataState<UserListResponse>> = flow {
                emit(DataState.Loading(ProgressBarState.Loading))
                try { emit(DataState.Data(centralCrmApiServices.getUserList(systemToken, UserListRequestDto()))) }
                catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
                emit(DataState.Loading(ProgressBarState.Idle))
            }



        }