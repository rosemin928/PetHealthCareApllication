package com.example.pethealthapplication.graph

import com.example.pethealthapplication.dto.BloodSugarLevelDTO
import com.example.pethealthapplication.dto.ResponseListDTO
import com.example.pethealthapplication.dto.PetWeightDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GraphApiService {
    @GET("/pets/{petId}/blood-sugar-level/recent/7")
    fun bloodSugarGraph(
        @Path("petId") petId: String
    ): Call<ResponseListDTO<BloodSugarLevelDTO>>

    @GET("pets/{petId}/weight/recent/7")
    fun weightGraph(
        @Path("petId") petId: String
    ): Call<ResponseListDTO<PetWeightDTO>>
}