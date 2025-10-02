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
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
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

        // TODO: implement text narration, highlighting words, play button, rewind

        btnBackToNarration.setOnClickListener {
            // Scroll back to top
            tvDescription.scrollTo(0,0)
        }
    }
}
