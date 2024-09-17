package com.example.pethealthapplication.dailyreportapi

import com.example.pethealthapplication.dto.DailyReportDTO
import com.example.pethealthapplication.dto.PetProfileDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface DailyReportApiService {
    @POST("/pets/{petId}/daily-record")
    fun dailyReport(
        @Path("petId") petId: String,
        @Body dailyReportDTO: DailyReportDTO
    ): Call<ResponseDTO<Any>>

    @GET("/pets/{petId}/daily-record/{recordDate}")
    fun dailyReportCheck(
        @Path("petId") petId: String,
        @Path("recordDate") recordDate: String
    ): Call<ResponseDTO<DailyReportDTO>>

    @PATCH("/pets/{petId}/daily-record")
    fun dailyReportUpdate(
        @Path("petId") petId: String,
        @Body dailyReportDTO: DailyReportDTO
    ): Call<ResponseDTO<Any>>

    @DELETE("/pets/{petId}/daily-record/{recordDate}")
    fun dailyReportDelete(
        @Path("petId") petId: String,
        @Path("recordDate") recordDate: String
    ): Call<ResponseDTO<Any>>
}