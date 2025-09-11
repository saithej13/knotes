package com.vst.knotes.MVVM.View.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.RateViewModel
import com.vst.knotes.databinding.FragmentRatesBinding
import com.vst.knotes.utils.MilkPriceCalculator

class RatesFragment : Fragment() {
    
    private var _binding: FragmentRatesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var rateViewModel: RateViewModel
    private lateinit var rateAdapter: RateAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeData()
    }
    
    private fun setupViewModel() {
        rateViewModel = ViewModelProvider(this)[RateViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        rateAdapter = RateAdapter(
            onEditClick = { rate ->
                val fragment = AddEditRateFragment.newInstance(rate.rateId)
                (activity as BaseActivity).replaceFragment(fragment)
            },
            onDeactivateClick = { rate ->
                (activity as BaseActivity).showCustomDialog(
                    title = "Deactivate Rate",
                    message = "Are you sure you want to deactivate this ${rate.rateType} rate?",
                    yesButtonText = "Deactivate",
                    noButtonText = "Cancel",
                    onYesClick = {
                        rateViewModel.deactivateRate(rate)
                    }
                )
            },
            onTestClick = { rate ->
                val fragment = TestRateFragment.newInstance(rate.rateId)
                (activity as BaseActivity).replaceFragment(fragment)
            }
        )
        
        binding.recyclerViewRates.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rateAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonAddCMRate.setOnClickListener {
            val fragment = AddEditRateFragment.newInstance(rateType = MilkPriceCalculator.RATE_TYPE_CM)
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.buttonAddBMRate.setOnClickListener {
            val fragment = AddEditRateFragment.newInstance(rateType = MilkPriceCalculator.RATE_TYPE_BM)
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.buttonRateCalculator.setOnClickListener {
            val fragment = RateCalculatorFragment()
            (activity as BaseActivity).replaceFragment(fragment)
        }
    }
    
    private fun observeData() {
        rateViewModel.allActiveRates.observe(viewLifecycleOwner) { rates ->
            rateAdapter.submitList(rates)
            binding.textViewEmptyState.visibility = if (rates.isEmpty()) View.VISIBLE else View.GONE
            
            // Update rate summaries
            updateRateSummaries(rates)
        }
        
        rateViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RateViewModel.OperationResult.Success -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Success",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
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
                else -> {}
            }
        }
    }
    
    private fun updateRateSummaries(rates: List<com.vst.knotes.MVVM.Model.RateModel>) {
        val cmRate = rates.find { it.rateType == MilkPriceCalculator.RATE_TYPE_CM }
        val bmRate = rates.find { it.rateType == MilkPriceCalculator.RATE_TYPE_BM }
        
        // Update CM rate summary
        if (cmRate != null) {
            binding.textViewCMRateSummary.text = "Min Fat: ${cmRate.minFat} - Max Fat: ${cmRate.maxFat} \n Min SNF: ${cmRate.minSnf} - Max SNF : ${cmRate.maxSnf}"
            binding.textViewCMRateDate.text = "Effective From: ${cmRate.startDate} To  ${cmRate.endDate}"
            binding.layoutCMRateSummary.visibility = View.VISIBLE
        } else {
            binding.layoutCMRateSummary.visibility = View.GONE
        }
        
        // Update BM rate summary  
        if (bmRate != null) {
            binding.textViewBMRateSummary.text = "Fat: ₹${bmRate.baseFatRate}/% | Max SNF: ${bmRate.maxSnf}% | Penalty: ₹${bmRate.snfPenalty}/%"
            binding.textViewBMRateDate.text = "Effective From: ${bmRate.startDate} To  ${bmRate.endDate}"
            binding.layoutBMRateSummary.visibility = View.VISIBLE
        } else {
            binding.layoutBMRateSummary.visibility = View.GONE
        }

        if (cmRate != null) {
            val html = generateRateChartHtml(
                minFat = cmRate.minFat!!,
                maxFat = cmRate.maxFat!!,
                minSnf = cmRate.minSnf!!,
                maxSnf = cmRate.maxSnf!!,
                step = 0.1, // or whatever step you use
                rateFactor = 1.0 // or your actual rate factor
            )
            binding.webViewRateChart.visibility = View.VISIBLE
            binding.webViewRateChart.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } else {
            binding.webViewRateChart.visibility = View.GONE
        }
    }

    private fun generateRateChartHtml(
        minFat: Double, maxFat: Double, minSnf: Double, maxSnf: Double, step: Double, rateFactor: Double
    ): String {
        // Read template from assets
        val template = requireContext().assets.open("ratechart_preview_template.html")
            .bufferedReader().use { it.readText() }

        // Generate fat headers
        val fatHeaders = (minFat..maxFat).step(step).map { String.format("%.1f", it) }
        // Generate snf rows
        val snfRows = (minSnf..maxSnf).step(step).map { snf ->
            val fatValues = fatHeaders.map { fatStr ->
                val fat = fatStr.toDouble()
                val rate = (fat * snf * rateFactor) // Example formula
                String.format("%.2f", rate)
            }
            mapOf("snf" to String.format("%.1f", snf), "fatValues" to fatValues)
        }

        // Replace template placeholders (simple, not a full template engine)
        var html = template
        html = html.replace("{{#each fatHeaders}}", fatHeaders.joinToString("") { "<th class=\"fat-header\">$it</th>" })
        html = html.replace("{{/each}}", "")
        html = html.replace("{{#each snfRows}}", snfRows.joinToString("") { row ->
            val snf = row["snf"]
            val fatValues = (row["fatValues"] as List<String>).joinToString("") { "<td>$it</td>" }
            "<tr><td class=\"snf-header\">$snf</td>$fatValues</tr>"
        })
        html = html.replace("{{/each}}", "")
        return html
    }

    // Helper extension for range with step
    private fun ClosedFloatingPointRange<Double>.step(step: Double): List<Double> {
        val list = mutableListOf<Double>()
        var current = start
        while (current <= endInclusive) {
            list.add(current)
            current += step
        }
        return list
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}