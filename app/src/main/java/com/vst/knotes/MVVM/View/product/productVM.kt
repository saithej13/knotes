package com.vst.knotes.MVVM.View.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Repositories.ProductRepository
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.View.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class productVM (private val productRepository: ProductRepository): ViewModel(){
    val viewModelScope = CoroutineScope(Dispatchers.IO)
    private val _products = MutableLiveData<List<productModel>>()
    val products: LiveData<List<productModel>> get() = _products
    private val _categories = MutableLiveData<List<categoryModel>>()
    val categories: LiveData<List<categoryModel>> get() = _categories

    fun fetchAllProducts(STOREID: String, ZONEID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productsList = productRepository.getAllProducts(STOREID, ZONEID)
                _products.postValue(productsList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun fetchProductById(STOREID: String, ZONEID: String,ProductId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val product = productRepository.getproductById(STOREID,ZONEID,ProductId)
                _products.postValue(product)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insert(product: productModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productRepository.insert(product)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun update(product: productModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productRepository.update(product)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun delete(product: productModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                productRepository.delete(product)
                fetchAllProducts("1","1")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun fetchCategories(STOREID: String, ZONEID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val category = productRepository.getAllCategories(STOREID, ZONEID)
                _categories.postValue(category)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}