package com.example.tourguideapp.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tourguideapp.*
import com.example.tourguideapp.api.ApiKeyProvider
import com.example.tourguideapp.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import android.view.View
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ReloadActivity : AppCompatActivity() {

    private lateinit var txtLoading: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        txtLoading = findViewById(R.id.txtLoading)
        progressBar = findViewById(R.id.progressBar)

        setupBottomNavigation()

        val base64Image = intent.getStringExtra("image_base64")
        if (base64Image != null) {
            callOpenAIVision(base64Image)
        } else {
            txtLoading.text = "No image received."
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        txtLoading = findViewById(R.id.txtLoading)
        progressBar = findViewById(R.id.progressBar)

        setupBottomNavigation()

        val base64Image = intent.getStringExtra("image_base64")

        if (base64Image != null) {
            callOpenAIVision(base64Image)
        } else {
            txtLoading.text = "No image received."
        }
    }*/

    private fun setupBottomNavigation() {
        val btnProfile: ImageButton = findViewById(R.id.btnProfile)
        val btnHome: ImageButton = findViewById(R.id.btnHome)
        val btnReload: ImageButton = findViewById(R.id.btnReload)
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnReload.setOnClickListener {
            // Stay on this screen
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun callOpenAIVision(imageBase64: String) {
        lifecycleScope.launch {
            try {
                // Show loading indicator
                progressBar.visibility = View.VISIBLE
                txtLoading.text = "Identifying landmark..."

                // Build JSON body for OpenAI Vision
                val json = """
                {
                  "model": "gpt-4o-mini",
                  "messages": [
                    {
                      "role": "user",
                      "content": [
                        {"type": "text", "text": "Identify this landmark"},
                        {"type": "image_url",
                         "image_url": {
                           "url": "data:image/jpeg;base64,$imageBase64"
                         }
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

                // Convert to RequestBody
                val requestBody = json.toRequestBody("application/json".toMediaType())

                // Make API call (this is now CORRECT)
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.openAIService.analyzeImage(requestBody)
                }

                val responseString = response.string()

                // Parse JSON
                val jsonObj = JSONObject(responseString)
                val landmark: String = jsonObj
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()

                // Go to result screen
                val intent = Intent(this@ReloadActivity, ResultActivity::class.java)
                intent.putExtra("landmarkName", landmark)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
                txtLoading.text = "Error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

}
