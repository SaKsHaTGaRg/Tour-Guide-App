package com.example.tourguideapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.*

class ResultActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var imgCaptured: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var btnBackToNarration: ImageButton
    private lateinit var btnNarration: ImageButton
    private lateinit var tts: TextToSpeech

    private var isSpeaking = false
    private var narrationText: String = ""

    private val backend = Backend()

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
            startActivity(
                Intent(this, ProfileActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }

        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            startActivity(
                Intent(this, HistoryActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(
                Intent(this, SettingsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }

        // Load image
        val photoPath = intent.getStringExtra("photo_path")
        val landmarkName = intent.getStringExtra("landmark_name")

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

        if (landmarkName == null) {
            Toast.makeText(this, "No landmark received from backend", Toast.LENGTH_LONG).show()
            return
        }

        // Loading text while story is generated
        tvDescription.text = "Generating story for $landmarkName..."
        narrationText = tvDescription.text.toString()

        // --- Hardcoded preferences (can later be user-chosen) ---
        val userStyle = "folklore"   // funny / scary / romantic / folklore etc.
        val userTone = "casual"      // casual / formal
        val userLength = "medium"    // short / medium / long

        // --- Fetch story from backend ---
        backend.fetchStoryFromBackend(
            landmark = landmarkName,
            style = userStyle,
            tone = userTone,
            length = userLength
        ) { story ->
            runOnUiThread {
                if (story != null) {
                    narrationText = story
                    tvDescription.text = story
                } else {
                    narrationText = "Could not generate story for $landmarkName."
                    tvDescription.text = narrationText
                }
            }
        }

        // Scroll to top button
        btnBackToNarration.setOnClickListener {
            tvDescription.scrollTo(0, 0)
        }

        // AI Narration Play/Pause
        btnNarration.setOnClickListener {
            if (isSpeaking) {
                tts.stop()
                isSpeaking = false
                btnNarration.setImageResource(R.drawable.ic_play_arrow)
            } else {
                speakOut(narrationText)
                isSpeaking = true
                btnNarration.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                // Language error handling
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
