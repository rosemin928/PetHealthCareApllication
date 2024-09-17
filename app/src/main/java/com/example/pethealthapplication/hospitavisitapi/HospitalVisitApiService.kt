package com.example.pethealthapplication.hospitavisitapi

import com.example.pethealthapplication.dto.HospitalVisitDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface HospitalVisitApiService {
    @PATCH("/pets/{petId}/next-visit-date")
    fun hospitalVisitUpdate(
        @Path("petId") petId: String,
        @Body hospitalVisitDTO: HospitalVisitDTO
    ): Call<ResponseDTO<Any>>

    @PATCH("/pets/{petId}/next-visit-date/delete")
    fun hospitalVisitDelete(
        @Path("petId") petId: String
    ): Call<ResponseDTO<Any>>

    @GET("/pets/{petId}/next-visit-date")
    fun hospitalVisitCheck(
        @Path("petId") petId: String
    ): Call<ResponseDTO<Any>>
}