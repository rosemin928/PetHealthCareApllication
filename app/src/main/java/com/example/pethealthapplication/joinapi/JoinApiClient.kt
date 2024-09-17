package com.example.pethealthapplication.joinapi

import android.content.Context
import com.example.pethealthapplication.loginapi.HeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JoinApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // 서버의 URL로 변경

    private var retrofit: Retrofit? = null

    private fun getClient(): Retrofit {

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    val joinApiService: JoinApiService by lazy {
        getClient().create(JoinApiService::class.java)
    }
}