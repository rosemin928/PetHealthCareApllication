package com.example.pethealthapplication.petprofileapi

import com.example.pethealthapplication.dto.PetProfileDTO
import com.example.pethealthapplication.dto.PetProfileUpdateDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PetProfileApiService {
    @POST("/users/{userId}/pet")
    fun petProfile(
        @Path("userId") userId: String,
        @Body petProfileDTO: PetProfileDTO
    ): Call<ResponseDTO<Any>>

    @GET("/pets/{petId}/profile")
    fun petProfileCheck(
        @Path("petId") petId: String
    ): Call<ResponseDTO<PetProfileDTO>>

    @PATCH("/pets/{petId}")
    fun petProfileUpdate(
        @Path("petId") petId: String,
        @Body petProfileUpdateDTO: PetProfileUpdateDTO
    ): Call<ResponseDTO<Any>>

    @DELETE("/pets/{petId}")
    fun petProfileDelete(
        @Path("petId") petId: String
    ): Call<ResponseDTO<Any>>
}