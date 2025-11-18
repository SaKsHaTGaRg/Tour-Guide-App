package com.example.tourguideapp.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {

    @Headers(
        "Authorization: Bearer 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",
        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    suspend fun analyzeImage(
        @Body requestBody: RequestBody
    ): ResponseBody
}
