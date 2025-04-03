package com.vst.knotes.MVVM.View.SalesCheckout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Adapter.addsalecategoryadapter
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.R
import com.vst.knotes.databinding.SaleCategoryCellBinding
import com.vst.knotes.databinding.SalecheckoutCellBinding
import java.io.File

class salecheckoutadapter (
    private val context: Context,
    private val data: ArrayList<productModel>?
) : RecyclerView.Adapter<salecheckoutadapter.ViewHolder>(), Filterable {

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
        val binding = SalecheckoutCellBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = data!![position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }


    inner class ViewHolder(private val binding: SalecheckoutCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: productModel) {
            if (!menuItem.productimageurl.isNullOrEmpty()) {
                if(menuItem.productimageurl.contains("storage")){
                    Picasso.get()
                        .load(File(menuItem.productimageurl))
                        .error(R.drawable.fab)
                        .into(binding.productImg)
                }
                else{
                    Picasso.get()
                        .load(menuItem.productimageurl)
                        .error(R.drawable.fab)
                        .into(binding.productImg)
                }

            } else {
                // You can set a default image or a placeholder image here
                binding.productImg.setImageResource(R.drawable.fab)
            }
            binding.noOfItemsText.setText(menuItem.quantity.toString())
            binding.productName.text = menuItem.productname

            itemView.setOnClickListener {
                mClickListener?.onItemClick(itemView, bindingAdapterPosition)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}