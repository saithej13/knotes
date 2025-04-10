package com.vst.knotes.MVVM.View.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.Model.MenuItem
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.DyamicUi.DynamicFragment
import com.vst.knotes.MVVM.View.Sale.addsale
import com.vst.knotes.MVVM.View.category.Category
import com.vst.knotes.MVVM.View.price.price
import com.vst.knotes.MVVM.View.product.product
import com.vst.knotes.R
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.databinding.Menu2Binding
import com.vst.knotes.databinding.MenuBinding

class menu : BaseFragment<Menu2Binding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): Menu2Binding {
        return Menu2Binding.inflate(inflater, parent, false)
    }
    override fun provideYourFragmentView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?, viewLifecycleOwner: LifecycleOwner): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = OrderRequestAdapter()


//        if (false) {
//            binding.recyclerview.visibility = View.VISIBLE
//            binding.nodata.visibility = View.GONE
//        } else {
//            binding.recyclerview.visibility = View.GONE
//            binding.nodata.visibility = View.VISIBLE
//        }
    }

    class OrderRequestAdapter : RecyclerView.Adapter<OrderRequestAdapter.OrderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.order_request_cell, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            // You can customize this if needed
        }

        override fun getItemCount(): Int = 5 // Display 5 cells

        class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // Bind views if needed, example:
            // val title = itemView.findViewById<TextView>(R.id.titleTV)
        }
    }

}