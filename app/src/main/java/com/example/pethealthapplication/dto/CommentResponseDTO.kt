package com.example.pethealthapplication.dto

data class CommentResponseDTO(
    val walkingPostCommentId: String,
    val nickname: String,
    val walkingPostCommentDate: String,
    val walkingPostCommentTime: String,
    val comment: String
)
