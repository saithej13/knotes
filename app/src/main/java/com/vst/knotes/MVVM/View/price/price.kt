package com.vst.knotes.MVVM.View.price

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
import com.vst.knotes.MVVM.Repositories.PriceRepository
import com.vst.knotes.MVVM.Model.pricingModel
import com.vst.knotes.MVVM.Repositories.categoryRepository
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.category.CategoryViewModelFactory
import com.vst.knotes.MVVM.View.category.categoryVM
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.databinding.PriceBinding

class price : BaseFragment<PriceBinding>() {
    private lateinit var viewModel: priceVM
    private var prices: ArrayList<pricingModel> = ArrayList()
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): PriceBinding {
        return PriceBinding.inflate(inflater, parent, false)
    }
    companion object {
        fun newInstance(id: String): AddPrice {
            return AddPrice().apply {
                arguments = Bundle().apply {
                    putString("priceid", id)
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
        viewModel = ViewModelProvider(this, factory).get(priceVM::class.java)
        viewModel.prices.observe(viewLifecycleOwner, { pricesList ->
            prices = pricesList as ArrayList<pricingModel>
            setupRecyclerView()
        })
        binding.fab.setOnClickListener{
            (activity as? BaseActivity)?.replaceFragment(AddPrice.newInstance("") as Fragment)
        }
        try {
            viewModel.fetchAllPrices(STOREID,ZONEID)
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        }
    }
    private fun setupRecyclerView() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        if (prices.isNotEmpty()) {
            binding.recyclerview.adapter = pricingadapter(
                requireContext(),
                prices,
                object : pricingadapter.EditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        Log.d("onEditIconClick","$position")
                        (activity as? BaseActivity)?.replaceFragment(AddPrice.newInstance(position.toString()) as Fragment)
                    }
                },object : pricingadapter.DeleteIconClickListener {
                    override fun onDeleteIconClick(position: Int,name:String) {
                        Log.d("onDeleteIconClick","$position")
                        (activity as BaseActivity).showCustomDialog(
                            "Alert",
                            "Do You Want to Delete this Price "+name,
                            "Yes",
                            "No",
                            {
                                val priceModel = pricingModel().apply {
                                    pricingid = position
                                }
                                viewModel.delete(priceModel)
                            }
                            ,{
                            (activity as BaseActivity).supportFragmentManager.popBackStack()
                        })
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