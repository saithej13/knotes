package com.vst.knotes.MVVM.View.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.MilkCollectionViewModel
import com.vst.knotes.databinding.FragmentCollectionDetailsBinding
import com.vst.knotes.utils.MilkPriceCalculator
import kotlinx.coroutines.launch

class CollectionDetailsFragment : Fragment() {
    
    private var _binding: FragmentCollectionDetailsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var milkCollectionViewModel: MilkCollectionViewModel
    private var collectionId: Int = -1
    
    companion object {
        private const val ARG_COLLECTION_ID = "collection_id"
        
        fun newInstance(collectionId: Int): CollectionDetailsFragment {
            val fragment = CollectionDetailsFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_COLLECTION_ID, collectionId)
            fragment.arguments = bundle
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        parseArguments()
        loadCollectionDetails()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        milkCollectionViewModel = ViewModelProvider(this)[MilkCollectionViewModel::class.java]
    }
    
    private fun parseArguments() {
        arguments?.let {
            collectionId = it.getInt(ARG_COLLECTION_ID, -1)
        }
    }
    
    private fun loadCollectionDetails() {
        // Since we don't have a direct method to get collection by ID with farmer,
        // we'll observe all collections and filter by ID
        binding.progressBar.visibility = View.VISIBLE
        
        milkCollectionViewModel.allCollectionsWithFarmers.observe(viewLifecycleOwner) { collections ->
            val collection = collections.find { it.collection.collectionId == collectionId }
            binding.progressBar.visibility = View.GONE
            
            if (collection != null) {
                displayCollectionDetails(collection)
            } else {
                (activity as BaseActivity).showCustomDialog(
                    title = "Error",
                    message = "Collection not found",
                    yesButtonText = "OK",
                    noButtonText = null,
                    onYesClick = {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                )
            }
        }
    }
    
    private fun displayCollectionDetails(collectionWithFarmer: MilkCollectionWithFarmerModel) {
        val collection = collectionWithFarmer.collection
        val farmer = collectionWithFarmer.farmer
        
        binding.apply {
            // Farmer Information
            textViewFarmerCode.text = farmer.farmerCode
            textViewFarmerName.text = farmer.farmerName
            textViewFarmerMobile.text = farmer.mobileNumber
            textViewFarmerAddress.text = farmer.address
            
            // Collection Information
            textViewCollectionDate.text = collection.collectionDate
            textViewCollectionTime.text = collection.collectionTime
            textViewQuantity.text = "${collection.quantity} Liters"
            textViewFat.text = "${collection.fat}%"
            textViewSnf.text = "${collection.snf}%"
            textViewRateType.text = collection.rateType
            textViewRatePerLiter.text = "₹${String.format("%.2f", collection.rate)}"
            textViewTotalAmount.text = "₹${String.format("%.2f", collection.amount)}"
            
            // Rate type specific information
            if (collection.rateType == MilkPriceCalculator.RATE_TYPE_CM) {
                cardRateType.setCardBackgroundColor(requireContext().getColor(android.R.color.holo_blue_light))
                textViewRateFormula.text = "CM Formula: (Fat × Fat Rate) + (SNF × SNF Rate)"
            } else {
                cardRateType.setCardBackgroundColor(requireContext().getColor(android.R.color.holo_green_light))
                textViewRateFormula.text = "BM Formula: (Fat × Rate) - (Max SNF - Current SNF) × Penalty"
            }
            
            // Quality assessment
            displayQualityAssessment(collection.fat, collection.snf)
            
            // Calculation breakdown
            displayCalculationBreakdown(collection)
            
            // Remarks
            if (!collection.remarks.isNullOrBlank()) {
                textViewRemarks.text = collection.remarks
                layoutRemarks.visibility = View.VISIBLE
            } else {
                layoutRemarks.visibility = View.GONE
            }
            
            // Show farmer's recent collections
            loadRecentCollections(farmer.farmerId)
        }
    }
    
    private fun displayQualityAssessment(fat: Double, snf: Double) {
        binding.apply {
            // Fat quality
            val fatQuality = when {
                fat >= 4.5 -> Pair("Excellent", android.R.color.holo_green_dark)
                fat >= 4.0 -> Pair("Good", android.R.color.holo_blue_dark)
                fat >= 3.5 -> Pair("Average", android.R.color.holo_orange_dark)
                else -> Pair("Low", android.R.color.holo_red_dark)
            }
            textViewFatQuality.text = fatQuality.first
            textViewFatQuality.setTextColor(requireContext().getColor(fatQuality.second))
            
            // SNF quality
            val snfQuality = when {
                snf >= 8.8 -> Pair("Excellent", android.R.color.holo_green_dark)
                snf >= 8.5 -> Pair("Good", android.R.color.holo_blue_dark)
                snf >= 8.2 -> Pair("Average", android.R.color.holo_orange_dark)
                else -> Pair("Low", android.R.color.holo_red_dark)
            }
            textViewSnfQuality.text = snfQuality.first
            textViewSnfQuality.setTextColor(requireContext().getColor(snfQuality.second))
            
            // Overall quality
            val overallScore = (fat + snf) / 2
            val overallQuality = when {
                overallScore >= 6.5 -> Pair("Premium", android.R.color.holo_green_dark)
                overallScore >= 6.0 -> Pair("High", android.R.color.holo_blue_dark)
                overallScore >= 5.5 -> Pair("Standard", android.R.color.holo_orange_dark)
                else -> Pair("Below Standard", android.R.color.holo_red_dark)
            }
            textViewOverallQuality.text = overallQuality.first
            textViewOverallQuality.setTextColor(requireContext().getColor(overallQuality.second))
        }
    }
    
    private fun displayCalculationBreakdown(collection: com.vst.knotes.MVVM.Model.MilkCollectionModel) {
        binding.apply {
            val breakdown = StringBuilder()
            breakdown.append("Calculation Breakdown:\n\n")
            breakdown.append("Quantity: ${collection.quantity} L\n")
            breakdown.append("Fat: ${collection.fat}%\n")
            breakdown.append("SNF: ${collection.snf}%\n")
            breakdown.append("Rate Type: ${collection.rateType}\n\n")
            breakdown.append("Rate per Liter: ₹${String.format("%.2f", collection.rate)}\n")
            breakdown.append("Total Amount: ${collection.quantity} × ₹${String.format("%.2f", collection.rate)} = ₹${String.format("%.2f", collection.amount)}")
            
            textViewCalculationBreakdown.text = breakdown.toString()
        }
    }
    
    private fun loadRecentCollections(farmerId: Int) {
        milkCollectionViewModel.getCollectionsByFarmer(farmerId).observe(viewLifecycleOwner) { collections ->
            val recentCollections = collections.take(5) // Show last 5 collections
            if (recentCollections.size > 1) { // More than current collection
                val summary = StringBuilder()
                summary.append("Recent Collections (Last 5):\n\n")
                
                recentCollections.forEach { collection ->
                    summary.append("${collection.collection.collectionDate} - ")
                    summary.append("${collection.collection.quantity}L - ")
                    summary.append("₹${String.format("%.2f", collection.collection.amount)}\n")
                }
                
                binding.textViewRecentCollections.text = summary.toString()
                binding.layoutRecentCollections.visibility = View.VISIBLE
            } else {
                binding.layoutRecentCollections.visibility = View.GONE
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonEdit.setOnClickListener {
            val fragment = AddEditCollectionFragment.newInstance(collectionId)
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.buttonBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        
        binding.buttonViewFarmerDetails.setOnClickListener {
            // Navigate to farmer details - this could be implemented later
            (activity as BaseActivity).showCustomDialog(
                title = "Farmer Details",
                message = "Farmer details view will be implemented in the next version",
                yesButtonText = "OK",
                noButtonText = null
            )
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}