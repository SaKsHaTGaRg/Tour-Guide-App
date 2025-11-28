package com.example.tourguideapp

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        loadStories()

        // bottom nav
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            loadStories()
        }

        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }

    override fun onResume() {
        super.onResume()
        loadStories()
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

        if (!cursor.moveToFirst()) {
            val emptyText = TextView(this)
            emptyText.text = "No saved stories yet. Generate one!"
            emptyText.textSize = 17f
            emptyText.setPadding(30, 30, 30, 30)
            container.addView(emptyText)
            cursor.close()
            return
        }

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
            tvSnippet.text = if (text.length > 120) text.substring(0, 120) + "..." else text

            val formattedDate = java.text.SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date(dateMillis))

            tvMeta.text = "Saved: $formattedDate"

            if (!imagePath.isNullOrEmpty()) {
                val bmp = BitmapFactory.decodeFile(imagePath)
                if (bmp != null) imgView.setImageBitmap(bmp)
            }

            item.setOnClickListener {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("landmark_name", name)
                intent.putExtra("storyText", text)
                intent.putExtra("photo_path", imagePath)
                intent.putExtra("fromHistory", true)
                startActivity(intent)
            }

            container.addView(item)

        } while (cursor.moveToNext())

        cursor.close()
    }
}