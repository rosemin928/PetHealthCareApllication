package com.example.pethealthapplication.dto

data class ObesityCheckDTO(
    var weightStatus: String,
    var waistRibVisibility: Char?,
    var ribTouchability: Char?,
    var bodyShape: Int?,
    var activityLevel: String?
)
