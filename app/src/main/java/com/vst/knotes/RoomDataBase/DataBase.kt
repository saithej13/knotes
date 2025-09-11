package com.vst.knotes.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vst.knotes.MVVM.Model.FarmerModel
import com.vst.knotes.MVVM.Model.MilkCollectionModel
import com.vst.knotes.MVVM.Model.RateModel


@Database(
    entities = [
        NotePageDO::class,
        FarmerModel::class,
        MilkCollectionModel::class,
        RateModel::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract fun roomService(): QueryClass
    abstract fun farmerDao(): FarmerDao
    abstract fun milkCollectionDao(): MilkCollectionDao
    abstract fun rateDao(): RateDao

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
