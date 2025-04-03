package com.vst.knotes.MVVM.View.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.Repositories.categoryRepository

class CategoryViewModelFactory(private val categoryRepository: categoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the ViewModel is categoryVM class
        if (modelClass == categoryVM::class.java) {
            return categoryVM(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}