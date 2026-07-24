package com.example.kmmexlvistech.business.datasource.impl



import com.example.kmmexlvistech.business.constants.BASE_URL_SERVER_ONE
import com.example.kmmexlvistech.business.datasource.dto.QuickActionDto
import com.example.kmmexlvistech.business.datasource.models.GetWidgetActivityResponse
import com.example.kmmexlvistech.business.datasource.models.QuickActionDTO
import com.example.kmmexlvistech.business.datasource.models.QuickActionResponse
import com.example.kmmexlvistech.business.datasource.services.HomeApiServices
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class HomeServiceImpl(
    private val httpClient: HttpClient
): HomeApiServices {


    override suspend fun getWidgetWithActivities(token: String): GetWidgetActivityResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += HomeApiServices.GET_WIDGET_ACTIVITY
            }
            header("Authorization","Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())


        }.body()
    }

    override suspend fun getQuickActionActivities(token: String, quickActionDTO: QuickActionDto): QuickActionResponse {
        return httpClient.post {
            url {
                takeFrom(BASE_URL_SERVER_ONE)
                encodedPath += HomeApiServices.QUICK_ACTION_ACTIVITY
            }
            header("Authorization","Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(quickActionDTO)


        }.body()
    }


}