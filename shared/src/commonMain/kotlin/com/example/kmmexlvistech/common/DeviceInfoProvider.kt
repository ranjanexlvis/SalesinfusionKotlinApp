package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.business.datasource.dto.DeviceInfoDTO

expect class DeviceInfoProvider {
    fun collect(): DeviceInfoDTO
}