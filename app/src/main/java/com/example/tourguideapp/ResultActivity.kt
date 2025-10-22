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

        imgCaptured = findViewById(R.id.imgCaptured)
        tvDescription = findViewById(R.id.tvDescription)
        btnBackToNarration = findViewById(R.id.btnBackToNarration)

        // Bottom navigation
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        // ✅ Get captured or uploaded image path
        val photoPath = intent.getStringExtra("photo_path")

        if (photoPath != null) {
            if (photoPath.startsWith("drawable:")) {
                // handle dummy upload image
                val resId = photoPath.substringAfter("drawable:").toInt()
                imgCaptured.setImageResource(resId)
            } else {
                // handle real photo from camera
                val bitmap = BitmapFactory.decodeFile(photoPath)
                imgCaptured.setImageBitmap(bitmap)
            }
        } else {
            // fallback dummy icon
            imgCaptured.setImageResource(R.mipmap.ic_launcher)
        }

        btnBackToNarration.setOnClickListener {
            // Scroll back to top of description
            tvDescription.scrollTo(0, 0)
        }
    }
}
