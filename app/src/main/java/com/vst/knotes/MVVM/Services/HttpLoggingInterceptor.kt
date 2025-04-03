package com.vst.knotes.MVVM.Services

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

class HttpLoggingInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.e("Request", "Method: ${request.method} URL: ${request.url}")
        if (request.body != null) {
            Log.e("Request", "Body: ${request.body!!.toString()}")
        }
        val response = chain.proceed(request)
        val responseBody = response.body
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer()
            val body = buffer.clone().readString(Charset.forName("UTF-8"))
            Log.e("Response", "Body: $body")
        }
        return response
    }
}