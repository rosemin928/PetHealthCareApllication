package com.example.pethealthapplication.snsapi

import android.content.Context
import com.example.pethealthapplication.nicknameapi.NicknameHeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SnsApiClient {
    private const val BASE_URL = "http://ec2-13-124-65-7.ap-northeast-2.compute.amazonaws.com:8080/"
    private var retrofit: Retrofit? = null

    private fun getClient(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(NicknameHeaderInterceptor(context)) // 기존 인터셉터 추가
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getApiService(context: Context): SnsApiService {
        return getClient(context).create(SnsApiService::class.java)
    }
}