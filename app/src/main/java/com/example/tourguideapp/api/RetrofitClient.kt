package com.example.tourguideapp.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.openai.com/"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer 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")
            .build()

        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val openAIService: OpenAIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }
}
