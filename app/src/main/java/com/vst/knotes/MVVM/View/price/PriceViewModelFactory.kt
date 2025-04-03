package com.vst.knotes.MVVM.View.price

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.Repositories.PriceRepository

class PriceViewModelFactory(private val priceRepository: PriceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == priceVM::class.java) {
            return priceVM(priceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}