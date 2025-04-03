package com.vst.knotes.MVVM.View.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.Repositories.ProductRepository

class ProductViewModelFactory(val productRepository: ProductRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == productVM::class.java) {
            return productVM(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}