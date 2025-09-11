package com.vst.knotes.MVVM.View.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vst.knotes.MVVM.viewModel.RateViewModel
import com.vst.knotes.databinding.FragmentTestRateBinding
import com.vst.knotes.utils.MilkPriceCalculator
import kotlinx.coroutines.launch

class TestRateFragment : Fragment() {
    
    private var _binding: FragmentTestRateBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var rateViewModel: RateViewModel
    private var rateId: Int = -1
    
    companion object {
        private const val ARG_RATE_ID = "rate_id"
        
        fun newInstance(rateId: Int): TestRateFragment {
            val fragment = TestRateFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_RATE_ID, rateId)
            fragment.arguments = bundle
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestRateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        parseArguments()
        loadRateData()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        rateViewModel = ViewModelProvider(this)[RateViewModel::class.java]
    }
    
    private fun parseArguments() {
        arguments?.let {
            rateId = it.getInt(ARG_RATE_ID, -1)
        }
    }
    
    private fun loadRateData() {
        lifecycleScope.launch {
            val rate = rateViewModel.getRateById(rateId)
            rate?.let { rateModel ->
                binding.textViewRateType.text = "Rate Type: ${rateModel.rateType}"
                binding.textViewEffectiveDate.text = "Effective From: ${rate.startDate} To  ${rate.endDate}"
                
                when (rateModel.rateType) {
                    MilkPriceCalculator.RATE_TYPE_CM -> {
                        binding.textViewRateDetails.text = 
                            "Fat Min : ${rateModel.minFat} - Fat Max : ${rateModel.maxFat} \n SNF Min: ${rateModel.minSnf} - SNF Max: ${rateModel.maxSnf}"
                        binding.textViewFormula.text = 
                            "Formula: inprogress"//(Fat × ${rateModel.fatRate}) + (SNF × ${rateModel.snfRate})
                    }
                    MilkPriceCalculator.RATE_TYPE_BM -> {
                        binding.textViewRateDetails.text = 
                            "Base Fat Rate: ₹${rateModel.baseFatRate}/% | Max SNF: ${rateModel.maxSnf}% | Penalty: ₹${rateModel.snfPenalty}/%"
                        binding.textViewFormula.text = 
                            "Formula: (Fat × ${rateModel.baseFatRate}) - (${rateModel.maxSnf} - SNF) × ${rateModel.snfPenalty}"
                    }
                }
                
                performSampleCalculations(rateModel)
            }
        }
    }
    
    private fun performSampleCalculations(rate: com.vst.knotes.MVVM.Model.RateModel) {
        val sampleData = listOf(
            Triple(1.0, 3.5, 8.0),
            Triple(1.0, 4.0, 8.5),
            Triple(1.0, 4.5, 9.0),
            Triple(2.0, 4.0, 8.5),
            Triple(5.0, 4.2, 8.7)
        )
        
        val results = StringBuilder()
        results.append("Sample Calculations:\n\n")
        
        sampleData.forEach { (quantity, fat, snf) ->
            val price = MilkPriceCalculator.calculatePrice(quantity, fat, snf, rate)
            val pricePerLiter = price / quantity
            results.append("Qty: ${quantity}L, Fat: ${fat}%, SNF: ${snf}%\n")
            results.append("Amount: ₹${String.format("%.2f", price)} (₹${String.format("%.2f")}/L)\n\n")
        }
        
        binding.textViewSampleResults.text = results.toString()
    }
    
    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}