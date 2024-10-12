package com.example.pethealthapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethealthapplication.nicknameapi.NicknameHeaderInterceptor
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
        fetchPosts()

        return view
    }

    private fun fetchPosts() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(NicknameHeaderInterceptor(requireContext())) // 인터셉터 추가
            .build()

        val apiService = Retrofit.Builder()
            .baseUrl("http://ec2-13-124-65-7.ap-northeast-2.compute.amazonaws.com:8080/")
            .client(okHttpClient)  // OkHttpClient를 추가
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SnsApiService::class.java)

        lifecycleScope.launch {
            try {
                // 최신 8개 게시글 불러오기
                val response = apiService.getLatestPosts()
                if (response.status == 200) {
                    snsAdapter = SnsAdapter(response.data)
                    recyclerView.adapter = snsAdapter
                } else {
                    Log.e("SnsFragment", "Error: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("SnsFragment", "Error fetching posts", e)
            }
        }
    }

}
