package com.vst.knotes.MVVM.View.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Model.MenuItem
import com.vst.knotes.R
import com.vst.knotes.databinding.LayoutScreenOnboardingBinding
import com.vst.knotes.databinding.MenucellBinding
import java.lang.String
import kotlin.Array
import kotlin.Int

class MenuAdapter(
    private val context: Context,
    private val data: MutableList<MenuItem>?
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>(), Filterable {

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
        val binding = MenucellBinding.inflate(LayoutInflater.from(context), parent, false)
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

    inner class ViewHolder(private val binding: MenucellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: MenuItem) {
            if (!menuItem.menuurl.isNullOrEmpty()) {
                Picasso.get()
                    .load(menuItem.menuurl)
                    .error(R.drawable.fab)
                    .into(binding.introImg)
            } else {
                // You can set a default image or a placeholder image here
                binding.introImg.setImageResource(R.drawable.fab)
            }

            binding.menuname.text = menuItem.Menuname

            itemView.setOnClickListener {
                mClickListener?.onItemClick(itemView, bindingAdapterPosition)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}