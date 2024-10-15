package com.example.pethealthapplication

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethealthapplication.dto.CommentRequestDTO
import com.example.pethealthapplication.dto.CommentResponseDTO
import com.example.pethealthapplication.snsapi.SnsApiClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private var walkingPostId: String? = null
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            walkingPostId = it.getString(ARG_WALKING_POST_ID)
        }
    }

    companion object {
        private const val ARG_WALKING_POST_ID = "walkingPostId"

        fun newInstance(walkingPostId: String): CommentBottomSheetFragment {
            val fragment = CommentBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_WALKING_POST_ID, walkingPostId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false)

        val submitButton = view.findViewById<Button>(R.id.submitButton)
        val commentEditText = view.findViewById<EditText>(R.id.commentEditText)
        val recyclerView = view.findViewById<RecyclerView>(R.id.commentRecyclerView)
        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager

        // 어댑터 초기화
        commentAdapter = CommentAdapter(emptyList())
        recyclerView.adapter = commentAdapter

        // 댓글 데이터를 서버에서 불러오기
        walkingPostId?.let { id -> fetchComments(id) }

        // 댓글 입력란 감시자 설정
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val commentEditTextString = commentEditText.text.toString()
                submitButton.isEnabled = commentEditTextString.isNotEmpty()
            }
        }
        commentEditText.addTextChangedListener(textWatcher)

        // 등록 버튼 클릭 리스너
        submitButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                submitComment(commentText, commentEditText)
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        // BottomSheet 크기 조절
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val bottomSheetBehavior = BottomSheetBehavior.from(it)
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            it.layoutParams.height = (screenHeight * 0.7).toInt()
            it.requestLayout()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    // 댓글 작성 메서드
    private fun submitComment(comment: String, commentEditText: EditText) {
        val postId = walkingPostId ?: run {
            Toast.makeText(context, "Invalid post ID", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID_KEY", null) ?: run {
            Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = SnsApiClient.getApiService(requireContext())

        lifecycleScope.launch {
            try {
                val commentRequest = CommentRequestDTO(true, comment)
                val response = apiService.submitComment(userId = userId, walkingPostId = postId, comment = commentRequest)

                if (response.status == 200) {
                    Toast.makeText(context, "댓글 작성 완료!", Toast.LENGTH_SHORT).show()
                    fetchComments(postId)  // 댓글 목록 갱신
                    commentEditText.text.clear()  // 입력란 초기화
                } else {
                    Toast.makeText(context, "댓글 작성 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CommentBottomSheet", "Error submitting comment", e)
                Toast.makeText(context, "댓글 작성 중 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 댓글 데이터를 서버에서 가져오는 함수
    private fun fetchComments(postId: String) {
        val apiService = SnsApiClient.getApiService(requireContext())

        lifecycleScope.launch {
            try {
                // 여기서 postId를 사용하여 댓글을 가져오는 요청
                val response = apiService.getComments(page = 0, size = 20, walkingPostId = postId)

                // 응답이 성공적일 경우
                if (response.isSuccessful) {
                    val comments = response.body()?.data ?: emptyList()
                    updateRecyclerView(comments)
                }else if (response.code() == 404) {
                    // 댓글이 없을 경우, 빈 공간만 보여줍니다
                    updateRecyclerView(emptyList())
                } else {
                    // 응답이 실패했을 경우 처리
                    Log.e("CommentBottomSheet", "Failed to fetch comments: ${response.message()}")
                    Toast.makeText(context, "댓글을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // 네트워크 오류 또는 다른 예외 처리
                Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // RecyclerView 업데이트 함수
    private fun updateRecyclerView(comments: List<CommentResponseDTO>) {
        commentAdapter.updateComments(comments)
        view?.findViewById<RecyclerView>(R.id.commentRecyclerView)?.scrollToPosition(commentAdapter.itemCount - 1)
    }
}