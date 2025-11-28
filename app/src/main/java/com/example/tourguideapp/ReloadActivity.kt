package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class ReloadActivity : BaseActivity() {

    private val backend = Backend()
    private var hasStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        // Get photo path from MainActivity
        val photoPath = intent.getStringExtra(MainActivity.EXTRA_PHOTO_PATH)

        if (photoPath == null) {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            analyzeImage(photoPath)
        }, 2000)
    }

    private fun analyzeImage(photoPath: String) {
        // extra guard so dont start ResultActivity twice
        if (hasStarted) return
        hasStarted = true

        backend.uploadImageToBackend(photoPath) { landmarkName ->
            runOnUiThread {
                if (landmarkName == null) {
                    Toast.makeText(this, "Backend error", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("photo_path", photoPath)
                intent.putExtra("landmark_name", landmarkName)
                startActivity(intent)

                finish()
            }
        }
    }
}