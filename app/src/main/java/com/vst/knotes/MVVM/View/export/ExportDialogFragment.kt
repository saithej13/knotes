package com.vst.knotes.MVVM.View.export

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.ExportViewModel
import com.vst.knotes.MVVM.viewModel.ReportsViewModel
import com.vst.knotes.databinding.DialogExportBinding
import com.vst.knotes.utils.ExportUtils
import java.io.File

class ExportDialogFragment : DialogFragment() {
    
    private var _binding: DialogExportBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var exportViewModel: ExportViewModel
    
    private var collections: List<MilkCollectionWithFarmerModel> = emptyList()
    private var summary: ReportsViewModel.ReportSummary? = null
    
    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 100
        
        fun newInstance(
            collections: List<MilkCollectionWithFarmerModel>,
            summary: ReportsViewModel.ReportSummary
        ): ExportDialogFragment {
            val fragment = ExportDialogFragment()
            fragment.collections = collections
            fragment.summary = summary
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExportBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupClickListeners()
        observeData()
        updateUI()
    }
    
    private fun setupViewModel() {
        exportViewModel = ViewModelProvider(this)[ExportViewModel::class.java]
    }
    
    private fun setupClickListeners() {
        binding.buttonExportExcel.setOnClickListener {
            if (checkStoragePermission()) {
                exportToExcel()
            }
        }
        
        binding.buttonExportPdf.setOnClickListener {
            if (checkStoragePermission()) {
                exportToPDF()
            }
        }
        
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun observeData() {
        exportViewModel.isExporting.observe(this) { isExporting ->
            binding.progressBar.visibility = if (isExporting) View.VISIBLE else View.GONE
            binding.buttonExportExcel.isEnabled = !isExporting
            binding.buttonExportPdf.isEnabled = !isExporting
        }
        
        exportViewModel.exportResult.observe(this) { result ->
            when (result) {
                is ExportViewModel.ExportResult.Success -> {
                    showSuccessDialog(result.file, result.type)
                }
                is ExportViewModel.ExportResult.Error -> {
                    (activity as? BaseActivity)?.showCustomDialog(
                        title = "Export Error",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
            }
        }
    }
    
    private fun updateUI() {
        summary?.let { summary ->
            binding.textViewReportTitle.text = summary.title
            binding.textViewRecordCount.text = "Records: ${collections.size}"
            binding.textViewDateRange.text = summary.dateRange
            
            // Show file size estimates
            val estimatedSize = estimateFileSize(collections.size)
            binding.textViewExcelSize.text = "~${estimatedSize.first}"
            binding.textViewPdfSize.text = "~${estimatedSize.second}"
        }
    }
    
    private fun checkStoragePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
            false
        } else {
            true
        }
    }
    
    private fun exportToExcel() {
        summary?.let { summary ->
            exportViewModel.exportToExcel(collections, summary)
        }
    }
    
    private fun exportToPDF() {
        summary?.let { summary ->
            exportViewModel.exportToPDF(collections, summary)
        }
    }
    
    private fun showSuccessDialog(file: File, type: ExportViewModel.ExportType) {
        val typeString = when (type) {
            ExportViewModel.ExportType.EXCEL -> "Excel"
            ExportViewModel.ExportType.PDF -> "PDF"
        }
        
        val fileSize = ExportUtils.getFileSize(file)
        
        (activity as? BaseActivity)?.showCustomDialog(
            title = "Export Successful",
            message = "$typeString file exported successfully!\n\nFile: ${file.name}\nSize: $fileSize\n\nWhat would you like to do?",
            yesButtonText = "Share",
            noButtonText = "Open",
            onYesClick = {
                exportViewModel.shareFile(file)
                dismiss()
            },
            onNoClick = {
                exportViewModel.openFile(file)
                dismiss()
            }
        )
    }
    
    private fun estimateFileSize(recordCount: Int): Pair<String, String> {
        // Rough estimates based on record count
        val excelSizeKB = (recordCount * 0.5 + 10).toInt() // ~0.5KB per record + 10KB overhead
        val pdfSizeKB = (recordCount * 1.0 + 15).toInt() // ~1.0KB per record + 15KB overhead
        
        val excelSize = if (excelSizeKB > 1024) {
            "${String.format("%.1f", excelSizeKB / 1024.0)} MB"
        } else {
            "$excelSizeKB KB"
        }
        
        val pdfSize = if (pdfSizeKB > 1024) {
            "${String.format("%.1f", pdfSizeKB / 1024.0)} MB"
        } else {
            "$pdfSizeKB KB"
        }
        
        return Pair(excelSize, pdfSize)
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}