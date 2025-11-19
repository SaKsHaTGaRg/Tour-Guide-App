package com.example.tourguideapp.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {

    @Headers(
        "Content-Type: application/json"
    )
    @POST("v1/responses")
    suspend fun analyzeImage(
        @Body requestBody: RequestBody
    ): ResponseBody
}
