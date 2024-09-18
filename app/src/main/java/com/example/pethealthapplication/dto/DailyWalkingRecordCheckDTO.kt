package com.example.pethealthapplication.dto

data class DailyWalkingRecordCheckDTO(
    var recordDate: String,
    var targetWalkingTime: Short,
    var walkingIntensity: Char,
    var walkingTime: Short,
    var targetWalkingResult: Boolean
)
