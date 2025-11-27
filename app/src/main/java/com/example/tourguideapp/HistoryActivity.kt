package com.example.tourguideapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.database.Cursor
import android.graphics.BitmapFactory
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView

class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        loadStories()

        // Set up clickable history items
        val itemCasaLomaHistory = findViewById<View>(R.id.itemCasaLomaHistory)
        itemCasaLomaHistory.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        val itemCasaLomaFolklore = findViewById<View>(R.id.itemCasaLomaFolklore)
        itemCasaLomaFolklore.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        val itemGeorgeBrown = findViewById<View>(R.id.itemGeorgeBrown)
        itemGeorgeBrown.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        val itemBigBen = findViewById<View>(R.id.itemBigBen)
        itemBigBen.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        // Bottom navigation
        val btnProfile: ImageButton = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        val btnHome: ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        val btnReload: ImageButton = findViewById(R.id.btnReload)
        btnReload.setOnClickListener {
            loadStories()
        }

        val btnSettings: ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }

    private fun loadStories() {

        val dbHelper = StoryDatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            "SELECT landmarkName, storyText, imagePath, createdAt FROM stories ORDER BY createdAt DESC",
            null
        )

        val container = findViewById<LinearLayout>(R.id.historyContainer)
        container.removeAllViews()


        // No stories saved
        if (!cursor.moveToFirst()) {
            val emptyText = TextView(this)
            emptyText.text = "No saved stories yet. Generate one!"
            emptyText.textSize = 17f
            emptyText.setPadding(30, 30, 30, 30)

            container.addView(emptyText)
            cursor.close()
            return
        }

        // Loop through stories
        do {
            val name = cursor.getString(0)
            val text = cursor.getString(1)
            val imagePath = cursor.getString(2)
            val dateMillis = cursor.getLong(3)

            val item = layoutInflater.inflate(
                R.layout.item_history_story,
                container,
                false
            )

            val tvTitle = item.findViewById<TextView>(R.id.tvHistoryTitle)
            val tvSnippet = item.findViewById<TextView>(R.id.tvHistorySnippet)
            val tvMeta = item.findViewById<TextView>(R.id.tvHistoryMeta)
            val imgView = item.findViewById<ImageView>(R.id.imgItemHistory)

            tvTitle.text = name

            // Show preview of story
            tvSnippet.text =
                if (text.length > 120) text.substring(0, 120) + "..." else text

            // Format date safely
            val formattedDate = java.text.SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date(dateMillis))

            tvMeta.text = "Saved: $formattedDate"

            // Load image if available
            if (imagePath != null) {
                val bmp = BitmapFactory.decodeFile(imagePath)
                if (bmp != null) imgView.setImageBitmap(bmp)
            }

            container.addView(item)

        } while (cursor.moveToNext())

        cursor.close()
    }
}