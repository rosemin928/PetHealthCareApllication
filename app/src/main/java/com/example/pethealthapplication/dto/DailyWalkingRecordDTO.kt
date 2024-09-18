package com.example.pethealthapplication.dto

data class DailyWalkingRecordDTO(
    var recordDate: String,
    var targetWalkingTime: Short,
    var walkingIntensity: Char,
    var walkingTime: Short
)
