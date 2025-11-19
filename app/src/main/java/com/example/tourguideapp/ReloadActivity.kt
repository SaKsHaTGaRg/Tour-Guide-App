package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tourguideapp.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ReloadActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        tvStatus = findViewById(R.id.txtLoading)
        progressBar = findViewById(R.id.progressBar)

        setupBottomNavigation()

        val imgBase64 = intent.getStringExtra("image_base64")
        if (imgBase64 != null) {
            analyze(imgBase64)
        } else {
            tvStatus.text = "No image received."
        }
    }

    private fun setupBottomNavigation() {
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.btnReload).setOnClickListener { }
        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener { finish() }
    }

    private fun analyze(base64: String) {
        lifecycleScope.launch {

            progressBar.visibility = View.VISIBLE
            tvStatus.text = "Identifying landmark..."

            val json = """
                {
                  "model": "gpt-4o-mini",
                  "input": [
                    {
                      "role": "user",
                      "content": [
                        {"type": "input_text", "text": "Identify this landmark."},
                        {
                          "type": "input_image",
                          "image_url": "data:image/jpeg;base64,$base64"
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaType())

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.openAIService.analyzeImage(body)
                }

                val resStr = response.string()
                val jsonObj = JSONObject(resStr)

                val output = jsonObj
                    .getJSONArray("output")
                    .getJSONObject(0)
                    .getString("content")

                val intent = Intent(this@ReloadActivity, ResultActivity::class.java)
                intent.putExtra("landmarkName", output)
                startActivity(intent)

            } catch (e: Exception) {
                tvStatus.text = "Error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
