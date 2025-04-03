package com.vst.knotes.MVVM.View.Sale

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.Adapter.addsalecategoryadapter
import com.vst.knotes.MVVM.Adapter.addsaleproductsadapter
import com.vst.knotes.MVVM.Repositories.SalesRepository
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.pricingModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.SalesCheckout.salescheckout
import com.vst.knotes.Utils.StringUtils
import com.vst.knotes.databinding.AddsaleBinding
import java.util.HashMap


class addsale : BaseFragment<AddsaleBinding>() {
    var hashMapPricing: LinkedHashMap<String, LinkedHashMap<String, ArrayList<pricingModel>>> = LinkedHashMap<String, LinkedHashMap<String, ArrayList<pricingModel>>>()
    var hmCategories : HashMap<String,ArrayList<categoryModel>> = HashMap<String,ArrayList<categoryModel>>()
    var hmproducts : HashMap<Int,ArrayList<productModel>> = HashMap<Int,ArrayList<productModel>>()
    var arrCategories : ArrayList<categoryModel> = ArrayList<categoryModel>()
    var saleDA = SalesRepository()
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): AddsaleBinding {
        return AddsaleBinding.inflate(inflater, parent, false)
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
        loaddata()
        binding.btnNext.setOnClickListener{
            (activity as? BaseActivity)?.replaceFragment(salescheckout::class.java.newInstance() as Fragment)
        }
    }
    fun loaddata(){
//        hashMapPricing = saleDA.getPricing()
//        hmCategories = saleDA.getCategories("1","1")
        hmproducts = saleDA.getProducts("1","1")
        arrCategories = saleDA.getarrCategories("1","1")
        binding.rcvcategories.layoutManager = LinearLayoutManager(context)
        if (!arrCategories.isEmpty()) {
            binding.rcvcategories.adapter = context?.let { addsalecategoryadapter(it, arrCategories)}
            (binding.rcvcategories.adapter as addsalecategoryadapter).setClickListener(object : addsalecategoryadapter.ItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    // Handle item click here
                    val categoryid = arrCategories[position]?.categoryid
                    Log.d("categoryid","categoryid: "+categoryid)
                    loadProductsForCategory(StringUtils.getInt(categoryid.toString()))
                    Log.d("hmproducts","hmproducts: "+hmproducts.get(StringUtils.getInt(categoryid.toString())))
                    Log.d("arrCategories","arrCategories: "+arrCategories)
                    //load products recyclerview based on categoryid
                }
            })
            binding.rcvcategories.visibility = View.VISIBLE
            binding.rcvcategoriesnodata.visibility = View.GONE
        } else {
            binding.rcvcategories.visibility = View.GONE
            binding.rcvcategoriesnodata.visibility = View.VISIBLE
        }
    }
    fun loadProductsForCategory(categoryid: Int?) {
        binding.gvproducts.numColumns = 2
        val ctx = context  // Store context in a local variable to avoid nullability issues

        if (ctx != null && hmproducts[categoryid] != null) {
            val productsList = hmproducts[categoryid]!! // Safe to use '!!' since we already checked for null
            val adapter = addsaleproductsadapter(ctx, productsList)
            binding.gvproducts.adapter = adapter as BaseAdapter
//            adapter.setClickListener(object : addsaleproductsadapter.ItemClickListener {
//                override fun onItemClick(view: View?, position: Int) {
//                    // Add the clicked product to cartProducts
//                    if (position in productsList.indices) {
//                        if(cartProducts.contains(productsList[position]))
//                        {
//                            //update quantity
////                            cartProducts.get(productsList[position].zoneid)
//                            Log.d("update","quantity")
//                        }
//                        else{
//                            cartProducts.add(productsList[position])
//                        }
//
//                        println("Added to cart: ${productsList[position].productname}") // Debug log
//                    }
//                }
//            })

            binding.gvproducts.visibility = View.VISIBLE
        } else {
            binding.gvproducts.visibility = View.GONE
        }
    }

}