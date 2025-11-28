package com.example.tourguideapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StoryDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "stories.db", null, 1) {

    companion object {
        private val savedStories = mutableSetOf<String>()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE stories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                landmarkName TEXT,
                storyText TEXT,
                imagePath TEXT,
                createdAt LONG
            );
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun saveStoryIfNotExists(
        landmarkName: String,
        storyText: String,
        imagePath: String?
    ) {
        val key = storyText.trim()

        if (savedStories.contains(key)) return

        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM stories WHERE storyText = ?",
            arrayOf(storyText)
        )

        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()

        if (exists) {
            savedStories.add(key)
            return
        }

        db.execSQL(
            """
            INSERT INTO stories (landmarkName, storyText, imagePath, createdAt)
            VALUES (?, ?, ?, ?)
            """,
            arrayOf(landmarkName, storyText, imagePath, System.currentTimeMillis())
        )

        savedStories.add(key)
    }
}