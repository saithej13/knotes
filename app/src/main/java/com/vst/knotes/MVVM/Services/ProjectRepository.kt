package com.vst.knotes.MVVM.Services

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectRepository(context: Context) {
//    Apiclient apiClient = AppModule.getClient().create(Apiclient.class);
    val apiClient = AppModule.getClient().create(Apiclient::class.java)

    fun getServices(): MutableLiveData<JsonObject?> {
        val data = MutableLiveData<JsonObject?>()
        apiClient.Login().enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    data.setValue(response.body())
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                data.setValue(null)
            }
        })
        return data
    }
    fun login(payload: JsonObject): MutableLiveData<JsonObject?> {
        val data = MutableLiveData<JsonObject?>()
        apiClient.Login().enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    data.setValue(response.body())
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                data.setValue(null)
            }
        })
        return data
    }

}