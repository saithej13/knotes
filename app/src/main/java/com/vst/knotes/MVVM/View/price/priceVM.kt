package com.vst.knotes.MVVM.View.price

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Repositories.PriceRepository
import com.vst.knotes.MVVM.Model.pricingModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.Repositories.categoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class priceVM (private val priceRepository: PriceRepository): ViewModel(){
    val viewModelScope = CoroutineScope(Dispatchers.IO)
    private val _prices = MutableLiveData<List<pricingModel>>()
    val prices: LiveData<List<pricingModel>> get() = _prices

    private val _products = MutableLiveData<List<productModel>>()
    val products: LiveData<List<productModel>> get() = _products

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    fun fetchAllPrices(STOREID: String, ZONEID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pricesList = priceRepository.getarrprices(STOREID, ZONEID)
                _prices.postValue(pricesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun fetchPricesById(STOREID: String, ZONEID: String,PRODUCTID:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pricesList = priceRepository.getarrpriceById(STOREID,ZONEID,PRODUCTID)
                _prices.postValue(pricesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun insert(price: pricingModel) {
        viewModelScope.launch {
            try {
                priceRepository.insert(price)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }
    fun update(price: pricingModel) {
        viewModelScope.launch {
            try {
                priceRepository.update(price)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }
    fun delete(price: pricingModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                priceRepository.delete(price)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchProducts(storeid: String, zoneid: String) {
        viewModelScope.launch {
            val productList = priceRepository.getProducts(storeid, zoneid)
            _products.postValue(productList)
        }
    }
}