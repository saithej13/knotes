package com.vst.knotes.MVVM.Repositories

import androidx.lifecycle.LiveData
import com.vst.knotes.MVVM.Model.FarmerModel
import com.vst.knotes.RoomDataBase.FarmerDao

class FarmerRepository(private val farmerDao: FarmerDao) {
    
    fun getAllActiveFarmers(): LiveData<List<FarmerModel>> = farmerDao.getAllActiveFarmers()
    
    fun getAllFarmers(): LiveData<List<FarmerModel>> = farmerDao.getAllFarmers()
    
    suspend fun getFarmerById(farmerId: Int): FarmerModel? = farmerDao.getFarmerById(farmerId)
    
    suspend fun getFarmerByCode(farmerCode: String): FarmerModel? = farmerDao.getFarmerByCode(farmerCode)
    
    suspend fun insertFarmer(farmer: FarmerModel): Long {
        return farmerDao.insertFarmer(farmer)
    }
    
    suspend fun updateFarmer(farmer: FarmerModel) {
        farmerDao.updateFarmer(farmer)
    }
    
    suspend fun deactivateFarmer(farmerId: Int) {
        farmerDao.deactivateFarmer(farmerId)
    }
    
    suspend fun deleteFarmer(farmer: FarmerModel) {
        farmerDao.deleteFarmer(farmer)
    }
    
    suspend fun isCodeExists(farmerCode: String): Boolean {
        return farmerDao.isCodeExists(farmerCode) > 0
    }
}