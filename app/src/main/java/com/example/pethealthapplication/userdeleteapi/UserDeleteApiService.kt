package com.example.pethealthapplication.userdeleteapi

import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface UserDeleteApiService {
    @DELETE("/users/{userId}")
    fun deleteUser(
        @Path("userId") userId: String
    ): Call<ResponseDTO<Any>>
}