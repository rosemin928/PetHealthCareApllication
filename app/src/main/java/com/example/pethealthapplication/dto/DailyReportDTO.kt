package com.example.pethealthapplication.dto

import java.math.BigDecimal
import java.time.LocalDate

data class DailyReportDTO(
    var recordDate: String,
    var diagnosis: String?,
    var medicine: String?,
    var weight: BigDecimal?,
    var bloodSugarLevel: Short?,
    var specialNote: String?
)
