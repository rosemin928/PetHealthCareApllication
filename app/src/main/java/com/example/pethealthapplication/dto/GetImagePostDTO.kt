package com.example.pethealthapplication.dto

data class GetImagePostDTO(
    val walkingPostId: String,
    val nickname: String,
    val walkingPostDate: String,  // 서버에서 "LocalDate" 타입을 문자열로 받아옴
    val walkingPostTime: String,  // "LocalTime" 타입을 문자열로 받아옴
    val content: String,
    val imageUrl: String
)
