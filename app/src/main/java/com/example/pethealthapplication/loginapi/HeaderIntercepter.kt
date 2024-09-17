package com.example.pethealthapplication.loginapi

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val authorizationHeader = response.header("Authorization")
        val userIdHeader = response.header("userId")

        if (authorizationHeader != null && userIdHeader != null) {
            val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("TOKEN_KEY", authorizationHeader)
            editor.putString("USER_ID_KEY", userIdHeader)
            editor.apply()
        }

        return response
    }
}



