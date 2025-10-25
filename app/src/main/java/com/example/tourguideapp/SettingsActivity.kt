package com.example.tourguideapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Switch
import com.example.tourguideapp.Prefs.fontScalePref
import com.example.tourguideapp.Prefs.isColorblindTheme
import com.example.tourguideapp.Prefs.isDarkMode
import com.example.tourguideapp.Prefs.isDyslexiaFont


class SettingsActivity : BaseActivity() {

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
            startActivity(Intent(this, HistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        btnSettings.setOnClickListener {
            //Do nothing
        }
        val appContext = applicationContext
        // setting up spinner adapters
        val spFontSize = findViewById<Spinner>(R.id.font_spn)
        val fontAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.font_sizes,
            R.layout.spinner_item
        )
        fontAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spFontSize.adapter = fontAdapter
        val scaleOptions = listOf(0.85f, 0.9f, 1.0f, 1.15f, 1.3f)
        val currentIndex = scaleOptions.indexOfFirst { it == appContext.fontScalePref }
        spFontSize.setSelection(if (currentIndex >= 0) currentIndex else 2) //default = Normal
        // Save + restart when user changes
        spFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val newScale = scaleOptions[position]
                if (appContext.fontScalePref != newScale) {
                    appContext.fontScalePref = newScale
                    val intent = Intent(this@SettingsActivity, SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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

        //Switches
        val swtDyslexia = findViewById<Switch>(R.id.dyslexia_swt)
        val swtColorblind = findViewById<Switch>(R.id.colorblind_swt)
        val swtDark = findViewById<Switch>(R.id.darkmode_swt)

        swtDyslexia.isChecked = appContext.isDyslexiaFont
        swtColorblind.isChecked = appContext.isColorblindTheme
        swtDark.isChecked = appContext.isDarkMode

        swtDark.setOnCheckedChangeListener { _, checked ->
            appContext.isDarkMode = checked
            Prefs.applyDarkMode(checked)
            recreate()
        }

        swtColorblind.setOnCheckedChangeListener { _, checked ->
            appContext.isColorblindTheme = checked
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        swtDyslexia.setOnCheckedChangeListener { _, checked ->
            appContext.isDyslexiaFont = checked
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}