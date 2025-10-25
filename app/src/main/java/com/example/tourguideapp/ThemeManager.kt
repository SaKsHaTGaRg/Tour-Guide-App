package com.example.tourguideapp

import android.app.Activity
import com.example.tourguideapp.Prefs.isColorblindTheme
import com.example.tourguideapp.Prefs.isDarkMode
import com.example.tourguideapp.Prefs.isDyslexiaFont

object ThemeManager {
    fun apply(activity: Activity) {
        if (activity.applicationContext.isColorblindTheme) {
            activity.setTheme(R.style.Theme_TourGuideApp_Colorblind)
        } else {
            activity.setTheme(R.style.Theme_TourGuideApp)
        }

        if (activity.applicationContext.isDyslexiaFont) {
            activity.theme.applyStyle(R.style.ThemeOverlay_TourGuideApp_Dyslexia, true)
        }

        Prefs.applyDarkMode(activity.applicationContext.isDarkMode)
    }
}