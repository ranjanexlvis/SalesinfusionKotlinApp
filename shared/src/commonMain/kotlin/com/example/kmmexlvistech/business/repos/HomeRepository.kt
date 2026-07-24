package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.LoginHeaderRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.VerifyOtpDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import com.example.kmmexlvistech.business.datasource.services.SplashApiServices
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.getPlatform
import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class HomeRepository(
            private val service: HomeApiServices,
        ) {

            fun getWidgetWithActivities(systemToken:String
            ): Flow<DataState<GetWidgetActivityResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))

                    val response = service.getWidgetWithActivities(systemToken)
                        emit(DataState.Data(response, response.status.toString()))



                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }


            fun getQuickActionMenu(systemToken:String, quickActionDTO: QuickActionDto
            ): Flow<DataState<QuickActionResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))

                    val response = service.getQuickActionActivities(systemToken,quickActionDTO)
                        emit(DataState.Data(response, response.status.toString()))



                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }




        }