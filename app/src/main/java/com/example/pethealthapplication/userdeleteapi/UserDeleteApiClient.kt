package com.example.pethealthapplication.userdeleteapi

import android.content.Context
import com.example.pethealthapplication.nicknameapi.NicknameApiClient
import com.example.pethealthapplication.nicknameapi.NicknameApiService
import com.example.pethealthapplication.nicknameapi.NicknameHeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserDeleteApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080"
    private var retrofit: Retrofit? = null

    private fun getClient(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(NicknameHeaderInterceptor(context))
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(UserDeleteApiClient.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getApiService(context: Context): UserDeleteApiService {
        return getClient(context).create(UserDeleteApiService::class.java)
    }
}