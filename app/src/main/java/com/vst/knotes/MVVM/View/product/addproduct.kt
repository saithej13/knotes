package com.vst.knotes.MVVM.View.product

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Repositories.ProductRepository
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.Repositories.categoryRepository
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.category.CategoryViewModelFactory
import com.vst.knotes.MVVM.View.category.categoryVM
import com.vst.knotes.R
import com.vst.knotes.Utils.ImageCaptureListener
import com.vst.knotes.Utils.StringUtils
import com.vst.knotes.databinding.AddproductBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class AddProduct  : BaseFragment<AddproductBinding>(), ImageCaptureListener {
    private lateinit var imageView: ImageView
    private lateinit var imgPath: String
    private lateinit var viewModel: productVM
    var categoryId: String = ""
    var categoryName: String = ""
    var categories: ArrayList<categoryModel> = ArrayList<categoryModel>()
    var products: ArrayList<productModel> = ArrayList<productModel>()
    val productDA = ProductRepository()
    var adapter: ArrayAdapter<*>? = null
    val categoryList = mutableListOf<String>()
    private var spinnerTextview: TextView? = null
    private val categoryCodeToPositionMap = mutableMapOf<String, Int>()
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): AddproductBinding {
        return AddproductBinding.inflate(inflater, parent, false)
    }
    companion object {
        fun newInstance(productId: String): AddProduct {
            return AddProduct().apply {
                arguments = Bundle().apply {
                    putString("productid", productId)
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
        imageView = binding.ivImage
        val factory = ProductViewModelFactory(ProductRepository())
        viewModel = ViewModelProvider(this,factory).get(productVM::class.java)
        viewModel.fetchCategories("1", "1")
        viewModel.categories.observe(viewLifecycleOwner, { categoriesList ->
            categories = categoriesList as ArrayList<categoryModel>
            if (categories.isNotEmpty()) {
                requireActivity().runOnUiThread {
                    populateSpinner()
                }
            }
        })


        binding.spselectcategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedProduct = categoryList[position]
                    val selectedProductId = selectedProduct.split("~")[0]
                    val selectedProductName = selectedProduct.split("~")[1]
                    categoryId = selectedProductId
                    categoryName = selectedProductName
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.capturecategoryimage.setOnClickListener{
            captureImage()
        }
        val productid = arguments?.getString("productid") ?: ""
        if (productid.isNotEmpty()) {
            loadData(productid)
        }
        binding.tvSubmit.setOnClickListener{
            submit(productid)
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

    fun captureImage() {
        (activity as BaseActivity).showImageSelectionDialog(this@AddProduct)
    }
    private fun populateSpinner() {
        if (categories.isEmpty()) {
            return
        }
        var index = 1
        for (category in categories) {
            categoryList.add("${category.categoryid}~${category.categoryname}")
            categoryCodeToPositionMap[category.categoryid] = index++
        }
        categoryList.add(0, "--Select Center--")
        if (adapter == null) {
            adapter = ArrayAdapter(requireContext(), R.layout.custom_spinnerdropdown, categoryList)
            binding.spselectcategory.setAdapter(adapter)
        }
    }
    fun submit(productId: String){
        val product = productModel().apply {
            productname = binding.productName.text.toString()
            productimageurl = imgPath
            productdescription = binding.productDescription.text.toString()
            producttype = StringUtils.getInt(binding.productType.text.toString())
            isactive = 1
            storeid = StringUtils.getInt(STOREID)
            zoneid= StringUtils.getInt(ZONEID)
            uom = StringUtils.getInt(binding.uom.text.toString())
            unitspercase = StringUtils.getInt(binding.unitspercase.text.toString())
            brand = StringUtils.getInt(binding.brand.text.toString())
            categoryid = categoryId.toInt()
        }
        if(productId.isNotEmpty()){
            product.productid = productId
            viewModel.update(product)
            showCategoryDialog("Product Updated Successfully")
        }
        else {
            viewModel.insert(product)
            showCategoryDialog("Product Saved Successfully")
        }

    }
    private fun showCategoryDialog(message: String) {
        (activity as BaseActivity).showCustomDialog(
            "Alert", message, "Ok", "", {
                (activity as BaseActivity).supportFragmentManager.popBackStack()
            }, {
                // No click handler (if needed)
            })
    }

    override fun onImageCaptured(bitmap: Bitmap, imagePath: String) {
        imageView.setImageBitmap(bitmap)
        imgPath = imagePath
    }

    private fun setSpinnerSelection(categoryId: String) {
        requireActivity().runOnUiThread {
            if (categoryId.isNotEmpty()) {
                val position = categoryCodeToPositionMap.get(categoryId)
                if (position != null) {
                    binding.spselectcategory.setSelection(position)
                }
            } else {
                binding.spselectcategory.setSelection(0)
            }
        }
    }

    fun loadData(productid:String){
        viewModel.fetchProductById(STOREID,ZONEID,productid)
        viewModel.products.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                val product = products[0]
                requireActivity().runOnUiThread {
                    binding.productName.setText(product.productname)
                    binding.productDescription.setText(product.productdescription)
                    binding.productType.setText(product.producttype.toString())
                    binding.brand.setText(product.brand.toString())
                    binding.uom.setText(product.uom.toString())
                    binding.unitspercase.setText(product.unitspercase.toString())
                    if (!product.productimageurl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(File(product.productimageurl))
                            .error(R.drawable.fab)
                            .into(imageView)
                        imgPath = product.productimageurl
                    } else {
                        imageView.setImageResource(R.drawable.fab)
                        imgPath = ""
                    }
                }
                GlobalScope.launch {
                    delay(1000)
                    setSpinnerSelection(product.categoryid.toString())
                }
            }
        }
    }

}