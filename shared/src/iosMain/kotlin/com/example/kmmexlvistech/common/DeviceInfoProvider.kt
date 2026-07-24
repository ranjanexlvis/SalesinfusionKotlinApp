package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.business.datasource.dto.DeviceInfoDTO
import platform.UIKit.UIDevice
import platform.Foundation.NSBundle

actual class DeviceInfoProvider {
    actual fun collect(): DeviceInfoDTO {
        val device = UIDevice.currentDevice
        val appVersion = NSBundle.mainBundle
            .infoDictionary?.get("CFBundleShortVersionString") as? String ?: "unknown"

        return DeviceInfoDTO(
            deviceId = device.identifierForVendor?.UUIDString ?: "unknown",
            deviceName = device.name,
            devicemodel = device.model,
            osvesion = "${device.systemName} ${device.systemVersion}",
            appversion = appVersion,
            macaddress = "02:00:00:00:00:00", // iOS has blocked real MAC access since iOS 7
        )
    }
}