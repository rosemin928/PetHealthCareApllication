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

        // 네비게이션 설정
        setBottomNavigationView()

        // Intent에서 플래그 확인
        val showAnalysis2Fragment = intent.getBooleanExtra("SHOW_ANALYSIS2_FRAGMENT", false)
        if (showAnalysis2Fragment) {
            // Analysis2Fragment를 표시
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, Analysis2Fragment())
                .commit()

            // BottomNavigationView의 선택 상태를 analysisFragment로 변경
            binding.bottomNavigationView.selectedItemId = R.id.analysisFragment
        } else {
            // 기본 화면(HomeFragment)을 표시
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment())
                .commit()
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

        val diabetesApiService = DiabetesApiClient.getApiService(this)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment()).commit()
                    true
                }
                R.id.recommendFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, RecommendFragment()).commit()
                    true
                }
                R.id.analysisFragment -> {
                    // SharedPreferences에서 petId를 analysisFragment 버튼을 눌렀을 때 가져오도록 변경
                    val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    val petId = sharedPreferences.getString("PET_ID_KEY", null)

                    // 조회 API 호출 후 데이터에 따라 프래그먼트 이동
                    checkDiabetesData(petId ?: "", diabetesApiService) { showAnalysisFragment ->
                        if (showAnalysisFragment) {
                            // 하나라도 null이면 analysis1Fragment로 이동
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.main_container, Analysis1Fragment())
                                .addToBackStack(null) // 백스택에 추가하지 않음으로써 이전 프래그먼트로 돌아가지 않도록 처리
                                .commit()
                        } else {
                            // 둘 다 null이 아니면 analysis2Fragment로 이동
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.main_container, Analysis2Fragment())
                                .addToBackStack(null) // 백스택에 추가하지 않음
                                .commit()
                        }
                    }
                    true
                }
                R.id.snsFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, SnsFragment()).commit()
                    true
                }
                R.id.myPageFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, MyPageFragment()).commit()
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
                    val showAnalysisFragment = diabetesRiskCheckDate.isNullOrEmpty() || diabetesRisk == null
                    callback(showAnalysisFragment)
                } else {
                    // API 오류 시 콜백 true로 설정하여 analysis1Fragment로 이동
                    callback(true)
                    Toast.makeText(this@MainActivity, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DiabetesCheck2DTO>>, t: Throwable) {
                // 네트워크 오류 시에도 null로 처리
                callback(true)
                Toast.makeText(this@MainActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}