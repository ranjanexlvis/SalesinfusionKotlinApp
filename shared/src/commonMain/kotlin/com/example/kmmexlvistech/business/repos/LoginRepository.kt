package com.example.kmmexlvistech.business.repos



import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.LoginHeaderRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.VerifyOtpDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse
import com.example.kmmexlvistech.business.datasource.services.SplashApiServices
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.getPlatform
import com.example.kmmexlvistech.utils.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

        class LoginRepository(
            private val service: SplashApiServices,
            private val appDataStoreManager: AppDataStore
        ) {

            fun getLogin(
                loginRequestDTO: LoginRequestDTO, systemToken:String
            ): Flow<DataState<LoginResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))
                    println("@@@@1")
                    val response = service.login(loginRequestDTO,systemToken)

                    println("@@@@3")


                    val isWrite = writeDataStore(response)
                    if (isWrite)
                        emit(DataState.Data(response, response.status.toString()))

                    else{
                        emit(DataState.Data(response, response.status.toString()))

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }
            fun getToken(
                tokenReqDto: TokenRequestDTO
            ): Flow<DataState<CommonGenericResponse>> = flow {
                try {
                    emit(DataState.Loading(progressBarState = ProgressBarState.FullScreenLoading))

                    val response = service.systemToken(tokenReqDto)
                    emit(DataState.Data(response, response.status))

                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(handleUseCaseException(e))

                }
            }

            private suspend fun writeDataStore(response: LoginResponse) : Boolean {
                println("@@@@123"+response.data?.token)
                if (response.status.toString()=="true") {
                    println("@@@@token"+response.data?.token)

                    appDataStoreManager.setValue(
                        DataStoreKeys.TOKEN, response.data?.token.toString()
                    )

                    appDataStoreManager.setValue(
                       DataStoreKeys.DeviceType, getPlatform().name
                    )
                    response.data?.user?.userName?.let {
                        appDataStoreManager.setValue(
                           DataStoreKeys.USERID, it
                        )
                    }

                    response.data?.user?.userName?.let {
                        appDataStoreManager.setValue(
                          DataStoreKeys.NAME, it
                        )
                    }


                    appDataStoreManager.setValue(
                       DataStoreKeys.Logout, "0"
                    )

                    return true
                }else{

                    return false
                }
            }


        }