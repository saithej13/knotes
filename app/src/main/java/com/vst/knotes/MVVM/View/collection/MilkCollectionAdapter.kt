package com.vst.knotes.MVVM.View.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.databinding.ItemMilkCollectionBinding
import com.vst.knotes.utils.MilkPriceCalculator

class MilkCollectionAdapter(
    private val onEditClick: (MilkCollectionWithFarmerModel) -> Unit,
    private val onDeleteClick: (MilkCollectionWithFarmerModel) -> Unit,
    private val onViewDetailsClick: (MilkCollectionWithFarmerModel) -> Unit
) : ListAdapter<MilkCollectionWithFarmerModel, MilkCollectionAdapter.CollectionViewHolder>(CollectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemMilkCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectionViewHolder(private val binding: ItemMilkCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(collectionWithFarmer: MilkCollectionWithFarmerModel) {
            val collection = collectionWithFarmer.collection
            val farmer = collectionWithFarmer.farmer
            
            binding.apply {
                // Farmer information
                textViewFarmerCode.text = farmer.farmerCode
                textViewFarmerName.text = farmer.farmerName
                
                // Collection details
                textViewCollectionDate.text = collection.collectionDate
                textViewCollectionTime.text = collection.collectionTime
                textViewQuantity.text = "${collection.quantity} L"
                textViewFat.text = "${collection.fat}%"
                textViewSnf.text = "${collection.snf}%"
                textViewRateType.text = collection.rateType
                textViewAmount.text = "₹${String.format("%.2f", collection.amount)}"
                textViewRatePerLiter.text = "₹${String.format("%.2f", collection.rate)}/L"
                
                // Rate type indicator color
                val indicatorColor = if (collection.rateType == MilkPriceCalculator.RATE_TYPE_CM) {
                    itemView.context.getColor(android.R.color.holo_blue_light)
                } else {
                    itemView.context.getColor(android.R.color.holo_green_light)
                }
                viewRateTypeIndicator.setBackgroundColor(indicatorColor)
                
                // Show/hide remarks
                if (!collection.remarks.isNullOrBlank()) {
                    textViewRemarks.text = collection.remarks
                    textViewRemarks.visibility = View.VISIBLE
                    textViewRemarksLabel.visibility = View.VISIBLE
                } else {
                    textViewRemarks.visibility = View.GONE
                    textViewRemarksLabel.visibility = View.GONE
                }
                
                // Set click listeners
                buttonEdit.setOnClickListener { onEditClick(collectionWithFarmer) }
                buttonDelete.setOnClickListener { onDeleteClick(collectionWithFarmer) }
                root.setOnClickListener { onViewDetailsClick(collectionWithFarmer) }
                
                // Calculate quality indicators
                showQualityIndicators(collection.fat, collection.snf)
            }
        }
        
        private fun showQualityIndicators(fat: Double, snf: Double) {
            binding.apply {
                // Fat quality indicator
                when {
                    fat >= 4.5 -> {
                        textViewFatQuality.text = "Excellent"
                        textViewFatQuality.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                    }
                    fat >= 4.0 -> {
                        textViewFatQuality.text = "Good"
                        textViewFatQuality.setTextColor(itemView.context.getColor(android.R.color.holo_blue_dark))
                    }
                    fat >= 3.5 -> {
                        textViewFatQuality.text = "Average"
                        textViewFatQuality.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                    }
                    else -> {
                        textViewFatQuality.text = "Low"
                        textViewFatQuality.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                    }
                }
                
                // SNF quality indicator
                when {
                    snf >= 8.8 -> {
                        textViewSnfQuality.text = "Excellent"
                        textViewSnfQuality.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                    }
                    snf >= 8.5 -> {
                        textViewSnfQuality.text = "Good"
                        textViewSnfQuality.setTextColor(itemView.context.getColor(android.R.color.holo_blue_dark))
                    }
                    snf >= 8.2 -> {
                        textViewSnfQuality.text = "Average"
                        textViewSnfQuality.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                    }
                    else -> {
                        textViewSnfQuality.text = "Low"
                        textViewSnfQuality.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                    }
                }
            }
        }
    }
}

class CollectionDiffCallback : DiffUtil.ItemCallback<MilkCollectionWithFarmerModel>() {
    override fun areItemsTheSame(oldItem: MilkCollectionWithFarmerModel, newItem: MilkCollectionWithFarmerModel): Boolean {
        return oldItem.collection.collectionId == newItem.collection.collectionId
    }

    override fun areContentsTheSame(oldItem: MilkCollectionWithFarmerModel, newItem: MilkCollectionWithFarmerModel): Boolean {
        return oldItem == newItem
    }
}