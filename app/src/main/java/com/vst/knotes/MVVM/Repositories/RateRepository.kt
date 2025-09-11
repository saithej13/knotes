package com.vst.knotes.MVVM.Repositories

import androidx.lifecycle.LiveData
import com.vst.knotes.MVVM.Model.RateModel
import com.vst.knotes.RoomDataBase.RateDao

class RateRepository(private val rateDao: RateDao) {
    
    suspend fun insertRate(rate: RateModel): Long {
        return rateDao.insertRate(rate)
    }
    
    suspend fun updateRate(rate: RateModel) {
        rateDao.updateRate(rate)
    }
    
    suspend fun deleteRate(rate: RateModel) {
        rateDao.deleteRate(rate)
    }
    
    fun getAllActiveRates(): LiveData<List<RateModel>> {
        return rateDao.getAllActiveRates()
    }
    
    suspend fun getLatestRateByType(rateType: String): RateModel? {
        return rateDao.getLatestRateByType(rateType)
    }
    
    suspend fun getRateById(rateId: Int): RateModel? {
        return rateDao.getRateById(rateId)
    }
    
    suspend fun deactivateRatesByType(rateType: String) {
        rateDao.deactivateRatesByType(rateType)
    }

    suspend fun findRateByTypeAndDateAndFatSnf(rateType: String, date: String, fat: Double, snf: Double): RateModel? {
        return rateDao.findRateByTypeAndDateAndFatSnf(rateType, date, fat, snf)
    }
}