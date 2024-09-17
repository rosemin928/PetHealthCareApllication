package com.example.pethealthapplication.dto

data class ResponseListDTO<T>(
    var status: Int,
    var message: String,
    var data: List<T>?
)
