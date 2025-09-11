package com.vst.knotes.MVVM.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milk_collections",
    foreignKeys = [
        ForeignKey(
            entity = FarmerModel::class,
            parentColumns = arrayOf("farmerId"),
            childColumns = arrayOf("farmerId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["farmerId"])]
)
data class MilkCollectionModel(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Int = 0,
    val farmerId: Int,
    val collectionDate: String,
    val collectionTime: String,
    val quantity: Double,
    val fat: Double,
    val snf: Double,
    val rate: Double,
    val amount: Double,
    val rateType: String, // "CM" or "BM"
    val remarks: String? = null
)