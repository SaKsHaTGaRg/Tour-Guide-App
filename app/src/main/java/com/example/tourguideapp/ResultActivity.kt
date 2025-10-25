package com.example.tourguideapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ResultActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var imgCaptured: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var btnBackToNarration: ImageButton
    private lateinit var btnNarration: ImageButton
    private lateinit var tts: TextToSpeech

    private var isSpeaking = false
    private var narrationText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        imgCaptured = findViewById(R.id.imgCaptured)
        tvDescription = findViewById(R.id.tvDescription)
        btnBackToNarration = findViewById(R.id.btnBackToNarration)
        btnNarration = findViewById(R.id.btnNarration)

        tts = TextToSpeech(this, this)

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

        // Load image
        val photoPath = intent.getStringExtra("photo_path")
        if (photoPath != null) {
            if (photoPath.startsWith("drawable:")) {
                val resId = photoPath.substringAfter("drawable:").toInt()
                imgCaptured.setImageResource(resId)
            } else {
                val bitmap = BitmapFactory.decodeFile(photoPath)
                imgCaptured.setImageBitmap(bitmap)
            }
        } else {
            imgCaptured.setImageResource(R.mipmap.ic_launcher)
        }

        // Scroll to top button
        btnBackToNarration.setOnClickListener {
            tvDescription.scrollTo(0, 0)
        }

        // AI Narration Play/Pause
        narrationText = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
            Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 
            Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris 
            nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in 
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
            Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia 
            deserunt mollit anim id est laborum.
        """.trimIndent()

        tvDescription.text = narrationText

        btnNarration.setOnClickListener {
            if (isSpeaking) {
                tts.stop()
                isSpeaking = false
                btnNarration.setImageResource(R.drawable.ic_play_arrow) // Play icon
            } else {
                speakOut(narrationText)
                isSpeaking = true
                btnNarration.setImageResource(R.drawable.ic_pause) // Pause icon
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle unsupported language
            }
        }
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    override fun onDestroy() {
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
