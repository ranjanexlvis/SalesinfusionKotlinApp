package com.example.kmmexlvistech.common

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.kmmexlvistech.business.datasource.dto.DeviceInfoDTO

actual class DeviceInfoProvider(private val context: Context) {
    actual fun collect(): DeviceInfoDTO {
        val appVersion = try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }

        // Real MAC address hasn't been readable since Android 6 (API 23) for privacy
        // reasons — every app gets back the same dummy value from the OS. Using the
        // Android ID as a stable per-install identifier instead of a fake MAC.
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown"

        return DeviceInfoDTO(
            deviceId = androidId,
            deviceName = Build.MODEL,
            devicemodel = "${Build.MANUFACTURER} ${Build.MODEL}",
            osvesion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            appversion = appVersion,
            macaddress = "02:00:00:00:00:00", // OS-level placeholder — see note below
        )
    }
}