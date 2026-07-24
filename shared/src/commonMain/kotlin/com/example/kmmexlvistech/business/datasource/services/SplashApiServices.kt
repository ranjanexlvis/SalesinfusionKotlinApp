package com.example.kmmexlvistech.business.datasource.services


import com.example.kmmexlvistech.business.datasource.dto.LoginRequestDTO
import com.example.kmmexlvistech.business.datasource.dto.TokenRequestDTO
import com.example.kmmexlvistech.business.datasource.models.CommonGenericResponse
import com.example.kmmexlvistech.business.datasource.models.LoginResponse


interface SplashApiServices {

    companion object{
        const val SYSTEM_TOKEN = "Secure/System"
        const val LOGIN = "Auth/UserLogin"
    }

    suspend fun systemToken(loginRequestDTO: TokenRequestDTO): CommonGenericResponse
    suspend fun login(loginRequestDTO: LoginRequestDTO, systemToken:String): LoginResponse


}