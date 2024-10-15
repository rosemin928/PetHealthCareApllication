package com.example.pethealthapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pethealthapplication.dto.CommentResponseDTO

class CommentAdapter(private var comments: List<CommentResponseDTO>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nicknameTextView: TextView = view.findViewById(R.id.nickname)
        val commentTextView: TextView = view.findViewById(R.id.comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sns_comments, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.nicknameTextView.text = comment.nickname
        holder.commentTextView.text = comment.comment
    }

    override fun getItemCount(): Int = comments.size

    // 댓글 목록을 업데이트하는 메서드
    fun updateComments(newComments: List<CommentResponseDTO>) {
        comments = newComments
        notifyDataSetChanged() // 전체 데이터를 새로 고침
    }
}

