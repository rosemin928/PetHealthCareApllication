package com.example.pethealthapplication.joinapi

import com.example.pethealthapplication.dto.JoinDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinApiService {
    @POST("/join")
    fun join(@Body joinDTO: JoinDTO): Call<ResponseDTO<Any>>
}