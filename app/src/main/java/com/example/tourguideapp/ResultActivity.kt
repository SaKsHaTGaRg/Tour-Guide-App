package com.example.tourguideapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    private val handler = Handler(Looper.getMainLooper())

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

        // Show loading text
        tvDescription.text = "Generating story for $landmarkName..."
        narrationText = tvDescription.text.toString()

        // Hardcoded user preferences
        val userStyle = "folklore"
        val userTone = "casual"
        val userLength = "medium"

        // Fetch story from backend
        backend.fetchStoryFromBackend(
            landmarkName,
            userStyle,
            userTone,
            userLength
        ) { story ->
            runOnUiThread {
                if (story != null) {
                    narrationText = story
                    tvDescription.text = story
                    saveStoryToDatabase(
                        landmarkName = landmarkName,
                        storyText = story,
                        imagePath = photoPath
                    )
                } else {
                    narrationText = "Could not generate story for $landmarkName."
                    tvDescription.text = narrationText
                }
            }
        }

        btnBackToNarration.setOnClickListener {
            tvDescription.scrollTo(0, 0)
        }

        btnNarration.setOnClickListener {
            if (isSpeaking) {
                tts.stop()
                isSpeaking = false
                btnNarration.setImageResource(R.drawable.ic_play_arrow)
            } else {
                speakWithHighlight(narrationText)
                isSpeaking = true
                btnNarration.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    // ---------------- HIGHLIGHT LOGIC ----------------

    private fun speakWithHighlight(text: String) {
        val words = text.split(" ")
        var currentIndex = 0

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                handler.post {
                    // Clear highlight when done
                    tvDescription.text = narrationText
                    isSpeaking = false
                    btnNarration.setImageResource(R.drawable.ic_play_arrow)
                }
            }

            override fun onError(utteranceId: String?) {}

            override fun onRangeStart(
                utteranceId: String?,
                start: Int,
                end: Int,
                frame: Int
            ) {
                handler.post {
                    highlightLine(start, end)
                }
            }
        })

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts1")

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "tts1")
    }

    private fun highlightLine(start: Int, end: Int) {
        try {
            val spannable = SpannableStringBuilder(narrationText)

            val highlightColor = ContextCompat.getColor(this, R.color.line_highlight)

            tvDescription.post {
                try {
                    val layout = tvDescription.layout ?: return@post

                    // Find the line that this text range belongs to
                    val line = layout.getLineForOffset(start)

                    val lineStart = layout.getLineStart(line)
                    val lineEnd = layout.getLineEnd(line)

                    // Apply highlight to the entire line
                    spannable.setSpan(
                        BackgroundColorSpan(highlightColor),
                        lineStart,
                        lineEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    tvDescription.text = spannable

                    // Auto-scroll so the highlighted line stays visible
                    val y = layout.getLineTop(line)
                    val scrollView = tvDescription.parent.parent as? android.widget.ScrollView
                    scrollView?.smoothScrollTo(0, y - 100)  // Extra padding above

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    // ---------------- TTS INITIALIZATION ----------------

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
        }
    }

    override fun onDestroy() {
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
    // Db Creation
    private fun saveStoryToDatabase(landmarkName: String, storyText: String, imagePath: String?) {
        val dbHelper = StoryDatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val values = android.content.ContentValues().apply {
            put("landmarkName", landmarkName)
            put("storyText", storyText)
            put("imagePath", imagePath)
            put("createdAt", System.currentTimeMillis())
        }

        db.insert("stories", null, values)
    }
}
