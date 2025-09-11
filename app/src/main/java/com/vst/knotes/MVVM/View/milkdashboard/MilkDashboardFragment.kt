package com.vst.knotes.MVVM.View.milkdashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.collection.AddEditCollectionFragment
import com.vst.knotes.MVVM.View.collection.MilkCollectionFragment
import com.vst.knotes.MVVM.View.farmer.FarmerFragment
import com.vst.knotes.MVVM.View.rates.RatesFragment
import com.vst.knotes.MVVM.View.reports.ReportsFragment
import com.vst.knotes.MVVM.viewModel.FarmerViewModel
import com.vst.knotes.MVVM.viewModel.MilkCollectionViewModel
import com.vst.knotes.MVVM.viewModel.RateViewModel
import com.vst.knotes.databinding.FragmentMilkDashboardBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MilkDashboardFragment : Fragment() {
    
    private var _binding: FragmentMilkDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var farmerViewModel: FarmerViewModel
    private lateinit var milkCollectionViewModel: MilkCollectionViewModel
    private lateinit var rateViewModel: RateViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilkDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModels()
        setupClickListeners()
        observeData()
        updateCurrentDateTime()
    }
    
    private fun setupViewModels() {
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        milkCollectionViewModel = ViewModelProvider(this)[MilkCollectionViewModel::class.java]
        rateViewModel = ViewModelProvider(this)[RateViewModel::class.java]
    }
    
    private fun setupClickListeners() {
        // Quick Actions
        binding.cardQuickCollection.setOnClickListener {
            val fragment = AddEditCollectionFragment.newInstance()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.cardAddFarmer.setOnClickListener {
            val fragment = com.vst.knotes.MVVM.View.farmer.AddEditFarmerFragment.newInstance()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.cardConfigureRates.setOnClickListener {
            val fragment = RatesFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.cardViewReports.setOnClickListener {
            val fragment = ReportsFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        // Main Menu Items
        binding.cardFarmers.setOnClickListener {
            val fragment = FarmerFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.cardCollections.setOnClickListener {
            val fragment = MilkCollectionFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.cardRates.setOnClickListener {
            val fragment = RatesFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.cardReports.setOnClickListener {
            val fragment = ReportsFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        // Today's Summary Click
        binding.cardTodaysSummary.setOnClickListener {
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            // Could navigate to today's report
            (activity as BaseActivity).showCustomDialog(
                title = "Today's Summary",
                message = "View detailed daily report for $today?",
                yesButtonText = "View Report",
                noButtonText = "Cancel",
                onYesClick = {
                    val fragment = ReportsFragment()
                    (activity as BaseActivity).replaceFragment(fragment)
                }
            )
        }
    }
    
    private fun observeData() {
        // Observe farmers count
        farmerViewModel.allActiveFarmers.observe(viewLifecycleOwner) { farmers ->
            binding.textViewActiveFarmers.text = farmers.size.toString()
        }
        
        // Observe total farmers count
        farmerViewModel.allFarmers.observe(viewLifecycleOwner) { farmers ->
            binding.textViewTotalFarmers.text = farmers.size.toString()
        }
        
        // Observe today's collections
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        milkCollectionViewModel.getCollectionsByDate(today).observe(viewLifecycleOwner) { collections ->
            binding.textViewTodaysCollections.text = collections.size.toString()
            binding.textViewTodaysQuantity.text = String.format("%.1f L", collections.sumOf { it.collection.quantity })
            binding.textViewTodaysAmount.text = String.format("₹%.2f", collections.sumOf { it.collection.amount })
            
            // Update farmers who collected today
            binding.textViewTodaysFarmers.text = collections.distinctBy { it.farmer.farmerId }.size.toString()
        }
        
        // Observe active rates
        rateViewModel.allActiveRates.observe(viewLifecycleOwner) { rates ->
            val cmRate = rates.find { it.rateType == "CM" }
            val bmRate = rates.find { it.rateType == "BM" }
            
            binding.textViewCmRateStatus.text = if (cmRate != null) "Active" else "Not Set"
            binding.textViewBmRateStatus.text = if (bmRate != null) "Active" else "Not Set"
            
            binding.textViewCmRateStatus.setTextColor(
                requireContext().getColor(
                    if (cmRate != null) android.R.color.holo_green_dark 
                    else android.R.color.holo_red_dark
                )
            )
            binding.textViewBmRateStatus.setTextColor(
                requireContext().getColor(
                    if (bmRate != null) android.R.color.holo_green_dark 
                    else android.R.color.holo_red_dark
                )
            )
        }
        
        // Load additional statistics
        loadWeeklyStats()
        loadMonthlyStats()
    }
    
    private fun loadWeeklyStats() {
        lifecycleScope.launch {
            try {
                val calendar = Calendar.getInstance()
                
                // Get start of current week (Monday)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val startOfWeek = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                
                // Get end of current week (Sunday)
                calendar.add(Calendar.DAY_OF_WEEK, 6)
                val endOfWeek = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                
                milkCollectionViewModel.getCollectionsByDateRange(startOfWeek, endOfWeek).observe(viewLifecycleOwner) { collections ->
                    binding.textViewWeeklyCollections.text = collections.size.toString()
                    binding.textViewWeeklyQuantity.text = String.format("%.1f L", collections.sumOf { it.collection.quantity })
                    binding.textViewWeeklyAmount.text = String.format("₹%.2f", collections.sumOf { it.collection.amount })
                }
            } catch (e: Exception) {
                // Handle error
                binding.textViewWeeklyCollections.text = "0"
                binding.textViewWeeklyQuantity.text = "0.0 L"
                binding.textViewWeeklyAmount.text = "₹0.00"
            }
        }
    }
    
    private fun loadMonthlyStats() {
        lifecycleScope.launch {
            try {
                val calendar = Calendar.getInstance()
                
                // Get start of current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                
                // Get end of current month
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endOfMonth = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                
                milkCollectionViewModel.getCollectionsByDateRange(startOfMonth, endOfMonth).observe(viewLifecycleOwner) { collections ->
                    binding.textViewMonthlyCollections.text = collections.size.toString()
                    binding.textViewMonthlyQuantity.text = String.format("%.1f L", collections.sumOf { it.collection.quantity })
                    binding.textViewMonthlyAmount.text = String.format("₹%.2f", collections.sumOf { it.collection.amount })
                }
            } catch (e: Exception) {
                // Handle error
                binding.textViewMonthlyCollections.text = "0"
                binding.textViewMonthlyQuantity.text = "0.0 L"
                binding.textViewMonthlyAmount.text = "₹0.00"
            }
        }
    }
    
    private fun updateCurrentDateTime() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Date()
        
        binding.textViewCurrentDate.text = dateFormat.format(now)
        binding.textViewCurrentTime.text = timeFormat.format(now)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}