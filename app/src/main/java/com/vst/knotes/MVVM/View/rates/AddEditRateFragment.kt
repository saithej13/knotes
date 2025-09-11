package com.vst.knotes.MVVM.View.rates

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vst.knotes.MVVM.Model.RateModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.RateViewModel
import com.vst.knotes.databinding.FragmentAddEditRateBinding
import com.vst.knotes.utils.MilkPriceCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditRateFragment : Fragment() {
    
    private var _binding: FragmentAddEditRateBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var rateViewModel: RateViewModel
    private var rateId: Int = -1
    private var rateType: String = ""
    private var isEditMode: Boolean = false
    
    companion object {
        private const val ARG_RATE_ID = "rate_id"
        private const val ARG_RATE_TYPE = "rate_type"
        
        fun newInstance(rateId: Int? = null, rateType: String? = null): AddEditRateFragment {
            val fragment = AddEditRateFragment()
            val bundle = Bundle()
            rateId?.let { bundle.putInt(ARG_RATE_ID, it) }
            rateType?.let { bundle.putString(ARG_RATE_TYPE, it) }
            fragment.arguments = bundle
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditRateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        parseArguments()
        setupUI()
        setupClickListeners()
        observeData()
        
        if (isEditMode) {
            loadRateData()
        }
    }
    
    private fun setupViewModel() {
        rateViewModel = ViewModelProvider(this)[RateViewModel::class.java]
    }
    
    private fun parseArguments() {
        arguments?.let {
            if (it.containsKey(ARG_RATE_ID)) {
                rateId = it.getInt(ARG_RATE_ID)
                isEditMode = true
            }
            if (it.containsKey(ARG_RATE_TYPE)) {
                rateType = it.getString(ARG_RATE_TYPE) ?: ""
            }
        }
    }
    
    private fun setupUI() {
        // Setup rate type spinner
        val rateTypes = listOf(MilkPriceCalculator.RATE_TYPE_CM, MilkPriceCalculator.RATE_TYPE_BM)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rateTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRateType.adapter = adapter
        
        // Setup SNF spinner for BM type
//        val snfOptions = MilkPriceCalculator.getSnfOptions()
//        val snfAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, snfOptions)
//        snfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerMaxSnf.adapter = snfAdapter
        
        // Set default values
        if (!isEditMode && rateType.isNotEmpty()) {
            val position = rateTypes.indexOf(rateType)
            if (position != -1) {
                binding.spinnerRateType.setSelection(position)
            }
        }
        
        binding.textViewTitle.text = if (isEditMode) "Edit Rate" else "Add Rate"
        binding.buttonSave.text = if (isEditMode) "Update" else "Save"
        
        // Set current date as default
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.editTextStartDate.setText(dateFormat.format(Date()))
        binding.editTextEndDate.setText(dateFormat.format(Date()))
        
        updateFieldsVisibility()
    }
    
    private fun setupClickListeners() {
        binding.spinnerRateType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateFieldsVisibility()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
        
        binding.editTextStartDate.setOnClickListener {
            showDatePicker { date -> binding.editTextStartDate.setText(date) }
        }
        binding.editTextEndDate.setOnClickListener {
            showDatePicker { date -> binding.editTextEndDate.setText(date) }
        }
        
        binding.buttonSave.setOnClickListener {
            saveRate()
        }
        
        binding.buttonCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        
        binding.buttonTest.setOnClickListener {
            testRate()
        }
    }
    
    private fun observeData() {
        rateViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.buttonSave.isEnabled = true
            
            when (result) {
                is RateViewModel.OperationResult.Success -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Success",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null,
                        onYesClick = {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    )
                }
                is RateViewModel.OperationResult.Error -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Error",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
                is RateViewModel.OperationResult.PriceCalculated -> {
                    binding.textViewTestResult.text = "Sample Price: ₹${String.format("%.2f", result.price)} per liter"
                    binding.textViewTestResult.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun loadRateData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val rate = rateViewModel.getRateById(rateId)
            binding.progressBar.visibility = View.GONE
            rate?.let {
                val rateTypes = listOf(MilkPriceCalculator.RATE_TYPE_CM, MilkPriceCalculator.RATE_TYPE_BM)
                val position = rateTypes.indexOf(it.rateType)
                if (position != -1) {
                    binding.spinnerRateType.setSelection(position)
                }
                binding.editTextStartDate.setText(it.startDate)
                binding.editTextEndDate.setText(it.endDate)
                if (it.rateType == MilkPriceCalculator.RATE_TYPE_CM) {
                    binding.editTextMinFat.setText(it.minFat?.toString() ?: "")
                    binding.editTextMaxFat.setText(it.maxFat?.toString() ?: "")
                    binding.editTextMinSnf.setText(it.minSnf?.toString() ?: "")
                    binding.editTextMaxSnf.setText(it.maxSnf?.toString() ?: "")
                    binding.editTextRate.setText(it.rate?.toString() ?: "")
                } else if (it.rateType == MilkPriceCalculator.RATE_TYPE_BM) {
                    binding.editTextMinFatBM.setText(it.minFat?.toString() ?: "")
                    binding.editTextMaxFatBM.setText(it.maxFat?.toString() ?: "")
                    binding.editTextMinSnfBM.setText(it.minSnf?.toString() ?: "")
                    binding.editTextMaxSnfBM.setText(it.maxSnf?.toString() ?: "")
                    binding.editTextRateBM.setText(it.rate?.toString() ?: "")
                }
            }
        }
    }
    
    private fun updateFieldsVisibility() {
        val selectedType = binding.spinnerRateType.selectedItem.toString()
        
        when (selectedType) {
            MilkPriceCalculator.RATE_TYPE_CM -> {
                binding.layoutCMFields.visibility = View.VISIBLE
                binding.layoutBMFields.visibility = View.GONE
            }
            MilkPriceCalculator.RATE_TYPE_BM -> {
                binding.layoutCMFields.visibility = View.GONE
                binding.layoutBMFields.visibility = View.VISIBLE
            }
        }
    }
    
    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                onDateSet(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    private fun saveRate() {
        val selectedType = binding.spinnerRateType.selectedItem.toString()
        val startDate = binding.editTextStartDate.text.toString().trim()
        val endDate = binding.editTextEndDate.text.toString().trim()
        if (selectedType == MilkPriceCalculator.RATE_TYPE_CM) {
            val minFat = binding.editTextMinFat.text.toString().toDoubleOrNull()
            val maxFat = binding.editTextMaxFat.text.toString().toDoubleOrNull()
            val minSnf = binding.editTextMinSnf.text.toString().toDoubleOrNull()
            val maxSnf = binding.editTextMaxSnf.text.toString().toDoubleOrNull()
            val rate = binding.editTextRate.text.toString().toDoubleOrNull() ?: 0.0
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonSave.isEnabled = false
            rateViewModel.insertCMRate(rate, startDate, endDate, minFat, maxFat, minSnf, maxSnf)
        } else if (selectedType == MilkPriceCalculator.RATE_TYPE_BM) {
            val minFat = binding.editTextMinFatBM.text.toString().toDoubleOrNull()
            val maxFat = binding.editTextMaxFatBM.text.toString().toDoubleOrNull()
            val minSnf = binding.editTextMinSnfBM.text.toString().toDoubleOrNull()
            val maxSnf = binding.editTextMaxSnfBM.text.toString().toDoubleOrNull()
            val rate = binding.editTextRateBM.text.toString().toDoubleOrNull() ?: 0.0
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonSave.isEnabled = false
            rateViewModel.insertBMRate(rate, startDate, endDate, minFat, maxFat, minSnf, maxSnf)
        }
    }
    
    private fun testRate() {
        val selectedType = binding.spinnerRateType.selectedItem.toString()
        val fat = 4.0 // Sample fat value
        val snf = 8.5 // Sample SNF value
        
        rateViewModel.calculateSamplePrice(selectedType, fat, snf)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}