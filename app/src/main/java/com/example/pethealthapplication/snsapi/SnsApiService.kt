package com.example.pethealthapplication.snsapi

import com.example.pethealthapplication.dto.ImagePostResponseDTO
import com.example.pethealthapplication.dto.ResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface SnsApiService {
    @Multipart
    @POST("/walking-post/{userId}/post")
    suspend fun postWalking(
        @Path("userId") userId: String,
        @Part("data") data: RequestBody,
        @Part image: MultipartBody.Part?
    ): ResponseDTO<Any>

    @GET("/walking-post/post")  // 예시: 게시글을 가져오는 엔드포인트
    suspend fun getLatestPosts(
        @Query("limit") limit: Int = 8,  // 최신 8개의 게시글만 요청
        @Query("sort") sort: String = "desc"  // 최신순 정렬
    ): ImagePostResponseDTO
}