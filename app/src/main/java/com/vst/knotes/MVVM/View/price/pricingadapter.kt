package com.vst.knotes.MVVM.View.price

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Model.pricingModel
import com.vst.knotes.MVVM.View.product.productadapter.DeleteIconClickListener
import com.vst.knotes.MVVM.View.product.productadapter.EditIconClickListener
import com.vst.knotes.R
import com.vst.knotes.databinding.PricingCellBinding
import java.io.File

class pricingadapter(
    private val context: Context,
    private val data: MutableList<pricingModel>?,
    private val editIconClickListener: EditIconClickListener,
    private val deleteIconClickListener: DeleteIconClickListener
) : RecyclerView.Adapter<pricingadapter.ViewHolder>(), Filterable {


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                // Implement filtering logic here
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Implement publishing results logic here
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PricingCellBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Item = data!![position]
        holder.bind(Item)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    inner class ViewHolder(private val binding: PricingCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: pricingModel) {

            binding.menuname.text = item.pricingid.toString()

            binding.removeItem.setOnClickListener{
                deleteIconClickListener.onDeleteIconClick(item.pricingid.toInt(),"Product Id : "+item.pricingid+"\n Price"+item.price)
            }
            binding.editItem.setOnClickListener{
                editIconClickListener.onEditIconClick(item.pricingid.toInt())
            }
        }
    }

    interface EditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    interface DeleteIconClickListener {
        fun onDeleteIconClick(position: Int,productname:String)
    }
}