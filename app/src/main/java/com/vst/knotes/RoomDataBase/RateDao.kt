package com.vst.knotes.RoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vst.knotes.MVVM.Model.RateModel

@Dao
interface RateDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: RateModel): Long
    
    @Update
    suspend fun updateRate(rate: RateModel)
    
    @Delete
    suspend fun deleteRate(rate: RateModel)
    
    @Query("SELECT * FROM rates WHERE isActive = 1 ORDER BY startDate DESC")
    fun getAllActiveRates(): LiveData<List<RateModel>>
    
    @Query("SELECT * FROM rates WHERE rateType = :rateType AND isActive = 1 ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestRateByType(rateType: String): RateModel?
    
    @Query("SELECT * FROM rates WHERE rateId = :rateId")
    suspend fun getRateById(rateId: Int): RateModel?
    
    @Query("UPDATE rates SET isActive = 0 WHERE rateType = :rateType")
    suspend fun deactivateRatesByType(rateType: String)

    @Query("SELECT * FROM rates WHERE rateType = :rateType AND isActive = 1 AND :date BETWEEN startDate AND endDate AND :fat BETWEEN IFNULL(minFat, :fat) AND IFNULL(maxFat, :fat) AND :snf BETWEEN IFNULL(minSnf, :snf) AND IFNULL(maxSnf, :snf) LIMIT 1")
    suspend fun findRateByTypeAndDateAndFatSnf(rateType: String, date: String, fat: Double, snf: Double): RateModel?
}