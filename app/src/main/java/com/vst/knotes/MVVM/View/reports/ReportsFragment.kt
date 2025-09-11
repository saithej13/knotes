package com.vst.knotes.MVVM.View.reports

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.collection.MilkCollectionAdapter
import com.vst.knotes.MVVM.View.export.ExportDialogFragment
import com.vst.knotes.MVVM.viewModel.FarmerViewModel
import com.vst.knotes.MVVM.viewModel.ReportsViewModel
import com.vst.knotes.databinding.FragmentReportsBinding
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {
    
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var reportsViewModel: ReportsViewModel
    private lateinit var farmerViewModel: FarmerViewModel
    private lateinit var collectionAdapter: MilkCollectionAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModels()
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        observeData()
    }
    
    private fun setupViewModels() {
        reportsViewModel = ViewModelProvider(this)[ReportsViewModel::class.java]
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
    }
    
    private fun setupUI() {
        // Setup report type spinner
        val reportTypes = listOf(
            "Daily Report",
            "Weekly Report", 
            "Monthly Report",
            "Billing Cycle Report",
            "Farmer Report",
            "Custom Report"
        )
        val reportAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, reportTypes)
        reportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReportType.adapter = reportAdapter
        
        // Setup billing cycle spinner
        val billingCycles = listOf("1-10", "11-20", "21-30/31")
        val cycleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, billingCycles)
        cycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBillingCycle.adapter = cycleAdapter
        
        // Setup month spinner
        val months = (1..12).map { month ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, month - 1)
            SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time) + " ($month)"
        }
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = monthAdapter
        
        // Setup year spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 2..currentYear + 1).toList()
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearAdapter
        binding.spinnerYear.setSelection(2) // Select current year
        
        // Set default dates
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = Date()
        binding.editTextStartDate.setText(dateFormat.format(today))
        binding.editTextEndDate.setText(dateFormat.format(today))
        binding.editTextReportDate.setText(dateFormat.format(today))
        
        // Show/hide relevant fields based on report type
        updateFieldVisibility()
    }
    
    private fun setupRecyclerView() {
        collectionAdapter = MilkCollectionAdapter(
            onEditClick = { /* Navigate to edit */ },
            onDeleteClick = { /* Handle delete */ },
            onViewDetailsClick = { /* View details */ }
        )
        
        binding.recyclerViewReportData.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collectionAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.spinnerReportType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateFieldVisibility()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
        
        binding.editTextStartDate.setOnClickListener {
            showDatePicker { date ->
                binding.editTextStartDate.setText(date)
            }
        }
        
        binding.editTextEndDate.setOnClickListener {
            showDatePicker { date ->
                binding.editTextEndDate.setText(date)
            }
        }
        
        binding.editTextReportDate.setOnClickListener {
            showDatePicker { date ->
                binding.editTextReportDate.setText(date)
            }
        }
        
        binding.buttonGenerateReport.setOnClickListener {
            generateReport()
        }
        
        binding.buttonExportReport.setOnClickListener {
            exportReport()
        }
        
        binding.buttonPrintReport.setOnClickListener {
            printReport()
        }
    }
    
    private fun observeData() {
        // Observe farmers for farmer selection
        farmerViewModel.allActiveFarmers.observe(viewLifecycleOwner) { farmers ->
            val farmerNames = listOf("All Farmers") + farmers.map { "${it.farmerCode} - ${it.farmerName}" }
            val farmerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, farmerNames)
            farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFarmer.adapter = farmerAdapter
        }
        
        // Observe report data
        reportsViewModel.reportData.observe(viewLifecycleOwner) { collections ->
            collectionAdapter.submitList(collections)
            binding.textViewRecordCount.text = "Records: ${collections.size}"
            binding.layoutReportResults.visibility = if (collections.isEmpty()) View.GONE else View.VISIBLE
        }
        
        // Observe report summary
        reportsViewModel.reportSummary.observe(viewLifecycleOwner) { summary ->
            displayReportSummary(summary)
        }
        
        // Observe operation results
        reportsViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.buttonGenerateReport.isEnabled = true
            
            when (result) {
                is ReportsViewModel.OperationResult.Success -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Success",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
                is ReportsViewModel.OperationResult.Error -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Error",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
            }
        }
    }
    
    private fun updateFieldVisibility() {
        val reportType = binding.spinnerReportType.selectedItem.toString()
        
        // Hide all fields first
        binding.layoutDateRange.visibility = View.GONE
        binding.layoutSingleDate.visibility = View.GONE
        binding.layoutMonthYear.visibility = View.GONE
        binding.layoutBillingCycle.visibility = View.GONE
        binding.layoutFarmerSelection.visibility = View.GONE
        
        // Show relevant fields based on report type
        when (reportType) {
            "Daily Report" -> {
                binding.layoutSingleDate.visibility = View.VISIBLE
            }
            "Weekly Report", "Custom Report" -> {
                binding.layoutDateRange.visibility = View.VISIBLE
                if (reportType == "Custom Report") {
                    binding.layoutFarmerSelection.visibility = View.VISIBLE
                }
            }
            "Monthly Report" -> {
                binding.layoutMonthYear.visibility = View.VISIBLE
            }
            "Billing Cycle Report" -> {
                binding.layoutMonthYear.visibility = View.VISIBLE
                binding.layoutBillingCycle.visibility = View.VISIBLE
                binding.layoutFarmerSelection.visibility = View.VISIBLE
            }
            "Farmer Report" -> {
                binding.layoutDateRange.visibility = View.VISIBLE
                binding.layoutFarmerSelection.visibility = View.VISIBLE
            }
        }
    }
    
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                onDateSelected(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    private fun generateReport() {
        val reportType = binding.spinnerReportType.selectedItem.toString()
        
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonGenerateReport.isEnabled = false
        
        when (reportType) {
            "Daily Report" -> {
                val date = binding.editTextReportDate.text.toString()
                reportsViewModel.generateDailyReport(date)
            }
            "Weekly Report" -> {
                val startDate = binding.editTextStartDate.text.toString()
                val endDate = binding.editTextEndDate.text.toString()
                reportsViewModel.generateWeeklyReport(startDate, endDate)
            }
            "Monthly Report" -> {
                val year = binding.spinnerYear.selectedItem.toString().toInt()
                val month = binding.spinnerMonth.selectedItemPosition + 1
                reportsViewModel.generateMonthlyReport(year, month)
            }
            "Billing Cycle Report" -> {
                val farmerIndex = binding.spinnerFarmer.selectedItemPosition
                if (farmerIndex > 0) { // Skip "All Farmers" option
                    // Get farmer ID from the list
                    farmerViewModel.allActiveFarmers.value?.let { farmers ->
                        val farmerId = farmers[farmerIndex - 1].farmerId
                        val year = binding.spinnerYear.selectedItem.toString().toInt()
                        val month = binding.spinnerMonth.selectedItemPosition + 1
                        val cycleIndex = binding.spinnerBillingCycle.selectedItemPosition
                        
                        val cycle = when (cycleIndex) {
                            0 -> ReportsViewModel.BillingCycle.FIRST_10
                            1 -> ReportsViewModel.BillingCycle.SECOND_10
                            2 -> ReportsViewModel.BillingCycle.THIRD_10_PLUS
                            else -> ReportsViewModel.BillingCycle.FIRST_10
                        }
                        
                        reportsViewModel.generateBillingCycleReport(farmerId, year, month, cycle)
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGenerateReport.isEnabled = true
                    (activity as BaseActivity).showCustomDialog(
                        title = "Error",
                        message = "Please select a farmer for billing cycle report",
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
            }
            "Farmer Report" -> {
                val farmerIndex = binding.spinnerFarmer.selectedItemPosition
                if (farmerIndex > 0) { // Skip "All Farmers" option
                    farmerViewModel.allActiveFarmers.value?.let { farmers ->
                        val farmerId = farmers[farmerIndex - 1].farmerId
                        val startDate = binding.editTextStartDate.text.toString()
                        val endDate = binding.editTextEndDate.text.toString()
                        reportsViewModel.generateFarmerReport(farmerId, startDate, endDate)
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGenerateReport.isEnabled = true
                    (activity as BaseActivity).showCustomDialog(
                        title = "Error",
                        message = "Please select a farmer for farmer report",
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
            }
            "Custom Report" -> {
                val startDate = binding.editTextStartDate.text.toString()
                val endDate = binding.editTextEndDate.text.toString()
                val farmerIndex = binding.spinnerFarmer.selectedItemPosition
                
                val farmerId = if (farmerIndex > 0) {
                    farmerViewModel.allActiveFarmers.value?.get(farmerIndex - 1)?.farmerId
                } else null
                
                reportsViewModel.generateCustomReport(startDate, endDate, farmerId)
            }
        }
    }
    
    private fun displayReportSummary(summary: ReportsViewModel.ReportSummary) {
        binding.apply {
            textViewReportTitle.text = summary.title
            textViewTotalCollections.text = summary.totalCollections.toString()
            textViewTotalQuantity.text = String.format("%.2f L", summary.totalQuantity)
            textViewTotalAmount.text = String.format("₹%.2f", summary.totalAmount)
            textViewUniqueFarmers.text = summary.uniqueFarmers.toString()
            textViewAverageFat.text = String.format("%.2f%%", summary.averageFat)
            textViewAverageSnf.text = String.format("%.2f%%", summary.averageSnf)
            textViewCmCollections.text = summary.cmCollections.toString()
            textViewBmCollections.text = summary.bmCollections.toString()
            
            layoutReportSummary.visibility = View.VISIBLE
        }
    }
    
    private fun exportReport() {
        val currentCollections = reportsViewModel.reportData.value ?: emptyList()
        val currentSummary = reportsViewModel.reportSummary.value
        
        if (currentCollections.isEmpty()) {
            (activity as BaseActivity).showCustomDialog(
                title = "No Data",
                message = "Please generate a report first before exporting",
                yesButtonText = "OK",
                noButtonText = null
            )
            return
        }
        
        if (currentSummary != null) {
            val exportDialog = ExportDialogFragment.newInstance(currentCollections, currentSummary)
            exportDialog.show(parentFragmentManager, "ExportDialog")
        } else {
            (activity as BaseActivity).showCustomDialog(
                title = "Error",
                message = "Report summary not available",
                yesButtonText = "OK",
                noButtonText = null
            )
        }
    }
    
    private fun printReport() {
        (activity as BaseActivity).showCustomDialog(
            title = "Print Report",
            message = "Print functionality will be implemented in the next version",
            yesButtonText = "OK",
            noButtonText = null
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}