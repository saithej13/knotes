package com.vst.knotes.MVVM.View.farmer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.Model.FarmerModel
import com.vst.knotes.databinding.ItemFarmerBinding

class FarmerAdapter(
    private val onEditClick: (FarmerModel) -> Unit,
    private val onDeleteClick: (FarmerModel) -> Unit
) : ListAdapter<FarmerModel, FarmerAdapter.FarmerViewHolder>(FarmerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val binding = ItemFarmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FarmerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FarmerViewHolder(private val binding: ItemFarmerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(farmer: FarmerModel) {
            binding.apply {
                textViewFarmerCode.text = farmer.farmerCode
                textViewFarmerName.text = farmer.farmerName
                textViewMobileNumber.text = farmer.mobileNumber
                textViewAddress.text = farmer.address
                textViewCreatedDate.text = "Created: ${farmer.createdDate}"
                
                // Show status indicator
                viewStatusIndicator.setBackgroundColor(
                    if (farmer.isActive) 
                        itemView.context.getColor(android.R.color.holo_green_light)
                    else 
                        itemView.context.getColor(android.R.color.holo_red_light)
                )
                
                // Show/hide inactive label
                textViewInactive.visibility = if (farmer.isActive) View.GONE else View.VISIBLE
                
                // Set click listeners
                buttonEdit.setOnClickListener { onEditClick(farmer) }
                buttonDelete.setOnClickListener { onDeleteClick(farmer) }
                
                // Handle item click for details
                root.setOnClickListener { 
                    // Could expand to show more details or navigate to farmer details
                }
            }
        }
    }
}

class FarmerDiffCallback : DiffUtil.ItemCallback<FarmerModel>() {
    override fun areItemsTheSame(oldItem: FarmerModel, newItem: FarmerModel): Boolean {
        return oldItem.farmerId == newItem.farmerId
    }

    override fun areContentsTheSame(oldItem: FarmerModel, newItem: FarmerModel): Boolean {
        return oldItem == newItem
    }
}