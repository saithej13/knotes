package com.vst.knotes.MVVM.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Model.MenuItem
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.R
import com.vst.knotes.databinding.MenucellBinding
import com.vst.knotes.databinding.SaleCategoryCellBinding
import java.io.File
import java.util.HashMap

class addsalecategoryadapter(
private val context: Context,
private val data: ArrayList<categoryModel>?
) : RecyclerView.Adapter<addsalecategoryadapter.ViewHolder>(), Filterable {

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
        val binding = SaleCategoryCellBinding.inflate(LayoutInflater.from(context), parent, false)
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


    inner class ViewHolder(private val binding: SaleCategoryCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: categoryModel) {
            if (!menuItem.categoryimageurl.isNullOrEmpty()) {
                if(menuItem.categoryimageurl.contains("storage")){
                    Picasso.get()
                        .load(File(menuItem.categoryimageurl))
                        .error(R.drawable.fab)
                        .into(binding.categoryImg)
                }
                else{
                    Picasso.get()
                        .load(menuItem.categoryimageurl)
                        .error(R.drawable.fab)
                        .into(binding.categoryImg)
                }

            } else {
                // You can set a default image or a placeholder image here
                binding.categoryImg.setImageResource(R.drawable.fab)
            }

            binding.categoryName.text = menuItem.categoryname

            itemView.setOnClickListener {
                mClickListener?.onItemClick(itemView, bindingAdapterPosition)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}