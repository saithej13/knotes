package com.vst.knotes.MVVM.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.MVVM.Repositories.MilkCollectionRepository
import com.vst.knotes.RoomDataBase.DataBase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: MilkCollectionRepository
    
    private val _reportData = MutableLiveData<List<MilkCollectionWithFarmerModel>>()
    val reportData: LiveData<List<MilkCollectionWithFarmerModel>> = _reportData
    
    private val _reportSummary = MutableLiveData<ReportSummary>()
    val reportSummary: LiveData<ReportSummary> = _reportSummary
    
    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult
    
    init {
        val database = DataBase.getInstance(application)
        repository = MilkCollectionRepository(database.milkCollectionDao())
    }
    
    fun generateDailyReport(date: String) {
        viewModelScope.launch {
            try {
                repository.getCollectionsByDate(date).observeForever { collections ->
                    _reportData.value = collections
                    calculateSummary(collections, "Daily Report - $date")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error generating daily report: ${e.message}")
            }
        }
    }
    
    fun generateWeeklyReport(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                repository.getCollectionsByDateRange(startDate, endDate).observeForever { collections ->
                    _reportData.value = collections
                    calculateSummary(collections, "Weekly Report - $startDate to $endDate")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error generating weekly report: ${e.message}")
            }
        }
    }
    
    fun generateMonthlyReport(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance()
                calendar.set(year, month - 1, 1)
                val startDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                
                repository.getCollectionsByDateRange(startDate, endDate).observeForever { collections ->
                    _reportData.value = collections
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
                    calculateSummary(collections, "Monthly Report - $monthName")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error generating monthly report: ${e.message}")
            }
        }
    }
    
    fun generateBillingCycleReport(farmerId: Int, year: Int, month: Int, cycle: BillingCycle) {
        viewModelScope.launch {
            try {
                val (startDay, endDay) = when (cycle) {
                    BillingCycle.FIRST_10 -> Pair(1, 10)
                    BillingCycle.SECOND_10 -> Pair(11, 20)
                    BillingCycle.THIRD_10_PLUS -> {
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month - 1, 1)
                        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        Pair(21, lastDay)
                    }
                }
                
                repository.getBillingCycleCollections(farmerId, year, month, startDay, endDay).observeForever { collections ->
                    _reportData.value = collections
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
                        Calendar.getInstance().apply { set(year, month - 1, 1) }.time
                    )
                    val farmerName = collections.firstOrNull()?.farmer?.farmerName ?: "Unknown"
                    calculateSummary(collections, "Billing Report - $farmerName - $monthName (${startDay}-${endDay})")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error generating billing report: ${e.message}")
            }
        }
    }
    
    fun generateFarmerReport(farmerId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                repository.getCollectionsByFarmer(farmerId).observeForever { allCollections ->
                    // Filter by date range
                    val filteredCollections = allCollections.filter { collection ->
                        isDateInRange(collection.collection.collectionDate, startDate, endDate)
                    }
                    
                    _reportData.value = filteredCollections
                    val farmerName = filteredCollections.firstOrNull()?.farmer?.farmerName ?: "Unknown"
                    calculateSummary(filteredCollections, "Farmer Report - $farmerName ($startDate to $endDate)")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error generating farmer report: ${e.message}")
            }
        }
    }
    
    fun generateCustomReport(startDate: String, endDate: String, farmerId: Int? = null) {
        viewModelScope.launch {
            try {
                if (farmerId != null) {
                    generateFarmerReport(farmerId, startDate, endDate)
                } else {
                    repository.getCollectionsByDateRange(startDate, endDate).observeForever { collections ->
                        _reportData.value = collections
                        calculateSummary(collections, "Custom Report - $startDate to $endDate")
                    }
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error generating custom report: ${e.message}")
            }
        }
    }
    
    private fun calculateSummary(collections: List<MilkCollectionWithFarmerModel>, title: String) {
        viewModelScope.launch {
            try {
                val summary = ReportSummary(
                    title = title,
                    totalCollections = collections.size,
                    totalQuantity = collections.sumOf { it.collection.quantity },
                    totalAmount = collections.sumOf { it.collection.amount },
                    uniqueFarmers = collections.distinctBy { it.farmer.farmerId }.size,
                    averageQuantityPerCollection = if (collections.isNotEmpty()) 
                        collections.sumOf { it.collection.quantity } / collections.size else 0.0,
                    averageAmountPerCollection = if (collections.isNotEmpty()) 
                        collections.sumOf { it.collection.amount } / collections.size else 0.0,
                    averageFat = if (collections.isNotEmpty()) 
                        collections.sumOf { it.collection.fat } / collections.size else 0.0,
                    averageSnf = if (collections.isNotEmpty()) 
                        collections.sumOf { it.collection.snf } / collections.size else 0.0,
                    cmCollections = collections.count { it.collection.rateType == "CM" },
                    bmCollections = collections.count { it.collection.rateType == "BM" },
                    dateRange = if (collections.isNotEmpty()) {
                        val dates = collections.map { it.collection.collectionDate }.sorted()
                        "${dates.first()} to ${dates.last()}"
                    } else "No data",
                    topFarmers = getTopFarmers(collections)
                )
                
                _reportSummary.value = summary
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error calculating summary: ${e.message}")
            }
        }
    }
    
    private fun getTopFarmers(collections: List<MilkCollectionWithFarmerModel>): List<FarmerSummary> {
        return collections.groupBy { it.farmer.farmerId }
            .map { (_, farmerCollections) ->
                FarmerSummary(
                    farmerCode = farmerCollections.first().farmer.farmerCode,
                    farmerName = farmerCollections.first().farmer.farmerName,
                    totalQuantity = farmerCollections.sumOf { it.collection.quantity },
                    totalAmount = farmerCollections.sumOf { it.collection.amount },
                    collectionsCount = farmerCollections.size
                )
            }
            .sortedByDescending { it.totalAmount }
            .take(10)
    }
    
    private fun isDateInRange(dateString: String, startDate: String, endDate: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)
            
            date != null && start != null && end != null &&
                    date.time >= start.time && date.time <= end.time
        } catch (e: Exception) {
            false
        }
    }
    
    enum class BillingCycle {
        FIRST_10,    // 1-10
        SECOND_10,   // 11-20  
        THIRD_10_PLUS // 21-30/31
    }
    
    data class ReportSummary(
        val title: String,
        val totalCollections: Int,
        val totalQuantity: Double,
        val totalAmount: Double,
        val uniqueFarmers: Int,
        val averageQuantityPerCollection: Double,
        val averageAmountPerCollection: Double,
        val averageFat: Double,
        val averageSnf: Double,
        val cmCollections: Int,
        val bmCollections: Int,
        val dateRange: String = "",
        val topFarmers: List<FarmerSummary> = emptyList()
    )
    
    data class FarmerSummary(
        val farmerCode: String,
        val farmerName: String,
        val totalQuantity: Double,
        val totalAmount: Double,
        val collectionsCount: Int
    )
    
    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }
}