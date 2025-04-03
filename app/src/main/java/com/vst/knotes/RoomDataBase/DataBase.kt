package com.vst.knotes.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [NotePageDO::class], version = 1, exportSchema = false)
abstract class DataBase : RoomDatabase() {
    abstract fun roomService(): QueryClass

    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getInstance(context: Context): DataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, DataBase::class.java, "notes_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}
