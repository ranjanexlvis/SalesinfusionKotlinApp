package com.example.kmmexlvistech.business.datasource.models

import com.example.kmmexlvistech.business.datasource.dto.GeoInfoDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Maps ipapi.co's raw response fields onto our own naming
@Serializable
private data class IpApiResponse(
    @SerialName("ip") val ip: String = "",
    @SerialName("city") val city: String = "",
    @SerialName("region") val region: String = "",
    @SerialName("country") val country: String = "",
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("timezone") val timezoneObj: TimezoneInfo = TimezoneInfo(),
)


@Serializable
private data class TimezoneInfo(
    @SerialName("id") val id: String = ""
)

class GeoInfoProvider(private val httpClient: HttpClient) {
    suspend fun fetch(): GeoInfoDTO {
        val response: IpApiResponse = try {
            httpClient.get("https://ipwho.is/").body()
        } catch (e: Exception) {
            IpApiResponse()
        }

        return GeoInfoDTO(
            publicIp = response.ip,
            timezoneId = response.timezoneObj.id,
            latitude = response.latitude,
            longitude = response.longitude,
            city = response.city,
            state = response.region,
            country = response.country,
        )
    }
}