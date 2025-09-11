package com.vst.knotes.MVVM.View.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.export.ExportDialogFragment
import com.vst.knotes.MVVM.viewModel.MilkCollectionViewModel
import com.vst.knotes.MVVM.viewModel.ReportsViewModel
import com.vst.knotes.databinding.FragmentMilkCollectionBinding

class MilkCollectionFragment : Fragment() {
    
    private var _binding: FragmentMilkCollectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var milkCollectionViewModel: MilkCollectionViewModel
    private lateinit var collectionAdapter: MilkCollectionAdapter
    private var isShowingTodaysCollections = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilkCollectionBinding.inflate(inflater, container, false)
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
        milkCollectionViewModel = ViewModelProvider(this)[MilkCollectionViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        collectionAdapter = MilkCollectionAdapter(
            onEditClick = { collection ->
                val fragment = AddEditCollectionFragment.newInstance(collection.collection.collectionId)
                (activity as BaseActivity).replaceFragment(fragment)
            },
            onDeleteClick = { collection ->
                (activity as BaseActivity).showCustomDialog(
                    title = "Delete Collection",
                    message = "Are you sure you want to delete this collection for ${collection.farmer.farmerName}?",
                    yesButtonText = "Delete",
                    noButtonText = "Cancel",
                    onYesClick = {
                        milkCollectionViewModel.deleteMilkCollection(collection.collection)
                    }
                )
            },
            onViewDetailsClick = { collection ->
                val fragment = CollectionDetailsFragment.newInstance(collection.collection.collectionId)
                (activity as BaseActivity).replaceFragment(fragment)
            }
        )
        
        binding.recyclerViewCollections.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collectionAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddCollection.setOnClickListener {
            val fragment = AddEditCollectionFragment.newInstance()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.buttonTodaysCollections.setOnClickListener {
            showTodaysCollections()
        }
        
        binding.buttonAllCollections.setOnClickListener {
            showAllCollections()
        }
        
        binding.buttonExportCollections.setOnClickListener {
            exportCollections()
        }
    }
    
    private fun observeData() {
        showAllCollections() // Default view
        
        milkCollectionViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is MilkCollectionViewModel.OperationResult.Success -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Success",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
                is MilkCollectionViewModel.OperationResult.Error -> {
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
    
    private fun showTodaysCollections() {
        isShowingTodaysCollections = true
        val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        milkCollectionViewModel.getCollectionsByDate(today).observe(viewLifecycleOwner) { collections ->
            collectionAdapter.submitList(collections)
            binding.textViewEmptyState.visibility = if (collections.isEmpty()) View.VISIBLE else View.GONE
            binding.textViewCollectionCount.text = "Today's Collections: ${collections.size}"
        }
        
        // Update button states
        binding.buttonTodaysCollections.setBackgroundColor(requireContext().getColor(android.R.color.holo_blue_light))
        binding.buttonAllCollections.setBackgroundColor(requireContext().getColor(android.R.color.darker_gray))
    }
    
    private fun showAllCollections() {
        isShowingTodaysCollections = false
        milkCollectionViewModel.allCollectionsWithFarmers.observe(viewLifecycleOwner) { collections ->
            collectionAdapter.submitList(collections)
            binding.textViewEmptyState.visibility = if (collections.isEmpty()) View.VISIBLE else View.GONE
            binding.textViewCollectionCount.text = "Total Collections: ${collections.size}"
        }
        
        // Update button states
        binding.buttonAllCollections.setBackgroundColor(requireContext().getColor(android.R.color.holo_blue_light))
        binding.buttonTodaysCollections.setBackgroundColor(requireContext().getColor(android.R.color.darker_gray))
    }
    
    private fun exportCollections() {
        val currentCollections = if (isShowingTodaysCollections) {
            // Today's collections are currently shown
            val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            milkCollectionViewModel.getCollectionsByDate(today).value ?: emptyList()
        } else {
            // All collections are currently shown
            milkCollectionViewModel.allCollectionsWithFarmers.value ?: emptyList()
        }
        
        if (currentCollections.isEmpty()) {
            (activity as BaseActivity).showCustomDialog(
                title = "No Data",
                message = "No collections available to export",
                yesButtonText = "OK",
                noButtonText = null
            )
            return
        }
        
        // Create a basic summary for the collections
        val summary = com.vst.knotes.MVVM.viewModel.ReportsViewModel.ReportSummary(
            title = if (isShowingTodaysCollections) "Today's Collections" else "All Collections",
            totalCollections = currentCollections.size,
            totalQuantity = currentCollections.sumOf { it.collection.quantity },
            totalAmount = currentCollections.sumOf { it.collection.amount },
            uniqueFarmers = currentCollections.map { it.farmer.farmerId }.distinct().size,
            averageQuantityPerCollection =0.0,
            averageAmountPerCollection = 0.0,
            averageFat = if (currentCollections.isNotEmpty()) currentCollections.sumOf { it.collection.fat } / currentCollections.size else 0.0,
            averageSnf = if (currentCollections.isNotEmpty()) currentCollections.sumOf { it.collection.snf } / currentCollections.size else 0.0,
            cmCollections = currentCollections.count { it.collection.rateType == "CM" },
            bmCollections = currentCollections.count { it.collection.rateType == "BM" }
        )
        
        val exportDialog = ExportDialogFragment.newInstance(currentCollections, summary)
        exportDialog.show(parentFragmentManager, "ExportDialog")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}