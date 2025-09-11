package com.vst.knotes.MVVM.Repositories

import androidx.lifecycle.LiveData
import com.vst.knotes.MVVM.Model.MilkCollectionModel
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.RoomDataBase.MilkCollectionDao

class MilkCollectionRepository(private val milkCollectionDao: MilkCollectionDao) {
    
    suspend fun insertCollection(collection: MilkCollectionModel): Long {
        return milkCollectionDao.insertCollection(collection)
    }
    
    suspend fun updateCollection(collection: MilkCollectionModel) {
        milkCollectionDao.updateCollection(collection)
    }
    
    suspend fun deleteCollection(collection: MilkCollectionModel) {
        milkCollectionDao.deleteCollection(collection)
    }
    
    fun getAllCollectionsWithFarmers(): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionDao.getAllCollectionsWithFarmers()
    }
    
    fun getCollectionsByFarmer(farmerId: Int): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionDao.getCollectionsByFarmer(farmerId)
    }
    
    fun getCollectionsByDate(date: String): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionDao.getCollectionsByDate(date)
    }
    
    fun getCollectionsByDateRange(startDate: String, endDate: String): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionDao.getCollectionsByDateRange(startDate, endDate)
    }
    
    suspend fun getTotalAmountByFarmerAndDateRange(farmerId: Int, startDate: String, endDate: String): Double {
        return milkCollectionDao.getTotalAmountByFarmerAndDateRange(farmerId, startDate, endDate) ?: 0.0
    }
    
    suspend fun getTotalQuantityByFarmerAndDateRange(farmerId: Int, startDate: String, endDate: String): Double {
        return milkCollectionDao.getTotalQuantityByFarmerAndDateRange(farmerId, startDate, endDate) ?: 0.0
    }
    
    suspend fun getTotalAmountByDateRange(startDate: String, endDate: String): Double {
        return milkCollectionDao.getTotalAmountByDateRange(startDate, endDate) ?: 0.0
    }
    
    suspend fun getTotalQuantityByDateRange(startDate: String, endDate: String): Double {
        return milkCollectionDao.getTotalQuantityByDateRange(startDate, endDate) ?: 0.0
    }
    
    fun getBillingCycleCollections(farmerId: Int, year: Int, month: Int, startDay: Int, endDay: Int): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionDao.getBillingCycleCollections(farmerId, year, month, startDay, endDay)
    }
}