package com.example.pethealthapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.widget.AppCompatButton


class RecommendFragment : Fragment() {

    private lateinit var allItems: MutableList<FeedItem>
    private lateinit var adapter: FeedAdapter
    private var selectedButton: AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommend, container, false)



        // 사료 리스트 데이터 초기화
        allItems = mutableListOf(
            FeedItem("조인트헬스", "19,000원", "칼슘", R.drawable.feed_joint_health),
            FeedItem("슬림앤조인트케어", "42,700원", "칼슘", R.drawable.feed_slim_and_joint_care),
            FeedItem("아카나 헤리티지 - 라이트 앤 피트", "31,500원", "단백질", R.drawable.feed_acana_heritage),
            FeedItem("건강백서 건강한 장", "20,000원", "유산균", R.drawable.feed_gungang),
            FeedItem("오리젠 오리지널 독", "41,000원", "단백질", R.drawable.feed_origen_original),
            FeedItem("지위픽 독 사슴고기", "124,000원", "단백질", R.drawable.feed_ziwi_peak),
            FeedItem("도그퓨어 위장건강 혼합유산균", "21,000원", "유산균", R.drawable.feed_dog_pure),
            FeedItem("고사료 솔류션 다이제스천 연어", "13,500원", "유산균", R.drawable.feed_go),
            FeedItem("빅스비 러블 동결건조 치킨&연어", "91,000원", "단백질", R.drawable.feed_bixbi_rawbble),
            FeedItem("아투 퍼피 연어", "42,000원", "단백질", R.drawable.feed_attu_puppy)
        )




        // 리스트뷰 초기화 및 어댑터 설정
        val listView: ListView = view.findViewById(R.id.feedListView)
        adapter = FeedAdapter(requireContext(), allItems.toMutableList())
        listView.adapter = adapter




        // 영양 성분 필터 버튼
        val calciumButton: AppCompatButton = view.findViewById(R.id.calcium)
        val probioticsButton: AppCompatButton = view.findViewById(R.id.probiotics)
        val proteinButton: AppCompatButton = view.findViewById(R.id.protein)

        calciumButton.setOnClickListener {
            handleButtonClick(calciumButton, "칼슘")
        }
        probioticsButton.setOnClickListener {
            handleButtonClick(probioticsButton, "유산균")
        }
        proteinButton.setOnClickListener {
            handleButtonClick(proteinButton, "단백질")
        }



        //검색 필터 구현
        val searchEditText: EditText = view.findViewById(R.id.search)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterItemsBySearch(s.toString())
            }
        })

        return view
    }



    //버튼 중복 클릭 불가
    private fun handleButtonClick(button: AppCompatButton, nutrition: String) {
        if (button == selectedButton) {
            button.isSelected = false
            selectedButton = null
            filterItemsByNutrition("")
        }
        else {
            selectedButton?.isSelected = false
            button.isSelected = true
            selectedButton = button
            filterItemsByNutrition(nutrition)
        }
    }




    //버튼 필터링
    private fun filterItemsByNutrition(nutrition: String) {
        val filteredItems = allItems.filter { it.nutrition.contains(nutrition) }
        adapter.clear()
        adapter.addAll(filteredItems)
        adapter.notifyDataSetChanged()
    }



    //검색 필터링
    private fun filterItemsBySearch(search: String) {
        val filteredItems = allItems.filter { it.name.contains(search, ignoreCase = true) }
        adapter.clear()
        adapter.addAll(filteredItems)
        adapter.notifyDataSetChanged()
    }
}