package com.example.kmmexlvistech.common

actual object AppUpdateHelper {
    actual fun getCurrentAppVersion(): String {
        return "1"
    }

    actual fun openPlayStoreForUpdate() {
        // No-op for iOS
    }
}
