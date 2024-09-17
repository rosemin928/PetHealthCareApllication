package com.example.pethealthapplication.dto

data class ResponseDTO<T>(
    var status: Int,
    var message: String,
    var data: T?
)
