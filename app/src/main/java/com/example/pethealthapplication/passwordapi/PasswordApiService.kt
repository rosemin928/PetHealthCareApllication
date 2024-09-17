package com.example.pethealthapplication.passwordapi

import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.UpdateNicknameDTO
import com.example.pethealthapplication.dto.UpdatePasswordDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PasswordApiService {
    @PATCH("/users/{userId}/password")
    fun changePassword(
        @Path("userId") userId: String,
        @Body updatePasswordDTO: UpdatePasswordDTO
    ): Call<ResponseDTO<Any>>
}