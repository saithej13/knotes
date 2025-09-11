package com.vst.knotes.MVVM.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vst.knotes.MVVM.Model.MilkCollectionModel
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.MVVM.Repositories.MilkCollectionRepository
import com.vst.knotes.MVVM.Repositories.RateRepository
import com.vst.knotes.RoomDataBase.DataBase
import com.vst.knotes.utils.MilkPriceCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MilkCollectionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val milkCollectionRepository: MilkCollectionRepository
    private val rateRepository: RateRepository
    
    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult
    
    private val _calculatedAmount = MutableLiveData<Double>()
    val calculatedAmount: LiveData<Double> = _calculatedAmount
    
    private val _ratePerLiter = MutableLiveData<Double>()
    val ratePerLiter: LiveData<Double> = _ratePerLiter
    
    val allCollectionsWithFarmers: LiveData<List<MilkCollectionWithFarmerModel>>
    
    init {
        val database = DataBase.getInstance(application)
        milkCollectionRepository = MilkCollectionRepository(database.milkCollectionDao())
        rateRepository = RateRepository(database.rateDao())
        allCollectionsWithFarmers = milkCollectionRepository.getAllCollectionsWithFarmers()
    }
    
    fun addMilkCollection(
        farmerId: Int,
        quantity: Double,
        fat: Double,
        snf: Double,
        rateType: String,
        remarks: String? = null
    ) {
        if (validateCollectionInput(farmerId, quantity, fat, snf, rateType)) {
            viewModelScope.launch {
                try {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    val rate = if (rateType == MilkPriceCalculator.RATE_TYPE_CM) {
                        rateRepository.findRateByTypeAndDateAndFatSnf(rateType, currentDate, fat, snf)
                    } else {
                        rateRepository.getLatestRateByType(rateType)
                    }
                    if (rate == null) {
                        _operationResult.value = OperationResult.Error("No active $rateType rate found. Please configure rates first.")
                        return@launch
                    }
                    val calculatedPrice = MilkPriceCalculator.calculatePrice(quantity, fat, snf, rate)
                    val pricePerLiter = MilkPriceCalculator.getRatePerLiter(fat, snf, rate)
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val currentTime = timeFormat.format(Date())
                    val collection = MilkCollectionModel(
                        farmerId = farmerId,
                        collectionDate = currentDate,
                        collectionTime = currentTime,
                        quantity = quantity,
                        fat = fat,
                        snf = snf,
                        rate = pricePerLiter,
                        amount = calculatedPrice,
                        rateType = rateType,
                        remarks = remarks
                    )
                    val result = milkCollectionRepository.insertCollection(collection)
                    if (result > 0) {
                        _operationResult.value = OperationResult.Success("Milk collection added successfully")
                    } else {
                        _operationResult.value = OperationResult.Error("Failed to add milk collection")
                    }
                } catch (e: Exception) {
                    _operationResult.value = OperationResult.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    fun updateMilkCollection(
        collectionId: Int,
        farmerId: Int,
        quantity: Double,
        fat: Double,
        snf: Double,
        rateType: String,
        collectionDate: String,
        collectionTime: String,
        remarks: String? = null
    ) {
        if (validateCollectionInput(farmerId, quantity, fat, snf, rateType)) {
            viewModelScope.launch {
                try {
                    val rate = if (rateType == MilkPriceCalculator.RATE_TYPE_CM) {
                        rateRepository.findRateByTypeAndDateAndFatSnf(rateType, collectionDate, fat, snf)
                    } else {
                        rateRepository.getLatestRateByType(rateType)
                    }
                    if (rate == null) {
                        _operationResult.value = OperationResult.Error("No active $rateType rate found")
                        return@launch
                    }
                    val calculatedPrice = MilkPriceCalculator.calculatePrice(quantity, fat, snf, rate)
                    val pricePerLiter = MilkPriceCalculator.getRatePerLiter(fat, snf, rate)
                    val collection = MilkCollectionModel(
                        collectionId = collectionId,
                        farmerId = farmerId,
                        collectionDate = collectionDate,
                        collectionTime = collectionTime,
                        quantity = quantity,
                        fat = fat,
                        snf = snf,
                        rate = pricePerLiter,
                        amount = calculatedPrice,
                        rateType = rateType,
                        remarks = remarks
                    )
                    milkCollectionRepository.updateCollection(collection)
                    _operationResult.value = OperationResult.Success("Milk collection updated successfully")
                } catch (e: Exception) {
                    _operationResult.value = OperationResult.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    fun deleteMilkCollection(collection: MilkCollectionModel) {
        viewModelScope.launch {
            try {
                milkCollectionRepository.deleteCollection(collection)
                _operationResult.value = OperationResult.Success("Milk collection deleted successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error: ${e.message}")
            }
        }
    }
    
    fun calculatePricePreview(
        quantity: Double,
        fat: Double,
        snf: Double,
        rateType: String
    ) {
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                val rate = if (rateType == MilkPriceCalculator.RATE_TYPE_CM) {
                    rateRepository.findRateByTypeAndDateAndFatSnf(rateType, currentDate, fat, snf)
                } else {
                    rateRepository.getLatestRateByType(rateType)
                }
                if (rate != null) {
                    val totalAmount = MilkPriceCalculator.calculatePrice(quantity, fat, snf, rate)
                    val pricePerLiter = MilkPriceCalculator.getRatePerLiter(fat, snf, rate)
                    _calculatedAmount.value = totalAmount
                    _ratePerLiter.value = pricePerLiter
                } else {
                    _operationResult.value = OperationResult.Error("No active $rateType rate found")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error calculating price: ${e.message}")
            }
        }
    }
    
    fun getCollectionsByFarmer(farmerId: Int): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionRepository.getCollectionsByFarmer(farmerId)
    }
    
    fun getCollectionsByDate(date: String): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionRepository.getCollectionsByDate(date)
    }
    
    fun getCollectionsByDateRange(startDate: String, endDate: String): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionRepository.getCollectionsByDateRange(startDate, endDate)
    }
    
    suspend fun getTotalAmountByFarmerAndDateRange(farmerId: Int, startDate: String, endDate: String): Double {
        return milkCollectionRepository.getTotalAmountByFarmerAndDateRange(farmerId, startDate, endDate)
    }
    
    suspend fun getTotalQuantityByFarmerAndDateRange(farmerId: Int, startDate: String, endDate: String): Double {
        return milkCollectionRepository.getTotalQuantityByFarmerAndDateRange(farmerId, startDate, endDate)
    }
    
    fun getBillingCycleCollections(farmerId: Int, year: Int, month: Int, startDay: Int, endDay: Int): LiveData<List<MilkCollectionWithFarmerModel>> {
        return milkCollectionRepository.getBillingCycleCollections(farmerId, year, month, startDay, endDay)
    }
    
    private fun validateCollectionInput(
        farmerId: Int,
        quantity: Double,
        fat: Double,
        snf: Double,
        rateType: String
    ): Boolean {
        when {
            farmerId <= 0 -> {
                _operationResult.value = OperationResult.Error("Please select a farmer")
                return false
            }
            quantity <= 0 -> {
                _operationResult.value = OperationResult.Error("Quantity must be greater than 0")
                return false
            }
            fat <= 0 || fat > 13 -> {
                _operationResult.value = OperationResult.Error("Fat percentage must be between 0 and 13")
                return false
            }
            !MilkPriceCalculator.isValidSnfRange(snf) -> {
                _operationResult.value = OperationResult.Error("SNF percentage must be between 8.0 and 9.0")
                return false
            }
            rateType.isBlank() -> {
                _operationResult.value = OperationResult.Error("Please select a rate type")
                return false
            }
        }
        return true
    }
    
    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }
}