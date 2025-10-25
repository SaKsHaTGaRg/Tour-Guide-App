package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View

class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        // Set up clickable history items
        val itemCasaLomaHistory = findViewById<View>(R.id.itemCasaLomaHistory)
        itemCasaLomaHistory.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        val itemCasaLomaFolklore = findViewById<View>(R.id.itemCasaLomaFolklore)
        itemCasaLomaFolklore.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        val itemGeorgeBrown = findViewById<View>(R.id.itemGeorgeBrown)
        itemGeorgeBrown.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        val itemBigBen = findViewById<View>(R.id.itemBigBen)
        itemBigBen.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        // Bottom navigation
        val btnProfile: ImageButton = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        val btnHome: ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        val btnReload: ImageButton = findViewById(R.id.btnReload)
        btnReload.setOnClickListener {
            // Already on History tab - do nothing or refresh
        }

        val btnSettings: ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }
}