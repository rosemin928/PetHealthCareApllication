package com.example.pethealthapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pethealthapplication.dto.GetImagePostDTO
import com.bumptech.glide.Glide

class SnsAdapter(private val posts: List<GetImagePostDTO>) : RecyclerView.Adapter<SnsAdapter.SnsViewHolder>() {

    class SnsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.postTitle)
        val content: TextView = view.findViewById(R.id.postContent)
        val imageView: ImageView = view.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sns_post, parent, false)
        return SnsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnsViewHolder, position: Int) {
        val post = posts[position]
        holder.title.text = post.nickname  // 제목 대신 사용자 닉네임 표시
        holder.content.text = post.content  // 게시글 내용 표시

        // 이미지 URL을 사용하여 이미지 로드 (Glide 사용 예시)
        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount() = posts.size
}
