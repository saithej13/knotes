package com.vst.knotes.MVVM.View.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.menu.menu.OrderRequestAdapter
import com.vst.knotes.R
import com.vst.knotes.databinding.OrderDetailsScreenBinding

class OrderDetails : BaseFragment<OrderDetailsScreenBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): OrderDetailsScreenBinding {
        return OrderDetailsScreenBinding.inflate(inflater, parent, false)
    }
    override fun provideYourFragmentView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?, viewLifecycleOwner: LifecycleOwner): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemDetailsRV.layoutManager = LinearLayoutManager(requireContext())
        binding.itemDetailsRV.adapter = OrderItemsDetailsAdapter()



    }

    class OrderItemsDetailsAdapter : RecyclerView.Adapter<OrderItemsDetailsAdapter.OrderItemsDetailsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemsDetailsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_details_itemdetails_cell, parent, false)
            return OrderItemsDetailsViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderItemsDetailsViewHolder, position: Int) {
            // You can customize this if needed
        }

        override fun getItemCount(): Int = 2 // Display 5 cells

        class OrderItemsDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}