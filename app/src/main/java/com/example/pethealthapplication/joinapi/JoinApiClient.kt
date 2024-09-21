package com.example.pethealthapplication.joinapi

import android.content.Context
import com.example.pethealthapplication.loginapi.HeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JoinApiClient {
    private const val BASE_URL = "http://ec2-13-124-65-7.ap-northeast-2.compute.amazonaws.com:8080/" // 서버의 URL로 변경

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