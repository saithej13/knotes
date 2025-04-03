package com.vst.knotes.MVVM.View.Sale

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.Model.saleModel
import com.vst.knotes.databinding.SaleCellBinding

class addsaleadapter (
    private val context: Context,
    private val data: MutableList<saleModel>?
    ): RecyclerView.Adapter<addsaleadapter.ViewHolder>(), Filterable {

        private var mClickListener: ItemClickListener? = null

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
            val binding = SaleCellBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val Item = data!![position]
            holder.bind(Item)
        }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

        fun setClickListener(itemClickListener: ItemClickListener?) {
            mClickListener = itemClickListener
        }

        inner class ViewHolder(private val binding: SaleCellBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: saleModel) {
                binding.date.text = item.saledate
                binding.paymenttype.text = item.paymenttyep
                binding.customername.text = item.customername
                binding.totalamount.text = item.totalamount.toString()
                binding.totaltaxamount.text = item.totaltaxamount.toString()
                binding.totaldiscountamount.text = item.totaldiscountamount.toString()
                itemView.setOnClickListener {
                    mClickListener?.onItemClick(itemView, bindingAdapterPosition)
                }
            }
        }

        interface ItemClickListener {
            fun onItemClick(view: View?, position: Int)
        }
}