package com.vst.knotes.RoomDataBase.SQLite

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import com.vst.knotes.MVVM.Services.AppConstants
import com.vst.knotes.Utils.MyApplication
import com.vst.knotes.Utils.Preference
import com.vst.knotes.Utils.SQLITEFILEPATH

class DatabaseHelper private constructor() {

    companion object {
        private var _database: SQLiteDatabase? = null
        private lateinit var preference: Preference  // Ensure preference is initialized properly

        @Throws(SQLException::class)
        fun openDatabase(): SQLiteDatabase? {
            return try {
                Log.d("DatabaseHelper", "Attempting to open database...")

                val context = MyApplication.getInstance()?.applicationContext
                if (context == null) {
                    Log.e("DatabaseHelper", "Context is null! Cannot proceed.")
                    return null
                }

                if (AppConstants.DATABASE_PATH.isNullOrEmpty()) {
                    Log.e("DatabaseHelper", "DATABASE_PATH is empty! Retrieving from shared preferences.")
                }

                val preference = Preference(context)
                val path = preference.getStringFromPreference(SQLITEFILEPATH, "")

                if (path.isEmpty()) {
                    Log.e("DatabaseHelper", "Stored database path is empty!")
                    return null
                } else {
                    AppConstants.DATABASE_PATH = path
                }

                val fullPath = "${AppConstants.DATABASE_PATH}${AppConstants.DATABASE_NAME}"
                Log.d("DatabaseHelper", "Final database path: $fullPath")

                if (_database == null || !_database!!.isOpen) {
                    _database = SQLiteDatabase.openDatabase(
                        fullPath,
                        null,
                        SQLiteDatabase.OPEN_READWRITE or SQLiteDatabase.CREATE_IF_NECESSARY
                    )
                }

                Log.d("DatabaseHelper", "Database opened successfully!")
                _database
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error opening database: ${e.localizedMessage}", e)
                null
            }
        }


        fun closeDatabase() {
            _database?.apply {
                if (isOpen) {
                    close()
                    _database = null
                }
            }
        }
    }
}