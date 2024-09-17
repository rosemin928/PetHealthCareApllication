package com.example.pethealthapplication.petprofilesummary

import com.example.pethealthapplication.dto.PetProfileSummaryDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PetProfileSummaryApiService {
    @GET("/users/{userId}/petId")
    fun getPetIdByUserId(
        @Path("userId") userId: String
    ): Call<ResponseDTO<List<String>>>

    @GET("/pets/{petId}/profile/summary")
    fun petProfileSummary(
        @Path("petId") petId: String,
    ): Call<ResponseDTO<PetProfileSummaryDTO>>
}