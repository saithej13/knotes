package com.vst.knotes.RoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vst.knotes.MVVM.Model.MilkCollectionModel
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel

@Dao
interface MilkCollectionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: MilkCollectionModel): Long
    
    @Update
    suspend fun updateCollection(collection: MilkCollectionModel)
    
    @Delete
    suspend fun deleteCollection(collection: MilkCollectionModel)
    
    @Transaction
    @Query("SELECT * FROM milk_collections ORDER BY collectionDate DESC, collectionTime DESC")
    fun getAllCollectionsWithFarmers(): LiveData<List<MilkCollectionWithFarmerModel>>
    
    @Transaction
    @Query("SELECT * FROM milk_collections WHERE farmerId = :farmerId ORDER BY collectionDate DESC, collectionTime DESC")
    fun getCollectionsByFarmer(farmerId: Int): LiveData<List<MilkCollectionWithFarmerModel>>
    
    @Transaction
    @Query("SELECT * FROM milk_collections WHERE collectionDate = :date ORDER BY collectionTime DESC")
    fun getCollectionsByDate(date: String): LiveData<List<MilkCollectionWithFarmerModel>>
    
    @Transaction
    @Query("SELECT * FROM milk_collections WHERE collectionDate BETWEEN :startDate AND :endDate ORDER BY collectionDate DESC, collectionTime DESC")
    fun getCollectionsByDateRange(startDate: String, endDate: String): LiveData<List<MilkCollectionWithFarmerModel>>
    
    @Query("SELECT SUM(amount) FROM milk_collections WHERE farmerId = :farmerId AND collectionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalAmountByFarmerAndDateRange(farmerId: Int, startDate: String, endDate: String): Double?
    
    @Query("SELECT SUM(quantity) FROM milk_collections WHERE farmerId = :farmerId AND collectionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalQuantityByFarmerAndDateRange(farmerId: Int, startDate: String, endDate: String): Double?
    
    @Query("SELECT SUM(amount) FROM milk_collections WHERE collectionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalAmountByDateRange(startDate: String, endDate: String): Double?
    
    @Query("SELECT SUM(quantity) FROM milk_collections WHERE collectionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalQuantityByDateRange(startDate: String, endDate: String): Double?
    
    @Transaction
    @Query("""
        SELECT * FROM milk_collections 
        WHERE farmerId = :farmerId 
        AND CAST(substr(collectionDate, 7, 4) AS INTEGER) = :year 
        AND CAST(substr(collectionDate, 4, 2) AS INTEGER) = :month 
        AND CAST(substr(collectionDate, 1, 2) AS INTEGER) BETWEEN :startDay AND :endDay
        ORDER BY collectionDate DESC, collectionTime DESC
    """)
    fun getBillingCycleCollections(farmerId: Int, year: Int, month: Int, startDay: Int, endDay: Int): LiveData<List<MilkCollectionWithFarmerModel>>
}