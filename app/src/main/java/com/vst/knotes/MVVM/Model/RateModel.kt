package com.vst.knotes.MVVM.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rates")
data class RateModel(
    @PrimaryKey(autoGenerate = true)
    val rateId: Int = 0,
    val rateType: String, // "CM" or "BM"
    val rate: Double? = null, // Single rate value
    val minFat: Double? = null, // Minimum fat
    val maxFat: Double? = null, // Maximum fat
    val minSnf: Double? = null, // Minimum snf
    val maxSnf: Double? = null, // Maximum snf
    val baseFatRate: Double? = null, // Used for BM type (fat * rate)
    val snfPenalty: Double? = null, // Used for BM type penalty calculation
    val isActive: Boolean = true,
    val startDate: String, // New field
    val endDate: String,   // New field
    val createdDate: String,
    val updatedDate: String? = null
)