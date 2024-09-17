package com.example.pethealthapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.pethealthapplication.databinding.ActivityMainBinding
import com.example.pethealthapplication.diabetesapi.DiabetesApiClient
import com.example.pethealthapplication.diabetesapi.DiabetesApiService
import com.example.pethealthapplication.dto.DiabetesCheck2DTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var isDouble = false //더블 클릭하여 뒤로가기를 하기 위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Intent로부터 값을 받아옴
        val directToAnalysis2 = intent.getBooleanExtra("SHOW_ANALYSIS2_FRAGMENT", false)

        if (directToAnalysis2) {
            // 바로 analysis2Fragment를 보여줌
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, Analysis2Fragment())
                .commit()
        } else {
            // BottomNavigationView 설정 및 다른 로직을 실행
            setBottomNavigationView()
        }

        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.homeFragment
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed() //메인화면에서 다시 가입화면으로 넘어갈 수 없게 하기 위해 뒤로가기 버튼 막음
        if (isDouble==true) {
            finishAffinity()
        }

        isDouble = true
        Toast.makeText(this, "종료하시려면 더블 클릭해주세요", Toast.LENGTH_LONG).show()

        Handler().postDelayed({
            isDouble = false
        }, 2000) //2초가 지나면 isDouble을 다시 false로 바꾼다
    }

    fun setBottomNavigationView() {

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val diabetesApiService = DiabetesApiClient.getApiService(this)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container,
                        HomeFragment()
                    ).commit()
                    true
                }
                R.id.recommendFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container,
                        RecommendFragment()).commit()
                    true
                }
                R.id.analysisFragment -> {
                    // 조회 API 호출
                    checkDiabetesData(petId ?: "", diabetesApiService) { showAnalysisFragment ->
                        if (showAnalysisFragment) {
                            // 하나라도 null이면 analysis1Fragment로 이동
                            supportFragmentManager.beginTransaction().replace(R.id.main_container,
                                Analysis1Fragment()
                            ).commit()
                        } else {
                            // 둘 다 null이 아니면 analysis2Fragment로 이동
                            supportFragmentManager.beginTransaction().replace(R.id.main_container,
                                Analysis2Fragment()
                            ).commit()
                        }
                    }
                    true
                }
                R.id.myPageFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container,
                        MyPageFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }

    // 데이터 조회 후 콜백 함수로 null 체크
    private fun checkDiabetesData(petId: String, apiService: DiabetesApiService, callback: (Boolean) -> Unit) {
        apiService.diabetesCheck(petId).enqueue(object : Callback<ResponseDTO<DiabetesCheck2DTO>> {
            override fun onResponse(call: Call<ResponseDTO<DiabetesCheck2DTO>>, response: Response<ResponseDTO<DiabetesCheck2DTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()?.data
                    val diabetesRiskCheckDate = data?.diabetesRiskCheckDate
                    val diabetesRisk = data?.diabetesRisk

                    // 두 값 중 하나라도 null이면 true, 그렇지 않으면 false
                    val showAnalysisFragment = diabetesRiskCheckDate == null || diabetesRisk == null
                    callback(showAnalysisFragment)
                } else {
                    callback(true) // API 오류 시 analysisFragment로 이동
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DiabetesCheck2DTO>>, t: Throwable) {
                callback(true) // 네트워크 오류 시에도 null로 처리
            }
        })
    }
}