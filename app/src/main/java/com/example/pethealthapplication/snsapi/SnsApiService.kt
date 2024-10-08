package com.example.pethealthapplication.snsapi

import com.example.pethealthapplication.dto.ResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SnsApiService {
    @Multipart
    @POST("/walking-post/{userId}/post")
    suspend fun postWalking(
        @Path("userId") userId: String,
        @Part("data") data: RequestBody,
        @Part image: MultipartBody.Part?
    ): ResponseDTO<Any>
}