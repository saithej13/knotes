package com.vst.knotes.MVVM.View.SalesCheckout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.Adapter.addsalecategoryadapter
import com.vst.knotes.MVVM.View.BaseActivity.Companion.cartProducts
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.databinding.SalescheckoutBinding


class salescheckout: BaseFragment<SalescheckoutBinding>(){
    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): SalescheckoutBinding {
        return SalescheckoutBinding.inflate(inflater, parent, false)
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

        }
    }
    fun loaddata(){
        binding.rcvcategories.layoutManager = LinearLayoutManager(context)
        if (!cartProducts.isEmpty()) {
            binding.rcvcategories.adapter = context?.let { salecheckoutadapter(it, cartProducts)}
            (binding.rcvcategories.adapter as salecheckoutadapter).setClickListener(object : salecheckoutadapter.ItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    // Handle item click here
                }
            })
            binding.rcvcategories.visibility = View.VISIBLE
        } else {
            binding.rcvcategories.visibility = View.GONE
        }
    }
}