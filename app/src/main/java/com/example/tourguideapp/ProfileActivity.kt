package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var spinnerVoice: Spinner
    private lateinit var etTone: EditText
    private lateinit var spinnerDetail: Spinner
    private lateinit var spinnerFocus: Spinner

    private lateinit var btnProfile: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnReload: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        // Spinners and EditText
        spinnerVoice = findViewById(R.id.spinnerVoice)
        etTone = findViewById(R.id.etTone)
        spinnerDetail = findViewById(R.id.spinnerDetail)
        spinnerFocus = findViewById(R.id.spinnerFocus)
        setupSpinners()

        // Bottom navigation
        btnProfile = findViewById(R.id.btnProfile)
        btnHome = findViewById(R.id.btnHome)
        btnReload = findViewById(R.id.btnReload)
        btnSettings = findViewById(R.id.btnSettings)

        btnProfile.setOnClickListener {
            // Already in profile, do nothing
        }

        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        btnReload.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }

    private fun setupSpinners() {
        // Voice Preference
        val voices = listOf("Male", "Female", "Neutral", "Custom")
        val voiceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voices)
        voiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVoice.adapter = voiceAdapter

        // Level of Detail
        val detailLevels = listOf("Low", "Medium", "High")
        val detailAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, detailLevels)
        detailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDetail.adapter = detailAdapter

        // Focus Preference
        val focusOptions = listOf("Summary", "Highlights", "Full Text")
        val focusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, focusOptions)
        focusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFocus.adapter = focusAdapter
    }
}
