package com.example.kmmexlvistech

import android.os.Build

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidPlatform : Platform {
    override val name: String =
        "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
}