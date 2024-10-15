package com.example.pethealthapplication.snsapi

import com.example.pethealthapplication.dto.CommentRequestDTO
import com.example.pethealthapplication.dto.CommentResponseDTO
import com.example.pethealthapplication.dto.ImagePostResponseDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.ResponseListDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
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

    @GET("/walking-post/post")
    suspend fun getLatestPosts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "desc"
    ): ImagePostResponseDTO

    @POST("/walking-post/{userId}/{walkingPostId}/comment")
    suspend fun submitComment(
        @Path("userId") userId: String,
        @Path("walkingPostId") walkingPostId: String,
        @Body comment: CommentRequestDTO // JSON 객체로 전송
    ): ResponseDTO<Any>

    @GET("/walking-post/{walkingPostId}/comment")
    suspend fun getComments(
        @Path("walkingPostId") walkingPostId: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "desc"
    ): Response<ResponseListDTO<CommentResponseDTO>>
}