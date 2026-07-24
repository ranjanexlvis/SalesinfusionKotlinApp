package com.example.kmmexlvistech.common

expect object AppUpdateHelper {
    fun getCurrentAppVersion(): String
    fun openPlayStoreForUpdate()
}
