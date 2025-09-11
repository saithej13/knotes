package com.vst.knotes.MVVM.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class FarmerModel(
    @PrimaryKey(autoGenerate = true)
    val farmerId: Int = 0,
    val farmerCode: String,
    val farmerName: String,
    val mobileNumber: String,
    val address: String,
    val isActive: Boolean = true,
    val createdDate: String,
    val updatedDate: String? = null
)