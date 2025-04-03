package com.vst.knotes.MVVM.View.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.View.category.categoryadapter.DeleteIconClickListener
import com.vst.knotes.MVVM.View.category.categoryadapter.EditIconClickListener
import com.vst.knotes.R
import com.vst.knotes.databinding.ProductCellBinding
import java.io.File

class productadapter(
    private val context: Context,
    private val data: MutableList<productModel>?,
    private val editIconClickListener: EditIconClickListener,
    private val deleteIconClickListener: DeleteIconClickListener
) : RecyclerView.Adapter<productadapter.ViewHolder>(), Filterable {



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
        val binding = ProductCellBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Item = data!![position]
        holder.bind(Item)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }


    inner class ViewHolder(private val binding: ProductCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: productModel) {
            if (!item.productimageurl.isNullOrEmpty()) {
                Picasso.get()
                    .load(File(item.productimageurl))
                    .error(R.drawable.fab)
                    .into(binding.introImg)
            } else {
                // You can set a default image or a placeholder image here
                binding.introImg.setImageResource(R.drawable.fab)
            }

            binding.menuname.text = item.productname
            binding.removeItem.setOnClickListener{
                deleteIconClickListener.onDeleteIconClick(item.productid.toInt(),item.productname)
            }
            binding.editItem.setOnClickListener{
                editIconClickListener.onEditIconClick(item.productid.toInt())
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