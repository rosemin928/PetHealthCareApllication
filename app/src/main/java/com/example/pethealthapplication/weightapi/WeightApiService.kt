package com.example.pethealthapplication.weightapi

import com.example.pethealthapplication.dto.CurrentWeightDTO
import com.example.pethealthapplication.dto.ObesityCheckDTO
import com.example.pethealthapplication.dto.ObesityUpdateDTO
import com.example.pethealthapplication.dto.RecommendedKcalCheckDTO
import com.example.pethealthapplication.dto.RecommendedKcalDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.TargetWeightDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigDecimal

interface WeightApiService {
    @PATCH("/pets/{petId}/target-weight")
    fun targetWeightUpdate(
        @Path("petId") petId: String,
        @Body targetWeightDTO: TargetWeightDTO
    ):Call<ResponseDTO<Any>>

    @GET("/pets/{petId}/target-weight")
    fun targetWeightCheck(
        @Path("petId") petId: String
    ):Call<ResponseDTO<BigDecimal>>

    @PATCH("/pets/{petId}/target-weight/delete")
    fun targetWeightDelete(
        @Path("petId") petId: String
    ):Call<ResponseDTO<Any>>

    @GET("/obesity-management/{petId}/current-weight")
    fun currentWeightCheck(
        @Path("petId") petId: String
    ):Call<ResponseDTO<BigDecimal>>

    @PATCH("/obesity-management/{petId}/current-weight")
    fun currentWeightChange(
        @Path("petId") petId: String,
        @Body currentWeightDTO: CurrentWeightDTO
    ):Call<ResponseDTO<Any>>

    @GET("/obesity-management/{petId}/check/standard-weight")
    fun standardWeightCheck(
        @Path("petId") petId: String
    ):Call<ResponseDTO<String>>

    @POST("/obesity-management/{petId}/calculate/daily-calorie")
    fun obesityCheck(
        @Path("petId") petId: String,
        @Body obesityCheckDTO: ObesityCheckDTO
    ):Call<ResponseDTO<RecommendedKcalDTO>>

    @PATCH("/obesity-management/{petId}/daily-calorie")
    fun obesityUpdate(
        @Path("petId") petId: String,
        @Body obesityUpdateDTO: ObesityUpdateDTO
    ):Call<ResponseDTO<Any>>

    @GET("/obesity-management/{petId}/daily-calorie")
    fun recommendedCaloriesCheck(
        @Path("petId") petId: String
    ):Call<ResponseDTO<RecommendedKcalCheckDTO>>

    @GET("/obesity-management/{petId}/weight-cal-recommended-calories")
    fun obesityWeightCheck(
        @Path("petId") petId: String
    ):Call<ResponseDTO<BigDecimal>>
}