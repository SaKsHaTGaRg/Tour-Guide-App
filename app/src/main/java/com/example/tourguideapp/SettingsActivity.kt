package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnProfile: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnReload: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Bottom navigation
        btnProfile = findViewById(R.id.btnProfile)
        btnHome = findViewById(R.id.btnHome)
        btnReload = findViewById(R.id.btnReload)
        btnSettings = findViewById(R.id.btnSettings)

        btnSettings.isSelected = true

        btnHome.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }
        btnProfile.setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }
        btnReload.setOnClickListener {
            //History activity implementation
        }
        btnSettings.setOnClickListener {
            //Do nothing
        }

        // setting up spinner adapters
        val spFontSize = findViewById<Spinner>(R.id.font_spn)
        val fontAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.font_sizes,
            R.layout.spinner_item
        )
        fontAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spFontSize.adapter = fontAdapter
        spFontSize.setSelection(2) //setting a default value

        val mesUnits = findViewById<Spinner>(R.id.units_spn)
        val mesAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.measurement_opt,
            R.layout.spinner_item
        )
        mesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        mesUnits.adapter = mesAdapter

        val downOpt = findViewById<Spinner>(R.id.download_spn)
        val downAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.download_opt,
            R.layout.spinner_item
        )
        downAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        downOpt.adapter = downAdapter
    }
}