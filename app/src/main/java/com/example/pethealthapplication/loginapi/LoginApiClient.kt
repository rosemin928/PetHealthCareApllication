package com.example.pethealthapplication.loginapi

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // 서버의 URL로 변경

    private var retrofit: Retrofit? = null

    private fun getClient(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(context)) // Context를 전달
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // OkHttpClient 적용
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getApiService(context: Context): LoginApiService {
        return getClient(context).create(LoginApiService::class.java)
    }
}
