package com.example.tourguideapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var imgCaptured: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var btnBackToNarration: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val btnProfile: ImageButton = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        val btnHome: ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java )
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        val btnReload: ImageButton = findViewById(R.id.btnReload)
        btnReload.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }


        imgCaptured = findViewById(R.id.imgCaptured)
        tvDescription = findViewById(R.id.tvDescription)
        btnBackToNarration = findViewById(R.id.btnBackToNarration)

        // Get captured image file path from intent
        val photoPath = intent.getStringExtra("photo_path")
        if (photoPath != null) {
            val bitmap = BitmapFactory.decodeFile(photoPath)
            imgCaptured.setImageBitmap(bitmap)
        }
        else {
            //Use dummy
            imgCaptured.setImageResource(R.mipmap.ic_launcher)
        }

        // TODO: implement text narration, highlighting words, play button, rewind

        btnBackToNarration.setOnClickListener {
            // Scroll back to top
            tvDescription.scrollTo(0,0)
        }
    }
}
