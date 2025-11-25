package com.example.tourguideapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import com.example.tourguideapp.Prefs.fontScalePref

//Automatically apply themes from settings to every view
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
    }
    override fun attachBaseContext(newBase: Context) {
        val scale = newBase.fontScalePref
        val config = newBase.resources.configuration
        config.fontScale = scale
        val scaledContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(scaledContext)
    }
}