package com.vst.knotes.MVVM.View.farmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.FarmerViewModel
import com.vst.knotes.R
import com.vst.knotes.databinding.FragmentFarmerBinding

class FarmerFragment : Fragment() {
    
    private var _binding: FragmentFarmerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var farmerViewModel: FarmerViewModel
    private lateinit var farmerAdapter: FarmerAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmerBinding.inflate(inflater, container, false)
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
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        farmerAdapter = FarmerAdapter(
            onEditClick = { farmer ->
                val fragment = AddEditFarmerFragment.newInstance(farmer.farmerId)
                (activity as BaseActivity).replaceFragment(fragment)
            },
            onDeleteClick = { farmer ->
                (activity as BaseActivity).showCustomDialog(
                    title = "Delete Farmer",
                    message = "Are you sure you want to delete ${farmer.farmerName}?",
                    yesButtonText = "Delete",
                    noButtonText = "Cancel",
                    onYesClick = {
                        farmerViewModel.deleteFarmer(farmer)
                    }
                )
            }
        )
        
        binding.recyclerViewFarmers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = farmerAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddFarmer.setOnClickListener {
            val fragment = AddEditFarmerFragment.newInstance()
            (activity as BaseActivity).replaceFragment(fragment)
        }
        
        binding.switchActiveOnly.setOnCheckedChangeListener { _, isChecked ->
            observeFarmers(isChecked)
        }
    }
    
    private fun observeData() {
        observeFarmers(binding.switchActiveOnly.isChecked)
        
        farmerViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is FarmerViewModel.OperationResult.Success -> {
                    (activity as BaseActivity).showCustomDialog(
                        title = "Success",
                        message = result.message,
                        yesButtonText = "OK",
                        noButtonText = null
                    )
                }
                is FarmerViewModel.OperationResult.Error -> {
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
    
    private fun observeFarmers(activeOnly: Boolean) {
        val liveData = if (activeOnly) {
            farmerViewModel.allActiveFarmers
        } else {
            farmerViewModel.allFarmers
        }
        
        liveData.observe(viewLifecycleOwner) { farmers ->
            farmerAdapter.submitList(farmers)
            binding.textViewEmptyState.visibility = if (farmers.isEmpty()) View.VISIBLE else View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}