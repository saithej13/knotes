package com.vst.knotes.MVVM.Services

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.vst.knotes.MVVM.Services.AppConstants

object AppModule {
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        synchronized(this) {
            if (retrofit == null) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(TokenAuthInterceptor())
                    .addInterceptor(HttpLoggingInterceptor())
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
        }
        return retrofit!!
    }
}