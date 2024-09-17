package com.example.pethealthapplication.diabetesapi

import com.example.pethealthapplication.dto.DiabetesAnalyzeDTO
import com.example.pethealthapplication.dto.DiabetesCheck2DTO
import com.example.pethealthapplication.dto.DiabetesCheckDTO
import com.example.pethealthapplication.dto.IsObesityDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigDecimal

interface DiabetesApiService {
    @GET("/diabetes-management/{petId}/daily-water-intake")
    fun dailyWaterIntakeCheck(
        @Path("petId") petId: String
    ): Call<ResponseDTO<BigDecimal>>

    @POST("/diabetes-management/{petId}/der-calories")
    fun derCaloriesCalculate(
        @Path("petId") petId: String,
        @Body isObesityDTO: IsObesityDTO
    ): Call<ResponseDTO<BigDecimal>>

    @POST("/diabetes-management/{petId}/check/risk")
    fun diabetesAnalyze(
        @Path("petId") petId: String,
        @Body diabetesAnalyzeDTO: DiabetesAnalyzeDTO
    ): Call<ResponseDTO<DiabetesCheckDTO>>

    @GET("/diabetes-management/{petId}/risk")
    fun diabetesCheck(
        @Path("petId") petId: String
    ): Call<ResponseDTO<DiabetesCheck2DTO>>
}