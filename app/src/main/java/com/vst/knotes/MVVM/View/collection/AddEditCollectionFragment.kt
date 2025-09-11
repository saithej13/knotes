package com.vst.knotes.MVVM.View.collection

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vst.knotes.MVVM.Model.FarmerModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.viewModel.FarmerViewModel
import com.vst.knotes.MVVM.viewModel.MilkCollectionViewModel
import com.vst.knotes.databinding.FragmentAddEditCollectionBinding
import com.vst.knotes.utils.MilkPriceCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditCollectionFragment : Fragment() {
    
    private var _binding: FragmentAddEditCollectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var milkCollectionViewModel: MilkCollectionViewModel
    private lateinit var farmerViewModel: FarmerViewModel
    
    private var collectionId: Int = -1
    private var isEditMode: Boolean = false
    private var farmersList: List<FarmerModel> = emptyList()
    
    companion object {
        private const val ARG_COLLECTION_ID = "collection_id"
        
        fun newInstance(collectionId: Int? = null): AddEditCollectionFragment {
            val fragment = AddEditCollectionFragment()
            val bundle = Bundle()
            collectionId?.let { bundle.putInt(ARG_COLLECTION_ID, it) }
            fragment.arguments = bundle
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModels()
        parseArguments()
        setupUI()
        setupClickListeners()
        observeData()
        
        if (isEditMode) {
            loadCollectionData()
        }
    }
    
    private fun setupViewModels() {
        milkCollectionViewModel = ViewModelProvider(this)[MilkCollectionViewModel::class.java]
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
    }
    
    private fun parseArguments() {
        arguments?.let {
            if (it.containsKey(ARG_COLLECTION_ID)) {
                collectionId = it.getInt(ARG_COLLECTION_ID)
                isEditMode = true
            }
        }
    }
    
    private fun setupUI() {
        binding.textViewTitle.text = if (isEditMode) "Edit Collection" else "Add Collection"
        binding.buttonSave.text = if (isEditMode) "Update" else "Save"
        
        // Setup rate type spinner
        val rateTypes = listOf(MilkPriceCalculator.RATE_TYPE_CM, MilkPriceCalculator.RATE_TYPE_BM)
        val rateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rateTypes)
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRateType.adapter = rateAdapter
        
        // Setup SNF spinner
//        val snfOptions = MilkPriceCalculator.getSnfOptions()
//        val snfAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, snfOptions)
//        snfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerSnf.adapter = snfAdapter
        
        // Set default values
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDateTime = Date()
        
        binding.tvDate.setText(dateFormat.format(currentDateTime))
        binding.tvShift.setText(timeFormat.format(currentDateTime))
//        binding.spinnerSnf.setSelection(snfOptions.indexOf(8.5)) // Default SNF 8.5
        
        setupInputChangeListeners()
    }
    
    private fun setupInputChangeListeners() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculatePricePreview()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        
        binding.editTextQuantity.addTextChangedListener(textWatcher)
        binding.editTextFat.addTextChangedListener(textWatcher)
        binding.editTextSnf.addTextChangedListener(textWatcher)
        
        binding.spinnerRateType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculatePricePreview()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
        
//        binding.spinnerSnf.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
//                calculatePricePreview()
//            }
//            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
//        })
    }
    
    private fun setupClickListeners() {
        binding.tvDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.tvShift.setOnClickListener {
            showTimePicker()
        }
        
        binding.buttonSave.setOnClickListener {
            saveCollection()
        }
        
//        binding.buttonCancel.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }
        
        binding.buttonCalculate.setOnClickListener {
            calculatePricePreview()
        }
    }
    
    private fun observeData() {
        // Observe farmers
        farmerViewModel.allActiveFarmers.observe(viewLifecycleOwner) { farmers ->
            farmersList = farmers
            setupFarmerSpinner()
        }
        
        // Observe calculation results
        milkCollectionViewModel.calculatedAmount.observe(viewLifecycleOwner) { amount ->
            binding.textViewTotalAmount.text = "Total Amount: ₹${String.format("%.2f", amount)}"
            binding.textViewTotalAmount.visibility = View.VISIBLE
        }
        
        milkCollectionViewModel.ratePerLiter.observe(viewLifecycleOwner) { rate ->
            binding.textViewRatePerLiter.text = "Rate per Liter: ₹${String.format("%.2f", rate)}"
            binding.textViewRatePerLiter.visibility = View.VISIBLE
        }
        
        // Observe operation results
        milkCollectionViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.buttonSave.isEnabled = true
            
            when (result) {
                is MilkCollectionViewModel.OperationResult.Success -> {
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
    
    private fun setupFarmerSpinner() {
        val farmerNames = farmersList.map { "${it.farmerCode} - ${it.farmerName}" }
        val farmerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, farmerNames)
        farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFarmer.adapter = farmerAdapter
    }
    
    private fun loadCollectionData() {
        // This would be implemented if we had a method to get collection by ID
        // For now, we'll leave it as placeholder
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            // TODO: Load existing collection data
            binding.progressBar.visibility = View.GONE
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.tvShift.setText(timeFormat.format(selectedTime.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }
    
    private fun calculatePricePreview() {
        val quantity = binding.editTextQuantity.text.toString().toDoubleOrNull()
        val fat = binding.editTextFat.text.toString().toDoubleOrNull()
        val snf = binding.editTextSnf.text.toString().toDoubleOrNull()
        val rateType = binding.spinnerRateType.selectedItem?.toString()
        
        if (quantity != null && fat != null && snf != null && !rateType.isNullOrBlank()) {
            milkCollectionViewModel.calculatePricePreview(quantity, fat, snf, rateType)
        } else {
            binding.textViewTotalAmount.visibility = View.GONE
            binding.textViewRatePerLiter.visibility = View.GONE
        }
    }
    
    private fun saveCollection() {
        val farmerIndex = binding.spinnerFarmer.selectedItemPosition
        if (farmerIndex < 0 || farmerIndex >= farmersList.size) {
            (activity as BaseActivity).showCustomDialog(
                title = "Error",
                message = "Please select a farmer",
                yesButtonText = "OK",
                noButtonText = null
            )
            return
        }
        
        val farmer = farmersList[farmerIndex]
        val quantity = binding.editTextQuantity.text.toString().toDoubleOrNull() ?: 0.0
        val fat = binding.editTextFat.text.toString().toDoubleOrNull() ?: 0.0
        val snf = binding.editTextSnf.text.toString()?.toDoubleOrNull() ?: 0.0
        val rateType = binding.spinnerRateType.selectedItem?.toString() ?: ""
//        val remarks = binding.editTextRemarks.text.toString().trim()
        val remarks = "";
        
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonSave.isEnabled = false
        
        if (isEditMode) {
            val collectionDate = binding.tvDate.text.toString()
            val collectionTime = binding.tvShift.text.toString() + ":00"
            milkCollectionViewModel.updateMilkCollection(
                collectionId, farmer.farmerId, quantity, fat, snf, rateType,
                collectionDate, collectionTime, remarks
            )
        } else {
            milkCollectionViewModel.addMilkCollection(
                farmer.farmerId, quantity, fat, snf, rateType, remarks
            )
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}