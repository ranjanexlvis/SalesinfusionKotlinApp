package com.example.kmmexlvistech.business.datasource.services

import com.example.kmmexlvistech.business.datasource.models.ProfileResponse
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileRequestDto
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileResponse

interface ProfileApiServices {
    suspend fun getProfile(token: String): ProfileResponse
    suspend fun updateProfile(token: String, request: UpdateProfileRequestDto): UpdateProfileResponse

    companion object {
        // match the leading-slash style of your other constants
        const val GET_PROFILE = "Profile"
        const val UPDATE_PROFILE = "Profile/Update"
    }
}