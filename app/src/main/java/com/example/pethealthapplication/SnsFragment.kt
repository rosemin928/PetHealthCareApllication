package com.example.pethealthapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethealthapplication.nicknameapi.NicknameHeaderInterceptor
import com.example.pethealthapplication.snsapi.SnsApiClient
import com.example.pethealthapplication.snsapi.SnsApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SnsFragment : Fragment() {
    private lateinit var snsAdapter: SnsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sns, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 게시물 작성 페이지로 이동하는 버튼 설정
        val writeButton = view.findViewById<RelativeLayout>(R.id.writeBtn)
        writeButton.setOnClickListener {
            val intent = Intent(requireContext(), SnsWriteActivity::class.java)
            startActivity(intent)
        }

        // 서버에서 게시글 데이터 가져오기
        fetchPosts(page = 0, size = 8)  // 첫 페이지, 8개 게시글 요청

        return view
    }

    private fun fetchPosts(page: Int, size: Int) {
        val apiService = SnsApiClient.getApiService(requireContext())

        lifecycleScope.launch {
            try {
                // 최신 게시글 페이징 요청
                val response = apiService.getLatestPosts(page, size)
                if (response.status == 200) {
                    snsAdapter = SnsAdapter(response.data)
                    recyclerView.adapter = snsAdapter
                } else {
                    Log.e("SnsFragment", "Error: ${response.message}")
                    Toast.makeText(requireContext(), "게시물 불러오기 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SnsFragment", "Error fetching posts", e)
                Toast.makeText(requireContext(), "게시물 불러오기 중 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }
}