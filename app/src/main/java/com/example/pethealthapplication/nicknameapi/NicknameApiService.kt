package com.example.pethealthapplication.nicknameapi

import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.UpdateNicknameDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NicknameApiService {
    @PATCH("/users/{userId}/nickname")
    fun changeNickname(
        @Path("userId") userId: String,
        @Body updateNicknameDTO: UpdateNicknameDTO
    ): Call<ResponseDTO<Any>>
}