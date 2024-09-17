package com.example.pethealthapplication.dto

import java.math.BigDecimal

data class ObesityUpdateDTO(
    var obesityDegree: String,
    var recommendedCalories: BigDecimal,
    var weightCalRecommendedCalories: BigDecimal,
    var calRecommendedCaloriesDate: String
)
