package com.example.pethealthapplication.dailyreportapi

import android.content.Context
import com.example.pethealthapplication.nicknameapi.NicknameHeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DailyReportApiClient {
    private const val BASE_URL = "http://ec2-13-124-65-7.ap-northeast-2.compute.amazonaws.com:8080/" // 서버의 URL로 변경

    private var retrofit: Retrofit? = null

    private fun getClient(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(NicknameHeaderInterceptor(context)) // Context를 전달
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

    fun getApiService(context: Context): DailyReportApiService {
        return getClient(context).create(DailyReportApiService::class.java)
    }
}