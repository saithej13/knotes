package com.vst.knotes.MVVM.View.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.viewModel.RateViewModel
import com.vst.knotes.databinding.FragmentRateCalculatorBinding
import com.vst.knotes.utils.MilkPriceCalculator

class RateCalculatorFragment : Fragment() {
    
    private var _binding: FragmentRateCalculatorBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var rateViewModel: RateViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRateCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupUI()
        setupClickListeners()
        observeData()
    }
    
    private fun setupViewModel() {
        rateViewModel = ViewModelProvider(this)[RateViewModel::class.java]
    }
    
    private fun setupUI() {
        // Setup rate type spinner
        val rateTypes = listOf(MilkPriceCalculator.RATE_TYPE_CM, MilkPriceCalculator.RATE_TYPE_BM)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rateTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRateType.adapter = adapter
        
        // Setup SNF spinner
        val snfOptions = MilkPriceCalculator.getSnfOptions()
        val snfAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, snfOptions)
        snfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.editTextSnf.adapter = snfAdapter
        
        // Set default values
        binding.editTextQuantity.setText("1.0")
        binding.editTextFat.setText("4.0")
//        binding.spinnerSnf.setSelection(snfOptions.indexOf(8.5)) // Default SNF 8.5
    }
    
    private fun setupClickListeners() {
        binding.buttonCalculate.setOnClickListener {
            calculatePrice()
        }
        
//        binding.buttonClear.setOnClickListener {
//            clearFields()
//        }
    }
    
    private fun observeData() {
        rateViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RateViewModel.OperationResult.PriceCalculated -> {
                    binding.textViewResult.text = "Total Amount: ₹${String.format("%.2f", result.price)}"
                    binding.textViewResult.visibility = View.VISIBLE
                    
                    // Show per liter rate
                    val quantity = binding.editTextQuantity.text.toString().toDoubleOrNull() ?: 1.0
                    val perLiterRate = result.price / quantity
                    binding.textViewPerLiterRate.text = "Rate per Liter: ₹${String.format("%.2f", perLiterRate)}"
                    binding.textViewPerLiterRate.visibility = View.VISIBLE
                }
                is RateViewModel.OperationResult.Error -> {
                    binding.textViewResult.text = "Error: ${result.message}"
                    binding.textViewResult.visibility = View.VISIBLE
                    binding.textViewPerLiterRate.visibility = View.GONE
                }
                else -> {}
            }
        }
    }
    
    private fun calculatePrice() {
        val rateType = binding.spinnerRateType.selectedItem.toString()
        val quantityInput = binding.editTextQuantity.text.toString().toDoubleOrNull()
        val fatInput = binding.editTextFat.text.toString().toDoubleOrNull()
        val snfInput = binding.editTextSnf.text.toString().toDoubleOrNull()

        when {
            quantityInput == null || quantityInput <= 0 -> {
                binding.textViewResult.text = "Error: Invalid quantity"
                binding.textViewResult.visibility = View.VISIBLE
                return
            }
            fatInput == null || fatInput <= 0 -> {
                binding.textViewResult.text = "Error: Invalid fat percentage"
                binding.textViewResult.visibility = View.VISIBLE
                return
            }
            snfInput == null || !MilkPriceCalculator.isValidSnfRange(snfInput) -> {
                binding.textViewResult.text = "Error: Invalid SNF percentage"
                binding.textViewResult.visibility = View.VISIBLE
                return
            }
        }

        val quantity = quantityInput
        val fat = fatInput
        val snf = snfInput

        if (snf != null && fat != null && quantity != null) {
            rateViewModel.calculateSamplePrice(rateType, fat, snf, quantity)
        }

    }
    
    private fun clearFields() {
        binding.editTextQuantity.setText("1.0")
        binding.editTextFat.setText("4.0")
        binding.editTextSnf.setSelection(5) // Reset to 8.5
        binding.textViewResult.visibility = View.GONE
        binding.textViewPerLiterRate.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}