package com.example.tourguideapp.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {

    suspend fun analyzeImage(
        @Header("Authorization") authHeader: String,
        @Body requestBody: RequestBody
    ): ResponseBody

}
