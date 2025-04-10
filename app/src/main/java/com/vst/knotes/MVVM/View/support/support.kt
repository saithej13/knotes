package com.vst.knotes.MVVM.View.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.category.Category
import com.vst.knotes.MVVM.View.menu.menu.OrderRequestAdapter
import com.vst.knotes.R
import com.vst.knotes.databinding.ProfileBinding
import com.vst.knotes.databinding.SupportBinding

class support : BaseFragment<SupportBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): SupportBinding {
        return SupportBinding.inflate(inflater, parent, false)
    }

    override fun provideYourFragmentView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?, viewLifecycleOwner: LifecycleOwner): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.visibility = View.VISIBLE
        binding.NoDataTv.visibility = View.GONE

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = MyOrderRequestAdapter {
            replaceFragment(OrderDetails())
        }
    }
    class MyOrderRequestAdapter(private val onItemClick: () -> Unit) : RecyclerView.Adapter<MyOrderRequestAdapter.MyOrderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_order_cell, parent, false)
            return MyOrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
            // You can customize this if needed
            holder.itemView.setOnClickListener {
                onItemClick() // trigger navigation
            }
        }

        override fun getItemCount(): Int = 2

        class MyOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}