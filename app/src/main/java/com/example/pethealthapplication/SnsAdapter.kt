package com.example.pethealthapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pethealthapplication.dto.GetImagePostDTO
import com.bumptech.glide.Glide

class SnsAdapter(
    private val posts: List<GetImagePostDTO>
) : RecyclerView.Adapter<SnsAdapter.SnsViewHolder>() {

    class SnsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.postTitle)
        val content: TextView = view.findViewById(R.id.postContent)
        val imageView: ImageView = view.findViewById(R.id.postImage)
        val commentButton: ImageView = view.findViewById(R.id.commentBtn)
        val imageBox: RelativeLayout = view.findViewById(R.id.imageBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sns_post, parent, false)
        return SnsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnsViewHolder, position: Int) {
        val post = posts[position]
        holder.title.text = post.nickname  // 사용자 닉네임 표시
        holder.content.text = post.content  // 게시글 내용 표시

        // 이미지 URL이 비어있지 않은 경우에만 이미지를 로드
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.imageView.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.imageUrl)
                .into(holder.imageView)
        } else {
            // 이미지 URL이 없으면 ImageView 숨기기
            holder.imageView.visibility = View.GONE
            holder.imageBox.visibility = View.GONE
        }

        // 댓글 버튼 클릭 리스너 설정
        holder.commentButton.setOnClickListener {
            // 댓글 창을 열기 위한 BottomSheetDialogFragment 호출
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val commentDialog = CommentBottomSheetFragment.newInstance(post.walkingPostId)
            commentDialog.show(fragmentManager, "CommentBottomSheetDialog")
        }
    }


    override fun getItemCount() = posts.size
}
