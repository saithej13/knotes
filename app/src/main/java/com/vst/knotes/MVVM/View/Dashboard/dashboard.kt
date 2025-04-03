package com.vst.knotes.MVVM.View.Dashboard

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.product.AddProduct
import com.vst.knotes.MVVM.View.product.product
import com.vst.knotes.databinding.DashboardBinding


class dashboard : BaseFragment<DashboardBinding>() {
    var show_more_it :Boolean = false
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): DashboardBinding {
        return DashboardBinding.inflate(inflater, parent, false)
    }
    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val show_all = binding.quicklinks.showall
        val products = binding.quicklinks.products
        val show_more = binding.quicklinks.showmore
        show_all.setOnClickListener{
            if(show_more_it){
                show_more.visibility = View.VISIBLE
                show_more_it=false
            }
            else{
                show_more.visibility = View.GONE
                show_more_it=true
            }
        }
        products.setOnClickListener{
            (activity as? BaseActivity)?.replaceFragment(product::class.java.newInstance() as Fragment)
        }
    }
}