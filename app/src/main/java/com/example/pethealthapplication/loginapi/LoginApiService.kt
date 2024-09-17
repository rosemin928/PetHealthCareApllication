package com.example.pethealthapplication.loginapi

import com.example.pethealthapplication.dto.LoginDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("/login")
    fun login(@Body loginDTO: LoginDTO): Call<Void>
}