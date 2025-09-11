package com.vst.knotes.MVVM.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vst.knotes.MVVM.Model.RateModel
import com.vst.knotes.MVVM.Repositories.RateRepository
import com.vst.knotes.RoomDataBase.DataBase
import com.vst.knotes.utils.MilkPriceCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RateViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: RateRepository
    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult
    
    val allActiveRates: LiveData<List<RateModel>>
    
    init {
        val rateDao = DataBase.getInstance(application).rateDao()
        repository = RateRepository(rateDao)
        allActiveRates = repository.getAllActiveRates()
    }
    
    fun insertCMRate(
        rate: Double,
        startDate: String,
        endDate: String,
        minFat: Double?,
        maxFat: Double?,
        minSnf: Double?,
        maxSnf: Double?
    ) {
        if (validateCMRateInput(rate, startDate, endDate)) {
            viewModelScope.launch {
                try {
                    repository.deactivateRatesByType(MilkPriceCalculator.RATE_TYPE_CM)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    val rateModel = RateModel(
                        rateType = MilkPriceCalculator.RATE_TYPE_CM,
                        rate = rate,
                        minFat = minFat,
                        maxFat = maxFat,
                        minSnf = minSnf,
                        maxSnf = maxSnf,
                        startDate = startDate,
                        endDate = endDate,
                        createdDate = currentDate
                    )
                    val result = repository.insertRate(rateModel)
                    if (result > 0) {
                        _operationResult.value = OperationResult.Success("CM rate added successfully")
                    } else {
                        _operationResult.value = OperationResult.Error("Failed to add CM rate")
                    }
                } catch (e: Exception) {
                    _operationResult.value = OperationResult.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    fun insertBMRate(
        rate: Double?,
        startDate: String,
        endDate: String,
        minFat: Double?,
        maxFat: Double?,
        minSnf: Double?,
        maxSnf: Double?
    ) {
        if (validateBMRateInput(rate, startDate, endDate)) {
            viewModelScope.launch {
                try {
                    repository.deactivateRatesByType(MilkPriceCalculator.RATE_TYPE_BM)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    val rateModel = RateModel(
                        rateType = MilkPriceCalculator.RATE_TYPE_BM,
                        rate = rate,
                        minFat = minFat,
                        maxFat = maxFat,
                        minSnf = minSnf,
                        maxSnf = maxSnf,
                        startDate = startDate,
                        endDate = endDate,
                        createdDate = currentDate
                    )
                    val result = repository.insertRate(rateModel)
                    if (result > 0) {
                        _operationResult.value = OperationResult.Success("BM rate added successfully")
                    } else {
                        _operationResult.value = OperationResult.Error("Failed to add BM rate")
                    }
                } catch (e: Exception) {
                    _operationResult.value = OperationResult.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    fun updateRate(rate: RateModel) {
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                
                val updatedRate = rate.copy(updatedDate = currentDate)
                repository.updateRate(updatedRate)
                _operationResult.value = OperationResult.Success("Rate updated successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error: ${e.message}")
            }
        }
    }
    
    fun deactivateRate(rate: RateModel) {
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                
                val deactivatedRate = rate.copy(isActive = false, updatedDate = currentDate)
                repository.updateRate(deactivatedRate)
                _operationResult.value = OperationResult.Success("Rate deactivated successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error: ${e.message}")
            }
        }
    }
    
    suspend fun getLatestRateByType(rateType: String): RateModel? {
        return repository.getLatestRateByType(rateType)
    }
    
    suspend fun getRateById(rateId: Int): RateModel? {
        return repository.getRateById(rateId)
    }
    
    fun calculateSamplePrice(
        rateType: String,
        fat: Double,
        snf: Double,
        quantity: Double = 1.0
    ): Double {
        viewModelScope.launch {
            try {
                val rate = repository.getLatestRateByType(rateType)
                if (rate != null) {
                    val price = MilkPriceCalculator.calculatePrice(quantity, fat, snf, rate)
                    _operationResult.value = OperationResult.PriceCalculated(price)
                } else {
                    _operationResult.value = OperationResult.Error("No active $rateType rate found")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error calculating price: ${e.message}")
            }
        }
        return 0.0
    }
    
    private fun validateCMRateInput(rate: Double, startDate: String, endDate: String): Boolean {
        when {
            rate <= 0 -> {
                _operationResult.value = OperationResult.Error("Rate must be greater than 0")
                return false
            }
            startDate.isBlank() -> {
                _operationResult.value = OperationResult.Error("Start date is required")
                return false
            }
            endDate.isBlank() -> {
                _operationResult.value = OperationResult.Error("End date is required")
                return false
            }
        }
        return true
    }
    
    private fun validateBMRateInput(rate: Double?, startDate: String, endDate: String): Boolean {
        when {
            rate == null || rate <= 0 -> {
                _operationResult.value = OperationResult.Error("Rate must be greater than 0")
                return false
            }
            startDate.isBlank() -> {
                _operationResult.value = OperationResult.Error("Start date is required")
                return false
            }
            endDate.isBlank() -> {
                _operationResult.value = OperationResult.Error("End date is required")
                return false
            }
        }
        return true
    }
    
    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
        data class PriceCalculated(val price: Double) : OperationResult()
    }
}