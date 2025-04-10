package com.vst.knotes.MVVM.Services

import com.vst.knotes.Utils.MyApplication
import com.vst.knotes.Utils.Preference
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class TokenAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val accessToken = Preference(MyApplication.context).getStringFromPreference("token","")
        if (!accessToken.isNullOrEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401 && request.url.encodedPath != "/refresh") {
            response.close()
            val refreshToken = Preference(MyApplication.context).getStringFromPreference("refresh_token","")
            if (!refreshToken.isNullOrEmpty()) {
                try {
                    val newTokens = refreshAccessToken(refreshToken)
                    if (newTokens != null) {
                        Preference(MyApplication.context).saveStringInPreference("token",newTokens.accessToken)
                        Preference(MyApplication.context).saveStringInPreference("refresh_token",newTokens.accessToken)

                        val newRequest = request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()

                        return chain.proceed(newRequest)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            } else {

            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): AuthTokens? {
        val client = OkHttpClient()
        val json = """
            {
                "refresh_token": "$refreshToken"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("${AppConstants.BASE_URL}/refresh")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body?.string() ?: "")
                val auth = jsonObject.optJSONObject("authorisation")
                val accessToken = auth?.optString("access_token")
                val newRefreshToken = auth?.optString("refresh_token")

                if (!accessToken.isNullOrEmpty() && !newRefreshToken.isNullOrEmpty()) {
                    return AuthTokens(accessToken, newRefreshToken)
                }
            }
        }

        throw Exception("Failed to refresh token")
    }

    data class AuthTokens(val accessToken: String, val refreshToken: String)
}