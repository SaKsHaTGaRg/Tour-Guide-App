package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
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

        analyzeImage(photoPath)
    }

    private fun analyzeImage(photoPath: String) {
        // extra guard so dont start ResultActivity twice
        if (hasStarted) return
        hasStarted = true

        backend.uploadImageToBackend(photoPath) { landmarkName ->
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                if (landmarkName == null) {
                    Toast.makeText(this, "Could not recognize the photo. Check your connection and try again.", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                if (landmarkName.equals("Unknown landmark", ignoreCase = true)) {
                    Toast.makeText(this, "No landmark recognized. Try a clearer photo.", Toast.LENGTH_LONG).show()
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

    override fun onDestroy() {
        backend.cancelRequests()
        super.onDestroy()
    }
}
