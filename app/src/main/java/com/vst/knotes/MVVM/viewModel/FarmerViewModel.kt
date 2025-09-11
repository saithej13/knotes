package com.vst.knotes.MVVM.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vst.knotes.MVVM.Model.FarmerModel
import com.vst.knotes.MVVM.Repositories.FarmerRepository
import com.vst.knotes.RoomDataBase.DataBase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FarmerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: FarmerRepository
    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult
    
    val allActiveFarmers: LiveData<List<FarmerModel>>
    val allFarmers: LiveData<List<FarmerModel>>
    
    init {
        val farmerDao = DataBase.getInstance(application).farmerDao()
        repository = FarmerRepository(farmerDao)
        allActiveFarmers = repository.getAllActiveFarmers()
        allFarmers = repository.getAllFarmers()
    }
    
    fun insertFarmer(
        farmerCode: String,
        farmerName: String,
        mobileNumber: String,
        address: String
    ) {
        if (validateFarmerInput(farmerCode, farmerName, mobileNumber)) {
            viewModelScope.launch {
                try {
                    if (repository.isCodeExists(farmerCode)) {
                        _operationResult.value = OperationResult.Error("Farmer code already exists")
                        return@launch
                    }
                    
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    
                    val farmer = FarmerModel(
                        farmerCode = farmerCode,
                        farmerName = farmerName,
                        mobileNumber = mobileNumber,
                        address = address,
                        createdDate = currentDate
                    )
                    
                    val result = repository.insertFarmer(farmer)
                    if (result > 0) {
                        _operationResult.value = OperationResult.Success("Farmer added successfully")
                    } else {
                        _operationResult.value = OperationResult.Error("Failed to add farmer")
                    }
                } catch (e: Exception) {
                    _operationResult.value = OperationResult.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    fun updateFarmer(
        farmerId: Int,
        farmerCode: String,
        farmerName: String,
        mobileNumber: String,
        address: String
    ) {
        if (validateFarmerInput(farmerCode, farmerName, mobileNumber)) {
            viewModelScope.launch {
                try {
                    val existingFarmer = repository.getFarmerById(farmerId)
                    if (existingFarmer == null) {
                        _operationResult.value = OperationResult.Error("Farmer not found")
                        return@launch
                    }
                    
                    // Check if code exists for other farmers
                    val farmerWithSameCode = repository.getFarmerByCode(farmerCode)
                    if (farmerWithSameCode != null && farmerWithSameCode.farmerId != farmerId) {
                        _operationResult.value = OperationResult.Error("Farmer code already exists")
                        return@launch
                    }
                    
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    
                    val updatedFarmer = existingFarmer.copy(
                        farmerCode = farmerCode,
                        farmerName = farmerName,
                        mobileNumber = mobileNumber,
                        address = address,
                        updatedDate = currentDate
                    )
                    
                    repository.updateFarmer(updatedFarmer)
                    _operationResult.value = OperationResult.Success("Farmer updated successfully")
                } catch (e: Exception) {
                    _operationResult.value = OperationResult.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    fun deactivateFarmer(farmerId: Int) {
        viewModelScope.launch {
            try {
                repository.deactivateFarmer(farmerId)
                _operationResult.value = OperationResult.Success("Farmer deactivated successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error: ${e.message}")
            }
        }
    }
    
    fun deleteFarmer(farmer: FarmerModel) {
        viewModelScope.launch {
            try {
                repository.deleteFarmer(farmer)
                _operationResult.value = OperationResult.Success("Farmer deleted successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error: ${e.message}")
            }
        }
    }
    
    suspend fun getFarmerById(farmerId: Int): FarmerModel? {
        return repository.getFarmerById(farmerId)
    }
    
    private fun validateFarmerInput(farmerCode: String, farmerName: String, mobileNumber: String): Boolean {
        when {
            farmerCode.isBlank() -> {
                _operationResult.value = OperationResult.Error("Farmer code is required")
                return false
            }
            farmerName.isBlank() -> {
                _operationResult.value = OperationResult.Error("Farmer name is required")
                return false
            }
            mobileNumber.isBlank() -> {
                _operationResult.value = OperationResult.Error("Mobile number is required")
                return false
            }
            mobileNumber.length != 10 -> {
                _operationResult.value = OperationResult.Error("Mobile number must be 10 digits")
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