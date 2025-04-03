package com.vst.knotes.MVVM.View.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Repositories.ProductRepository
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.Repositories.categoryRepository
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.category.AddCategory
import com.vst.knotes.MVVM.View.category.CategoryViewModelFactory
import com.vst.knotes.MVVM.View.category.categoryVM
import com.vst.knotes.MVVM.View.category.categoryadapter
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.databinding.ProductBinding


class product : BaseFragment<ProductBinding>() {
    private lateinit var viewModel: productVM
    var products: ArrayList<productModel> = ArrayList<productModel>()
//    private lateinit var ItemList: MutableList<productModel>
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): ProductBinding {
        return ProductBinding.inflate(inflater, parent, false)
    }
//    companion object {
//        fun newInstance(id: String): AddProduct {
//            return AddProduct().apply {
//                arguments = Bundle().apply {
//                    putString("productid", id)
//                }
//            }
//        }
//    }
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
        val factory = ProductViewModelFactory(ProductRepository())
        viewModel = ViewModelProvider(this, factory).get(productVM::class.java)
        binding.fab.setOnClickListener{
            (activity as? BaseActivity)?.replaceFragment(AddProduct::class.java.newInstance() as Fragment)
        }
        viewModel.products.observe(viewLifecycleOwner, { productsList ->
            products = productsList as ArrayList<productModel>
            setupRecyclerView()
        })
        try {
            viewModel.fetchAllProducts(STOREID, ZONEID)
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        }
    }
    private fun setupRecyclerView() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        if (products.isNotEmpty()) {
            binding.recyclerview.adapter = productadapter(
                requireContext(),
                products,
                object : productadapter.EditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        Log.d("onEditIconClick", "$position")
                        (activity as BaseActivity).replaceFragment(AddProduct.newInstance(position.toString()) as Fragment)
                    }
                },
                object : productadapter.DeleteIconClickListener {
                    override fun onDeleteIconClick(position: Int, name: String) {
                        Log.d("onDeleteIconClick", "$position")
                        (activity as BaseActivity).showCustomDialog(
                            "Alert",
                            "Do You Want to Delete Product $name?",
                            "Yes",
                            "No",
                            {
                                val productModel = productModel().apply {
                                    productid = position.toString()
                                }
                                viewModel.delete(productModel)
                            },
                            {
                                // No clicked, just dismiss
                            }
                        )
                    }
                })
            binding.recyclerview.visibility = View.VISIBLE
            binding.nodata.visibility = View.GONE
        } else {
            binding.recyclerview.visibility = View.GONE
            binding.nodata.visibility = View.VISIBLE
        }
    }
}