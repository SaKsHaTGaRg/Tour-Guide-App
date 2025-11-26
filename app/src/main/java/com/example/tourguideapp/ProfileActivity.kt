package com.example.tourguideapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tourguideapp.ReloadActivity

class ProfileActivity : BaseActivity() {

    private lateinit var etUserName: EditText
    private lateinit var imgProfile: ImageView
    private lateinit var btnChangePhoto: Button
    private lateinit var btnSave: Button

    private lateinit var spinnerVoice: Spinner
    private lateinit var etTone: EditText
    private lateinit var spinnerDetail: Spinner
    private lateinit var spinnerFocus: Spinner

    private lateinit var btnProfile: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnReload: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var sharedPrefs: SharedPreferences
    private var isAltPhoto = false // Used to simulate photo switching

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // SharedPreferences for saving profile data
        sharedPrefs = getSharedPreferences("UserProfile", MODE_PRIVATE)

        // Initialize UI elements
        etUserName = findViewById(R.id.etUserName)
        imgProfile = findViewById(R.id.imgProfile)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        btnSave = findViewById(R.id.btnSave)

        spinnerVoice = findViewById(R.id.spinnerVoice)
        etTone = findViewById(R.id.etTone)
        spinnerDetail = findViewById(R.id.spinnerDetail)
        spinnerFocus = findViewById(R.id.spinnerFocus)

        btnProfile = findViewById(R.id.btnProfile)
        btnHome = findViewById(R.id.btnHome)
        btnReload = findViewById(R.id.btnReload)
        btnSettings = findViewById(R.id.btnSettings)

        setupSpinners()
        loadUserData()
        setupListeners()
    }

    private fun setupSpinners() {
        val voices = listOf("Male", "Female", "Neutral", "Custom")
        val details = listOf("Low", "Medium", "High")
        val focus = listOf("Summary", "Highlights", "Full Text")

        spinnerVoice.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voices)
        spinnerDetail.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, details)
        spinnerFocus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, focus)
    }

    private fun loadUserData() {
        etUserName.setText(sharedPrefs.getString("name", "John Doe"))
        etTone.setText(sharedPrefs.getString("tone", ""))

        spinnerVoice.setSelection(sharedPrefs.getInt("voiceIndex", 0))
        spinnerDetail.setSelection(sharedPrefs.getInt("detailIndex", 0))
        spinnerFocus.setSelection(sharedPrefs.getInt("focusIndex", 0))

        isAltPhoto = sharedPrefs.getBoolean("isAltPhoto", false)
        imgProfile.setImageResource(
            if (isAltPhoto) R.drawable.ic_photo_alt else R.drawable.ic_photo
        )
    }

    private fun setupListeners() {
        btnChangePhoto.setOnClickListener {
            // Swap between default and alternate dummy image
            isAltPhoto = !isAltPhoto
            imgProfile.setImageResource(
                if (isAltPhoto) R.drawable.ic_photo_alt else R.drawable.ic_photo
            )
        }

        btnSave.setOnClickListener {
            // Save data to SharedPreferences
            sharedPrefs.edit().apply {
                putString("name", etUserName.text.toString())
                putString("tone", etTone.text.toString())
                putInt("voiceIndex", spinnerVoice.selectedItemPosition)
                putInt("detailIndex", spinnerDetail.selectedItemPosition)
                putInt("focusIndex", spinnerFocus.selectedItemPosition)
                putBoolean("isAltPhoto", isAltPhoto)
                apply()
            }
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
        }

        // Bottom Navigation
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            //Do nothing
        }
        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }
}