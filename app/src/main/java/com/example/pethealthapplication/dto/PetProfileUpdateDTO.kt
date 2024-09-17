package com.example.pethealthapplication.dto

import java.math.BigDecimal

data class PetProfileUpdateDTO(
    var breed: String,
    var gender: Char,
    var age: Byte,
    var currentWeight: BigDecimal,
    var isNeutered: Boolean,
    var hasDiabetes: Boolean,
    var insulinTime1: String,
    var insulinTime2: String,
    var insulinTime3: String,
    var heartwormShotDate: String,
    var heartwormMedicineDate: String
)
