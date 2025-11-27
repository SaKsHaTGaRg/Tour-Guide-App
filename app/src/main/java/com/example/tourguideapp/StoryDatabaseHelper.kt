package com.example.tourguideapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StoryDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "stories.db", null, 1) {

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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}