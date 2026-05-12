package com.example.lab3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, "languages.db", null, 1
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "language TEXT NOT NULL," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS history")
        onCreate(db)
    }

    fun insertLanguage(language: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("language", language)
        }
        val result = db.insert("history", null, values)
        db.close()
        return result != -1L
    }

    fun getAllLanguages(): List<String> {
        val list = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT language, timestamp FROM history ORDER BY id DESC", null
        )
        while (cursor.moveToNext()) {
            val language = cursor.getString(0)
            val timestamp = cursor.getString(1)
            list.add("$language  —  $timestamp")
        }
        cursor.close()
        db.close()
        return list
    }

    fun clearAll() {
        val db = writableDatabase
        db.execSQL("DELETE FROM history")
        db.close()
    }
}