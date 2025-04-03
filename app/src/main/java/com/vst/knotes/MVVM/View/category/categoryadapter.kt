package com.vst.knotes.MVVM.View.category


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.R
import com.vst.knotes.databinding.CategoryCellBinding
import java.io.File

class categoryadapter(
    private val context: Context,
    private val data: MutableList<categoryModel>?,
    private val editIconClickListener: EditIconClickListener,
    private val deleteIconClickListener: DeleteIconClickListener
) : RecyclerView.Adapter<categoryadapter.ViewHolder>(), Filterable {

    private var filteredData = mutableListOf<categoryModel>()

    init {
        filteredData = data?.toMutableList() ?: mutableListOf()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    results.values = data
                    results.count = data?.size ?: 0
                } else {
                    val filteredList = mutableListOf<categoryModel>()
                    val filterPattern = constraint.toString().toLowerCase().trim()


                    data?.forEach {
                        if (it.categoryname.toLowerCase().contains(filterPattern)) {
                            filteredList.add(it)
                        }
                    }

                    results.values = filteredList
                    results.count = filteredList.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredData = results?.values as MutableList<categoryModel>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryCellBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    inner class ViewHolder(private val binding: CategoryCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: categoryModel) {
            if (!item.categoryimageurl.isNullOrEmpty()) {
                Picasso.get()
                    .load(File(item.categoryimageurl))
                    .error(R.drawable.fab)
                    .into(binding.introImg)
            } else {
                binding.introImg.setImageResource(R.drawable.fab)
            }

            binding.menuname.text = item.categoryname

            binding.removeItem.setOnClickListener {
                deleteIconClickListener.onDeleteIconClick(item.categoryid.toInt(), item.categoryname)
            }

            binding.editItem.setOnClickListener {
                editIconClickListener.onEditIconClick(item.categoryid.toInt())
            }
        }
    }

    interface EditIconClickListener {
        fun onEditIconClick(position: Int)
    }

    interface DeleteIconClickListener {
        fun onDeleteIconClick(position: Int, categoryname: String)
    }
}