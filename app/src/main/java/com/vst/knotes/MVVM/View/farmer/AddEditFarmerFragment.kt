package com.vst.knotes.MVVM.View.farmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vst.knotes.MVVM.Model.FarmerModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.FarmerViewModel
import com.vst.knotes.databinding.FragmentAddEditFarmerBinding
import kotlinx.coroutines.launch

class AddEditFarmerFragment : Fragment() {
    
    private var _binding: FragmentAddEditFarmerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var farmerViewModel: FarmerViewModel
    private var farmerId: Int = -1
    private var isEditMode: Boolean = false
    
    companion object {
        private const val ARG_FARMER_ID = "farmer_id"
        
        fun newInstance(farmerId: Int? = null): AddEditFarmerFragment {
            val fragment = AddEditFarmerFragment()
            val bundle = Bundle()
            farmerId?.let { bundle.putInt(ARG_FARMER_ID, it) }
            fragment.arguments = bundle
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditFarmerBinding.inflate(inflater, container, false)
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
            loadFarmerData()
        }
    }
    
    private fun setupViewModel() {
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
    }
    
    private fun parseArguments() {
        arguments?.let {
            if (it.containsKey(ARG_FARMER_ID)) {
                farmerId = it.getInt(ARG_FARMER_ID)
                isEditMode = true
            }
        }
    }
    
    private fun setupUI() {
        binding.textViewTitle.text = if (isEditMode) "Edit Farmer" else "Add Farmer"
        binding.buttonSave.text = if (isEditMode) "Update" else "Save"
    }
    
    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            saveFarmer()
        }
        
        binding.buttonCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    
    private fun observeData() {
        farmerViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.buttonSave.isEnabled = true
            
            when (result) {
                is FarmerViewModel.OperationResult.Success -> {
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
    
    private fun loadFarmerData() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val farmer = farmerViewModel.getFarmerById(farmerId)
            binding.progressBar.visibility = View.GONE
            
            farmer?.let {
                binding.editTextFarmerCode.setText(it.farmerCode)
                binding.editTextFarmerName.setText(it.farmerName)
                binding.editTextMobileNumber.setText(it.mobileNumber)
                binding.editTextAddress.setText(it.address)
            }
        }
    }
    
    private fun saveFarmer() {
        val farmerCode = binding.editTextFarmerCode.text.toString().trim()
        val farmerName = binding.editTextFarmerName.text.toString().trim()
        val mobileNumber = binding.editTextMobileNumber.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonSave.isEnabled = false
        
        if (isEditMode) {
            farmerViewModel.updateFarmer(farmerId, farmerCode, farmerName, mobileNumber, address)
        } else {
            farmerViewModel.insertFarmer(farmerCode, farmerName, mobileNumber, address)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}