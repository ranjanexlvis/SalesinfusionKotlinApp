package com.example.kmmexlvistech.business.core


interface AppDataStore {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

    suspend fun deleteValues()

}