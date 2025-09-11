package com.vst.knotes.RoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vst.knotes.MVVM.Model.FarmerModel

@Dao
interface FarmerDao {
    
    @Query("SELECT * FROM farmers WHERE isActive = 1 ORDER BY farmerName ASC")
    fun getAllActiveFarmers(): LiveData<List<FarmerModel>>
    
    @Query("SELECT * FROM farmers ORDER BY farmerName ASC")
    fun getAllFarmers(): LiveData<List<FarmerModel>>
    
    @Query("SELECT * FROM farmers WHERE farmerId = :farmerId")
    suspend fun getFarmerById(farmerId: Int): FarmerModel?
    
    @Query("SELECT * FROM farmers WHERE farmerCode = :farmerCode")
    suspend fun getFarmerByCode(farmerCode: String): FarmerModel?
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFarmer(farmer: FarmerModel): Long
    
    @Update
    suspend fun updateFarmer(farmer: FarmerModel)
    
    @Query("UPDATE farmers SET isActive = 0 WHERE farmerId = :farmerId")
    suspend fun deactivateFarmer(farmerId: Int)
    
    @Delete
    suspend fun deleteFarmer(farmer: FarmerModel)
    
    @Query("SELECT COUNT(*) FROM farmers WHERE farmerCode = :farmerCode")
    suspend fun isCodeExists(farmerCode: String): Int
}