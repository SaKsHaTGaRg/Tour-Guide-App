package com.example.tourguideapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object Prefs {
    private const val FILE = "settings_prefs"
    private const val KEY_DARKMODE = "darkmode_enabled"
    private const val KEY_COLORBLIND = "colorblind_enabled"
    private const val KEY_DYSLEXIA = "dyslexia_enabled"
    private const val KEY_FONT_SCALE = "font_scale"
    //Creating xml with shared preferences
    private fun sharedPrefs(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    //Setting properties with getter, setter
    var Context.fontScalePref: Float
        get() = sharedPrefs(this).getFloat(KEY_FONT_SCALE, 1.0f)
        set(value) = sharedPrefs(this).edit().putFloat(KEY_FONT_SCALE, value).apply()
    var Context.isDarkMode: Boolean
        get() = sharedPrefs(this).getBoolean(KEY_DARKMODE, false)
        set(value) {
            sharedPrefs(this).edit().putBoolean(KEY_DARKMODE, value).apply()
        }

    var Context.isColorblindTheme: Boolean
        get() = sharedPrefs(this).getBoolean(KEY_COLORBLIND, false)
        set(value) {
            sharedPrefs(this).edit().putBoolean(KEY_COLORBLIND, value).apply()
        }

    var Context.isDyslexiaFont: Boolean
        get() = sharedPrefs(this).getBoolean(KEY_DYSLEXIA, false)
        set(value) {
            sharedPrefs(this).edit().putBoolean(KEY_DYSLEXIA, value).apply()
        }
    //dark mode helper
    fun applyDarkMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}