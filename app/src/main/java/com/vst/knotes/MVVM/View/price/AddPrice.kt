package com.vst.knotes.MVVM.View.price

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.Repositories.PriceRepository
import com.vst.knotes.MVVM.Model.pricingModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.R
import com.vst.knotes.Utils.CalendarUtils
import com.vst.knotes.Utils.ImageCaptureListener
import com.vst.knotes.databinding.AddpriceBinding
import java.util.Calendar
import androidx.lifecycle.Observer
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.productModel


class AddPrice : BaseFragment<AddpriceBinding>(), ImageCaptureListener {
    private lateinit var imageView: ImageView
    private lateinit var imgPath: String
    var productId: String = ""
    var productName: String = ""
    var startdate:String=""
    var enddate : String=""
    var monthFrom:Int = 0
    var yearFrom:Int = 0
    var dayFrom:Int = 0
    var monthTo:Int = 0
    var yearTo:Int = 0
    var dayTo:Int = 0
    val productsList = mutableListOf<String>()
    var adapter: ArrayAdapter<*>? = null
//    private val productCodeToPositionMap = mutableMapOf<String, Int>()
val productCodeToPositionMap = mutableMapOf<Int, Int>()
    var prices: ArrayList<pricingModel> = ArrayList<pricingModel>()
    var arrProducts : ArrayList<productModel> = ArrayList<productModel>()
    private lateinit var viewModel: priceVM
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): AddpriceBinding {
        return AddpriceBinding.inflate(inflater, parent, false)
    }
    companion object {
        fun newInstance(categoryId: String): AddPrice {
            return AddPrice().apply {
                arguments = Bundle().apply {
                    putString("priceid", categoryId)
                }
            }
        }
    }
    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = PriceViewModelFactory(PriceRepository())
        viewModel = ViewModelProvider(this,factory).get(priceVM::class.java)
        productId=""
        val priceid = arguments?.getString("priceid") ?: ""
        val c: Calendar = Calendar.getInstance()
        monthTo = c.get(Calendar.MONTH)
        yearTo = c.get(Calendar.YEAR)
        dayTo = c.get(Calendar.DAY_OF_MONTH)
        monthFrom = c.get(Calendar.MONTH)
        yearFrom = c.get(Calendar.YEAR)
        dayFrom = c.get(Calendar.DAY_OF_MONTH)
        var selectedDate = CalendarUtils.getOrderSummaryDate(yearFrom, monthFrom, dayFrom)
        startdate = selectedDate
        binding.startdate.setText(CalendarUtils.getFormatedDatefromString(selectedDate))
        binding.startdate.setTag(selectedDate)
        selectedDate = CalendarUtils.getOrderSummaryDate(yearTo, monthTo, dayTo)
        enddate = selectedDate
        binding.enddate.setText(CalendarUtils.getFormatedDatefromString(selectedDate))
        binding.enddate.setTag(selectedDate)
        binding.tvSubmit.setOnClickListener{
            submit(priceid)
        }
        binding.llstartdate.setOnClickListener{
            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { view, yearSel, monthOfYear, dayOfMonth ->
                        val selectedDate: String =
                            CalendarUtils.getOrderSummaryDate(yearSel, monthOfYear, dayOfMonth)
                        if (!selectedDate.equals(startdate, ignoreCase = true)) {
                            if (CalendarUtils.getDiffBtwDatesInDays(selectedDate,enddate) >= 0) {
                                yearFrom = yearSel
                                monthFrom = monthOfYear
                                dayFrom = dayOfMonth

                                startdate = selectedDate
                                binding.startdate.setText(CalendarUtils.getFormatedDatefromString(startdate))
                                binding.startdate.setTag(startdate)
                            } else (activity as BaseActivity).showCustomDialog(
                                "Alert",
                                getString(com.vst.knotes.R.string.from_date_should_not_be_greater_than_to_date),
                                getString(com.vst.knotes.R.string.ok),
                                null,
                                null
                            )
                        }
                    }, yearFrom, monthFrom, dayFrom
                ).show()
            }
        }
        binding.llenddate.setOnClickListener{
            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { view, yearSel, monthOfYear, dayOfMonth ->
                        val selectedDate: String =
                            CalendarUtils.getOrderSummaryDate(yearSel, monthOfYear, dayOfMonth)
                        if (!selectedDate.equals(enddate, ignoreCase = true)) {
                            if (CalendarUtils.getDiffBtwDatesInDays(startdate, selectedDate) >= 0) {
                                yearTo = yearSel
                                monthTo = monthOfYear
                                dayTo = dayOfMonth

                                enddate = selectedDate
                                binding.enddate.setText(CalendarUtils.getFormatedDatefromString(enddate))
                                binding.enddate.setTag(enddate)
                            } else (activity as BaseActivity).showCustomDialog(
                                "Alert",
                                getString(com.vst.knotes.R.string.to_date_should_not_be_lesser_than_from_date),
                                getString(com.vst.knotes.R.string.ok),
                                null,
                                null
                            )
                        }
                    }, yearTo, monthTo, dayTo
                ).show()
            }
        }

        viewModel.fetchProducts(STOREID, ZONEID)
        viewModel.products.observe(viewLifecycleOwner, Observer { productsList ->
            val formattedList = mutableListOf("--Select Product--")
            var index = 1

            for (product in productsList) {
                formattedList.add("${product.productid}~${product.productname}")
                productCodeToPositionMap[product.productid.toInt()] = index++
            }

            val productAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinnerdropdown, formattedList)
            productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spselect.adapter = productAdapter

            if (productId.isNotEmpty()) {
                setSpinnerSelection(productId.toInt(), productCodeToPositionMap)
            }
        })
        if(priceid.isNotEmpty()){
            loadData(priceid)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                // Show error message to the user (e.g., using a Toast or Snackbar)
                (activity as BaseActivity).showCustomDialog("Alert",errorMessage+"","Ok","",{
                    //yes click
                    (activity as BaseActivity).supportFragmentManager.popBackStack()
                },{
                    //no click
                })
            }
        })

        binding.spselect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedProduct = productsList[position]
                    val selectedProductId = selectedProduct.split("~")[0]
                    productId = selectedProductId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when no product is selected (if needed)
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as BaseActivity).setImageCaptureListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as BaseActivity).removeImageCaptureListener()
    }
    fun submit(pricingid: String){
        val price = pricingModel().apply {
            productid = productId.toInt()
            price = binding.price.text.toString().toDouble()
            sgst = binding.sgst.text.toString().toDouble()
            cgst = binding.cgst.text.toString().toDouble()
            igst = binding.igst.text.toString().toDouble()
            othertax = binding.othertax.text.toString().toDouble()
            discount = binding.discount.text.toString().toDouble()
            storeid = STOREID.toInt()
            zoneid = ZONEID.toInt()
            startdate = startdate
            enddate = enddate
        }
        if(pricingid.isNotEmpty()){
            price.pricingid = pricingid.toInt()
            viewModel.update(price)
            showDialog("Price Updated Successfully")
        }
        else {
            viewModel.insert(price)
            showDialog("Price Saved Successfully")
        }
    }

    override fun onImageCaptured(bitmap: Bitmap, imagePath: String) {
        imageView.setImageBitmap(bitmap)
        imgPath = imagePath
    }

fun loadData(pricingid:String){
    viewModel.fetchPricesById(STOREID,ZONEID,pricingid)
    viewModel.prices.observe(viewLifecycleOwner){prices->
        if(prices.isNotEmpty()){
            val price = prices[0]
            binding.price.setText(price.price.toString())
            binding.sgst.setText(price.sgst.toString())
            binding.cgst.setText(price.cgst.toString())
            binding.igst.setText(price.igst.toString())
            binding.discount.setText(price.discount.toString())
            binding.othertax.setText(price.othertax.toString())
            setSpinnerSelection(price.productid.toInt(), productCodeToPositionMap)
            binding.startdate.setText(price.startdate)
            binding.enddate.setText(price.enddate)
        }
    }
}
    private fun setSpinnerSelection(productId: Int, productCodeToPositionMap: Map<Int, Int>) {
        val position = productCodeToPositionMap[productId] ?: 0
        binding.spselect.setSelection(position)
    }
    private fun showDialog(message: String) {
        (activity as BaseActivity).showCustomDialog(
            "Alert", message, "Ok", "", {
                (activity as BaseActivity).supportFragmentManager.popBackStack()
            }, {
                // No click handler (if needed)
            })
    }
}