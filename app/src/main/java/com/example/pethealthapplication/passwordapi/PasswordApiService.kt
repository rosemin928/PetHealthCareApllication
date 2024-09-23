package com.example.pethealthapplication.passwordapi

import com.example.pethealthapplication.dto.PasswordCheckDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.UpdatePasswordDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PasswordApiService {
    @PATCH("/users/{userId}/password")
    fun changePassword(
        @Path("userId") userId: String,
        @Body updatePasswordDTO: UpdatePasswordDTO
    ): Call<ResponseDTO<Any>>

    @POST("/users/check/password")
    fun passwordCheck(
        @Body passwordCheckDTO: PasswordCheckDTO
    ): Call<ResponseDTO<String>>
}