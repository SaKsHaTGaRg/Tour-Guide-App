package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ReloadActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        // Get photo path from MainActivity
        val photoPath = intent.getStringExtra(MainActivity.EXTRA_PHOTO_PATH)

        // Simulate 3-second processing
        Handler(Looper.getMainLooper()).postDelayed({
            // Open ResultActivity and pass photo path
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("photo_path", photoPath)
            startActivity(intent)

            // Finish reload so user can't go back here
            finish()
        }, 3000)
    }
}
