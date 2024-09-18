package com.example.pethealthapplication.dto

data class DailyWalkingRecordUpdateDTO(
    var recordDate: String,
    var walkingIntensity: Char,
    var walkingTime: Short
)
