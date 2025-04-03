package com.vst.knotes.MVVM.View.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vst.knotes.MVVM.Repositories.categoryRepository
import com.vst.knotes.MVVM.Model.categoryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class categoryVM(private val categoryRepository: categoryRepository): ViewModel() {
    val viewModelScope = CoroutineScope(Dispatchers.IO)
    private val _categories = MutableLiveData<List<categoryModel>>()
    val categories: LiveData<List<categoryModel>> get() = _categories

    fun fetchAllCategories(STOREID: String, ZONEID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categoriesList = categoryRepository.getAllCategories(STOREID, ZONEID)
                _categories.postValue(categoriesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchCategoryById(CategoryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val category = categoryRepository.getCategoryById(CategoryId)
                _categories.postValue(category)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insert(category: categoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categoryRepository.insert(category)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun update(category: categoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categoryRepository.update(category)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun delete(category: categoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categoryRepository.delete(category)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}