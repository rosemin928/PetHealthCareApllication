package com.example.pethealthapplication.dto

import java.math.BigDecimal

data class PetProfileSummaryDTO(
    val petName: String,
    val age: Byte,
    val currentWeight: BigDecimal,
    val animalType: String
)
