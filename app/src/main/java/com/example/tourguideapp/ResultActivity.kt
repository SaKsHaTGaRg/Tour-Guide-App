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
    private lateinit var btnNarration: ImageButton
    private lateinit var btnBackToNarration: ImageButton

    private lateinit var tts: TextToSpeech
    private var isSpeaking = false
    private var ttsReady = false
    private var narrationText = ""
    private val backend = Backend()
    private val handler = Handler(Looper.getMainLooper())

    private var hasRequestedStory = false
    private var hasSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        imgCaptured = findViewById(R.id.imgCaptured)
        tvDescription = findViewById(R.id.tvDescription)
        btnNarration = findViewById(R.id.btnNarration)
        btnBackToNarration = findViewById(R.id.btnBackToNarration)
        btnNarration.isEnabled = false

        tts = TextToSpeech(this, this)

        setupBottomNav()

        val openedFromHistory = intent.getBooleanExtra("fromHistory", false)

        if (openedFromHistory) {
            loadFromHistory()
            return
        }

        loadFromCamera()
    }

    private fun loadFromHistory() {
        val story = intent.getStringExtra("storyText")
        val photoPath = intent.getStringExtra("photo_path")

        narrationText = story ?: ""
        tvDescription.text = narrationText
        updateNarrationButton()

        if (!photoPath.isNullOrEmpty()) {
            BitmapFactory.decodeFile(photoPath)?.let { imgCaptured.setImageBitmap(it) }
        }
    }

    private fun loadFromCamera() {

        if (hasRequestedStory) return
        hasRequestedStory = true

        val photoPath = intent.getStringExtra("photo_path")
        val landmark = intent.getStringExtra("landmark_name")

        if (photoPath != null) {
            BitmapFactory.decodeFile(photoPath)?.let { imgCaptured.setImageBitmap(it) }
        }

        if (landmark == null) {
            Toast.makeText(this, "No landmark received", Toast.LENGTH_LONG).show()
            return
        }

        tvDescription.text = "Generating story for $landmark..."

        backend.fetchStoryFromBackend(
            landmark, "folklore", "casual", "medium"
        ) { story ->

            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                if (story == null) {
                    tvDescription.text = "Could not generate story."
                    return@runOnUiThread
                }

                narrationText = story
                tvDescription.text = story
                updateNarrationButton()

                if (!hasSaved) {
                    hasSaved = true
                    StoryDatabaseHelper(this).use { database ->
                        database.saveStoryIfNotExists(
                            landmarkName = landmark,
                            storyText = story,
                            imagePath = photoPath
                        )
                    }
                }
            }
        }
    }

    private fun speakWithHighlight(text: String) {
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts1")
        }

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(p0: String?) {}

            override fun onDone(p0: String?) {
                handler.post { resetNarration() }
            }

            override fun onError(p0: String?) {
                handler.post { resetNarration() }
            }

            override fun onRangeStart(id: String?, start: Int, end: Int, frame: Int) {
                handler.post {
                    if (isSpeaking && !isFinishing && !isDestroyed) highlightLine(start, end)
                }
            }
        })

        isSpeaking = true
        btnNarration.setImageResource(R.drawable.ic_pause)
        if (tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "tts1") == TextToSpeech.ERROR) {
            resetNarration()
        }
    }

    private fun updateNarrationButton() {
        btnNarration.isEnabled = ttsReady && narrationText.isNotBlank()
    }

    private fun resetNarration() {
        if (isFinishing || isDestroyed) return
        tvDescription.text = narrationText
        isSpeaking = false
        btnNarration.setImageResource(R.drawable.ic_play_arrow)
    }

    private fun highlightLine(start: Int, end: Int) {
        try {
            val spannable = SpannableStringBuilder(narrationText)
            val highlightColor = ContextCompat.getColor(this, R.color.line_highlight)

            tvDescription.post {
                if (!isSpeaking || isFinishing || isDestroyed) return@post
                val layout = tvDescription.layout ?: return@post
                val line = layout.getLineForOffset(start)
                val lineStart = layout.getLineStart(line)
                val lineEnd = layout.getLineEnd(line)

                spannable.setSpan(
                    BackgroundColorSpan(highlightColor),
                    lineStart,
                    lineEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                tvDescription.text = spannable
            }

        } catch (_: Exception) {}
    }

    private fun setupBottomNav() {
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnNarration.setOnClickListener {
            if (!ttsReady || narrationText.isBlank()) return@setOnClickListener
            if (isSpeaking) {
                tts.stop()
                resetNarration()
            } else {
                speakWithHighlight(narrationText)
            }
        }

        btnBackToNarration.setOnClickListener {
            tvDescription.scrollTo(0, 0)
        }
    }

    override fun onInit(status: Int) {
        if (isFinishing || isDestroyed) return
        if (status == TextToSpeech.SUCCESS) {
            val languageStatus = tts.setLanguage(Locale.US)
            ttsReady = languageStatus != TextToSpeech.LANG_MISSING_DATA &&
                languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
        }
        updateNarrationButton()
    }

    override fun onDestroy() {
        backend.cancelRequests()
        handler.removeCallbacksAndMessages(null)
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
