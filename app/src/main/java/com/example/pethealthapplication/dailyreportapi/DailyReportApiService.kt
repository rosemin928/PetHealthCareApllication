package com.example.pethealthapplication.dailyreportapi

import com.example.pethealthapplication.dto.DailyReportDTO
import com.example.pethealthapplication.dto.DailyWalkingRecordCheckDTO
import com.example.pethealthapplication.dto.DailyWalkingRecordDTO
import com.example.pethealthapplication.dto.DailyWalkingRecordUpdateDTO
import com.example.pethealthapplication.dto.PetProfileDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.ResponseListDTO
import com.example.pethealthapplication.dto.TargetWalkingTimeDTO
import com.example.pethealthapplication.dto.WalkingScheduleDTO
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

    @PATCH("/walking-management/{petId}/current-target-walking-time")
    fun targetWalkingTime(
        @Path("petId") petId: String,
        @Body targetWalkingTimeDTO: TargetWalkingTimeDTO
    ): Call<ResponseDTO<Any>>

    @PATCH("/walking-management/{petId}/walking-schedule")
    fun walkingSchedule(
        @Path("petId") petId: String,
        @Body walkingScheduleDTO: WalkingScheduleDTO
    ): Call<ResponseDTO<Any>>

    @PATCH("/walking-management/{petId}/current-target-walking-time/delete")
    fun targetWalkingTimeDelete(
        @Path("petId") petId: String
    ): Call<ResponseDTO<Any>>

    @PATCH("/walking-management/{petId}/walking-schedule/delete")
    fun walkingScheduleDelete(
        @Path("petId") petId: String
    ): Call<ResponseDTO<Any>>

    @GET("/walking-management/{petId}/current-target-walking-time")
    fun targetWalkingTimeCheck(
        @Path("petId") petId: String
    ): Call<ResponseDTO<Short>>

    @GET("/walking-management/{petId}/walking-schedule")
    fun walkingScheduleCheck(
        @Path("petId") petId: String
    ): Call<ResponseDTO<String>>

    @POST("/walking-management/{petId}/daily-walking-record")
    fun dailyWalkingRecordAdd(
        @Path("petId") petId: String,
        @Body dailyWalkingRecordDTO: DailyWalkingRecordDTO
    ): Call<ResponseDTO<Any>>

    @GET("/walking-management/{petId}/daily-walking-record/{recordDate}")
    fun dailyWalkingRecordCheck(
        @Path("petId") petId: String,
        @Path("recordDate") recordDate: String
    ): Call<ResponseDTO<DailyWalkingRecordCheckDTO>>

    @DELETE("/walking-management/{petId}/daily-walking-record/{recordDate}")
    fun dailyWalkingRecordDelete(
        @Path("petId") petId: String,
        @Path("recordDate") recordDate: String
    ): Call<ResponseDTO<Any>>

    @PATCH("/walking-management/{petId}/daily-walking-record")
    fun dailyWalkingRecordUpdate(
        @Path("petId") petId: String,
        @Body dailyWalkingRecordUpdateDTO: DailyWalkingRecordUpdateDTO
    ): Call<ResponseDTO<Any>>
}