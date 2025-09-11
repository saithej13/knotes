package com.vst.knotes.utils

import com.vst.knotes.MVVM.Model.RateModel
import kotlin.math.max

class MilkPriceCalculator {
    
    companion object {
        const val RATE_TYPE_CM = "CM"
        const val RATE_TYPE_BM = "BM"
        
        /**
         * Calculate milk price based on rate type
         * CM Formula: (fat + snf) * rate
         * BM Formula: (fat * rate) - (maxsnf - snf) * penalty
         */
        fun calculatePrice(
            quantity: Double,
            fat: Double,
            snf: Double,
            rateModel: RateModel
        ): Double {
            return when (rateModel.rateType) {
                RATE_TYPE_CM -> calculateCMPrice(quantity, fat, snf, rateModel)
                RATE_TYPE_BM -> calculateBMPrice(quantity, fat, snf, rateModel)
                else -> 0.0
            }
        }
        
        /**
         * CM (Combined Milk) Price Calculation
         * Formula: quantity * (fat + snf) * rate
         */
        private fun calculateCMPrice(
            quantity: Double,
            fat: Double,
            snf: Double,
            rateModel: RateModel
        ): Double {
//            val fatRate = rateModel.fatRate ?: 0.0
//            val snfRate = rateModel.snfRate ?: 0.0

            // If separate rates for fat and snf
            //CM RATE FORMULA => (FAT + SNF) * RATE /100
            //BM RATE FORMULA => (RATE * FAT) - (9-MAXSNF)
//            val safeQuantity = if (quantity == 0.0) 1.0 else if(quantity == null) 1.0 else quantity

            var CalCulatedRate:Double  =0.0

                if (fat >= rateModel.minFat!! && fat <= rateModel.maxFat!!) {
                    if (snf >= rateModel.minSnf!! && snf <= rateModel.maxSnf!!) {
//                    return quantity * ((fat * rateModel.rate) + (maxsnf * snfRate))
                        CalCulatedRate = quantity * ((fat +snf) * ((rateModel.rate!!)/100))
                    }
                }

            // If single combined rate (backwards compatibility)
//            val combinedRate = rateModel.fatRate ?: 0.0//Commented by saiteja on 24-07-2025
//            return quantity * (fat + snf) * combinedRate
            return CalCulatedRate
        }
        
        /**
         * BM (Buffalo Milk) Price Calculation
         * Formula: quantity * [(fat * rate) - max(0, (maxsnf - snf) * penalty)]
         */
        private fun calculateBMPrice(
            quantity: Double,
            fat: Double,
            snf: Double,
            rateModel: RateModel
        ): Double {
//            val baseFatRate = rateModel.baseFatRate ?: 0.0
//            val maxSnf = rateModel.maxSnf ?: 8.0
//            val snfPenalty = rateModel.snfPenalty ?: 0.0
//
//            val fatAmount = fat * baseFatRate
//            val snfPenaltyAmount = if (snf < maxSnf) {
//                (maxSnf - snf) * snfPenalty
//            } else {
//                0.0
//            }
//
//            val pricePerLiter = max(0.0, fatAmount - snfPenaltyAmount)
//            return quantity * pricePerLiter
            //BM RATE FORMULA => (RATE * FAT) - (9-MAXSNF)
            var CalCulatedRate:Double  =0.0

            if (fat >= rateModel.minFat!! && fat <= rateModel.maxFat!!) {
                if (snf >= rateModel.minSnf!! && snf <= rateModel.maxSnf!!) {
//                    return quantity * ((fat * rateModel.rate) + (maxsnf * snfRate))
                    CalCulatedRate = quantity * (((fat*rateModel.rate!!) - (9.0-snf))/100)
                }
            }
            return CalCulatedRate
        }
        
        /**
         * Get rate per liter for display purposes
         */
        fun getRatePerLiter(
            fat: Double,
            snf: Double,
            rateModel: RateModel
        ): Double {
            return calculatePrice(1.0, fat, snf, rateModel)
        }
        
        /**
         * Validate SNF range (8.0 to 9.0 including decimals)
         */
        fun isValidSnfRange(snf: Double): Boolean {
            return snf in 8.0..9.0
        }
        
        /**
         * Format SNF to one decimal place
         */
        fun formatSnf(snf: Double): Double {
            return kotlin.math.round(snf * 10.0) / 10.0
        }
        
        /**
         * Get available SNF values (8.0, 8.1, 8.2, ... 8.9, 9.0)
         */
        fun getSnfOptions(): List<Double> {
            val snfList = mutableListOf<Double>()
            for (i in 80..90) {
                snfList.add(i / 10.0)
            }
            return snfList
        }
    }
}