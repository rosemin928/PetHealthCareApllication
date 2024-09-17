package com.example.pethealthapplication.nicknameapi

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NicknameHeaderInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val token = getTokenFromPrefs()
        val newRequest: Request = originalRequest.newBuilder()
            .header("Authorization", "$token") //token이 이미 Bearer가 붙어서 오기 때문에 Bearer를 붙일 필요 없음
            .build()
        return chain.proceed(newRequest)
    }

    private fun getTokenFromPrefs(): String {
        // SharedPreferences에서 토큰을 가져오는 로직을 구현
        return context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).getString("TOKEN_KEY", "") ?: ""
    }
}