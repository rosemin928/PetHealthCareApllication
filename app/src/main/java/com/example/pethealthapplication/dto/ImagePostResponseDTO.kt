package com.example.pethealthapplication.dto

data class ImagePostResponseDTO(
    val status: Int,
    val message: String,
    val data: List<GetImagePostDTO>  // 실제 게시글 데이터 목록
)
