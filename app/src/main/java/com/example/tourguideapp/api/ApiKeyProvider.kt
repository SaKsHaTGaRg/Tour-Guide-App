package com.example.tourguideapp.api

import com.example.tourguideapp.BuildConfig


object ApiKeyProvider {
    val openAIKey: String
        get() = BuildConfig.OPENAI_API_KEY
}
