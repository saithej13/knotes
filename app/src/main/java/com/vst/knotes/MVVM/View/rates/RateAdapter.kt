package com.vst.knotes.MVVM.View.rates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.MVVM.Model.RateModel
import com.vst.knotes.databinding.ItemRateBinding
import com.vst.knotes.utils.MilkPriceCalculator

class RateAdapter(
    private val onEditClick: (RateModel) -> Unit,
    private val onDeactivateClick: (RateModel) -> Unit,
    private val onTestClick: (RateModel) -> Unit
) : ListAdapter<RateModel, RateAdapter.RateViewHolder>(RateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val binding = ItemRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RateViewHolder(private val binding: ItemRateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rate: RateModel) {
            binding.apply {
                textViewRateType.text = rate.rateType
                textViewEffectiveDate.text = "Effective From: ${rate.startDate} To  ${rate.endDate}"
                
                when (rate.rateType) {
                    MilkPriceCalculator.RATE_TYPE_CM -> {
//                        textViewRateDetails.text = "Fat: ₹${rate.fatRate}/% | SNF: ₹${rate.snfRate}/%"//commented by saiteja on 24-07-2025
//                        textViewRateFormula.text = "Formula: (Fat × ${rate.fatRate}) + (SNF × ${rate.snfRate})"
                        cardRate.setCardBackgroundColor(itemView.context.getColor(android.R.color.holo_blue_light))
                    }
                    MilkPriceCalculator.RATE_TYPE_BM -> {
                        textViewRateDetails.text = "Fat: ₹${rate.baseFatRate}/% | Max SNF: ${rate.maxSnf}% | Penalty: ₹${rate.snfPenalty}/%"
                        textViewRateFormula.text = "Formula: (Fat × ${rate.baseFatRate}) - (${rate.maxSnf} - SNF) × ${rate.snfPenalty}"
                        cardRate.setCardBackgroundColor(itemView.context.getColor(android.R.color.holo_green_light))
                    }
                }
                
                // Show creation and update dates
                textViewCreatedDate.text = "Created: ${rate.createdDate}"
                rate.updatedDate?.let { 
                    textViewUpdatedDate.text = "Updated: $it"
                    textViewUpdatedDate.visibility = android.view.View.VISIBLE
                } ?: run {
                    textViewUpdatedDate.visibility = android.view.View.GONE
                }
                
                // Set click listeners
                buttonEdit.setOnClickListener { onEditClick(rate) }
                buttonDeactivate.setOnClickListener { onDeactivateClick(rate) }
                buttonTest.setOnClickListener { onTestClick(rate) }
                
                // Handle item click for details
                root.setOnClickListener { 
                    // Could expand to show more details
                }
            }
        }
    }
}

class RateDiffCallback : DiffUtil.ItemCallback<RateModel>() {
    override fun areItemsTheSame(oldItem: RateModel, newItem: RateModel): Boolean {
        return oldItem.rateId == newItem.rateId
    }

    override fun areContentsTheSame(oldItem: RateModel, newItem: RateModel): Boolean {
        return oldItem == newItem
    }
}