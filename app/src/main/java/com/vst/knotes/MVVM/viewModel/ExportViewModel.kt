package com.vst.knotes.MVVM.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.utils.ExportUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ExportViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context = application.applicationContext
    
    private val _exportResult = MutableLiveData<ExportResult>()
    val exportResult: LiveData<ExportResult> = _exportResult
    
    private val _isExporting = MutableLiveData<Boolean>()
    val isExporting: LiveData<Boolean> = _isExporting
    
    fun exportToExcel(
        collections: List<MilkCollectionWithFarmerModel>,
        summary: ReportsViewModel.ReportSummary
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isExporting.postValue(true)
                
                if (!ExportUtils.isExternalStorageWritable()) {
                    _exportResult.postValue(ExportResult.Error("External storage not available"))
                    return@launch
                }
                
                ExportUtils.exportToExcel(
                    context = context,
                    collections = collections,
                    summary = summary,
                    onSuccess = { file ->
                        _exportResult.postValue(ExportResult.Success(file, ExportType.EXCEL))
                    },
                    onError = { error ->
                        _exportResult.postValue(ExportResult.Error(error))
                    }
                )
                
            } catch (e: Exception) {
                _exportResult.postValue(ExportResult.Error("Export failed: ${e.message}"))
            } finally {
                _isExporting.postValue(false)
            }
        }
    }
    
    fun exportToPDF(
        collections: List<MilkCollectionWithFarmerModel>,
        summary: ReportsViewModel.ReportSummary
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isExporting.postValue(true)
                
                if (!ExportUtils.isExternalStorageWritable()) {
                    _exportResult.postValue(ExportResult.Error("External storage not available"))
                    return@launch
                }
                
                ExportUtils.exportToPDF(
                    context = context,
                    collections = collections,
                    summary = summary,
                    onSuccess = { file ->
                        _exportResult.postValue(ExportResult.Success(file, ExportType.PDF))
                    },
                    onError = { error ->
                        _exportResult.postValue(ExportResult.Error(error))
                    }
                )
                
            } catch (e: Exception) {
                _exportResult.postValue(ExportResult.Error("Export failed: ${e.message}"))
            } finally {
                _isExporting.postValue(false)
            }
        }
    }
    
    fun shareFile(file: File) {
        ExportUtils.shareFile(context, file)
    }
    
    fun openFile(file: File) {
        ExportUtils.openFile(context, file)
    }
    
    sealed class ExportResult {
        data class Success(val file: File, val type: ExportType) : ExportResult()
        data class Error(val message: String) : ExportResult()
    }
    
    enum class ExportType {
        EXCEL, PDF
    }
}