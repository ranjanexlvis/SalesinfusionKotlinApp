package com.example.kmmexlvistech.business.repos

import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.models.ProfileResponse
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileRequestDto
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileResponse
import com.example.kmmexlvistech.business.datasource.services.ProfileApiServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// business/repos/ProfileRepository.kt
class ProfileRepository(private val service: ProfileApiServices) {

    fun getProfile(systemToken: String): Flow<DataState<ProfileResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try { emit(DataState.Data(service.getProfile(systemToken))) }
        catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
        emit(DataState.Loading(ProgressBarState.Idle))
    }

    fun updateProfile(systemToken: String, request: UpdateProfileRequestDto): Flow<DataState<UpdateProfileResponse>> = flow {
        emit(DataState.Loading(ProgressBarState.Loading))
        try { emit(DataState.Data(service.updateProfile(systemToken, request))) }
        catch (e: Exception) { emit(DataState.Response(UIComponent.ToastSimple(e.message ?: "Something went wrong"))) }
        emit(DataState.Loading(ProgressBarState.Idle))
    }
}