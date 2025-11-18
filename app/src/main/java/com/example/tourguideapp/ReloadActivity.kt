package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.TextView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tourguideapp.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import org.json.JSONObject

class ReloadActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        statusText = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)

        val imageBase64 = intent.getStringExtra("image_base64") ?: return

        callOpenAIVision(imageBase64)
    }

    private fun callOpenAIVision(imageBase64: String) {
        lifecycleScope.launch {
            statusText.text = "Identifying landmark..."

            val json = JSONObject().apply {
                put("model", "gpt-4o-mini")
                put("messages", listOf(
                    JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a landmark identifier. Respond ONLY with the landmark name.")
                    },
                    JSONObject().apply {
                        put("role", "user")
                        put("content", listOf(
                            JSONObject().apply {
                                put("type", "text")
                                put("text", "What landmark is this?")
                            },
                            JSONObject().apply {
                                put("type", "image_url")
                                put("image_url", JSONObject().apply {
                                    put("url", "data:image/jpeg;base64,$imageBase64")
                                })
                            }
                        ))
                    }
                ))
            }

            val requestBody = RequestBody.create(
                "application/json".toMediaType(),
                json.toString()
            )

            try {
                val responseStr = withContext(Dispatchers.IO) {
                    RetrofitClient.openAIService.analyzeImage(requestBody).string()
                }

                // --- Extract landmark name from OpenAI response ---
                val parsed = JSONObject(responseStr)
                val landmarkName = parsed
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()

                // GO TO ResultActivity with landmark name
                val intent = Intent(this@ReloadActivity, ResultActivity::class.java)
                intent.putExtra("landmark_name", landmarkName)
                intent.putExtra("story_text", " ") // empty for now
                intent.putExtra("image_base64", imageBase64)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                statusText.text = "Error identifying landmark."
            }
        }
    }
}
