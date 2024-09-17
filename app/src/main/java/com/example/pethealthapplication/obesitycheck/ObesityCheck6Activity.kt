package com.example.pethealthapplication.obesitycheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.ObesityCheckDTO
import com.example.pethealthapplication.dto.RecommendedKcalDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.weightapi.WeightApiClient
import com.example.pethealthapplication.weightapi.WeightApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ObesityCheck6Activity : AppCompatActivity() {

    private var activityButton: Button? = null
    private var activityLevel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check6)

        val greatActivity = findViewById<Button>(R.id.great)
        val mediumActivity = findViewById<Button>(R.id.medium)
        val lessActivity = findViewById<Button>(R.id.less)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val weightApiService = WeightApiClient.getApiService(this)

        //정보 받기
        val weightStatus = intent.getStringExtra("WEIGHT_STATUS")
        val waistRibVisibility = intent.getCharExtra("WAIST_RIB_VISIBILITY", 'y')
        val ribTouchability = intent.getCharExtra("RIB_TOUCHABILITY", 'y')
        val bodyShape = intent.getIntExtra("BODY_SHAPE", 1)


        //버튼 중복 클릭 금지
        greatActivity.setOnClickListener {
            handleBcsButtonClick(greatActivity)
            activityLevel = "많음"
        }
        mediumActivity.setOnClickListener {
            handleBcsButtonClick(mediumActivity)
            activityLevel = "보통"
        }
        lessActivity.setOnClickListener {
            handleBcsButtonClick(lessActivity)
            activityLevel = "적음"
        }


        // 다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            // ObesityCheckDTO에 값 할당
            val obesityCheckDTO = ObesityCheckDTO(
                weightStatus = weightStatus ?: "",
                waistRibVisibility = waistRibVisibility,
                ribTouchability = ribTouchability,
                bodyShape = bodyShape,
                activityLevel = activityLevel
            )

            obesityCheck(petId ?: "", weightApiService, obesityCheckDTO)
        }

    }


    //버튼 중복 클릭 금지 이벤트
    private fun handleBcsButtonClick(button: Button) {
        if (button == activityButton) {
            button.isSelected = false
            activityButton = null
        }
        else {
            activityButton?.isSelected = false
            button.isSelected = true
            activityButton = button
        }
    }

    //비만도 + 하루 적정 칼로리 계산
    private fun obesityCheck(petId: String, apiService: WeightApiService, obesityCheckDTO: ObesityCheckDTO) {
        apiService.obesityCheck(petId, obesityCheckDTO).enqueue(object :
            Callback<ResponseDTO<RecommendedKcalDTO>> {
            override fun onResponse(call: Call<ResponseDTO<RecommendedKcalDTO>>, response: Response<ResponseDTO<RecommendedKcalDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    if (status == 200 && data != null) {
                        Toast.makeText(this@ObesityCheck6Activity, "적정 칼로리 계산 성공!", Toast.LENGTH_SHORT).show()

                        // Intent에 데이터 담기
                        val intent = Intent(this@ObesityCheck6Activity, ObesityCheck7Activity::class.java)
                        intent.putExtra("PET_NAME", data.petName)
                        intent.putExtra("OBESITY_DEGREE", data.obesityDegree)
                        intent.putExtra("RECOMMENDED_CALORIES", data.recommendedCalories.toPlainString()) // BigDecimal은 String으로 변환
                        intent.putExtra("WEIGHT_CAL_RECOMMENDED_CALORIES", data.weightCalRecommendedCalories.toPlainString()) // BigDecimal은 String으로 변환
                        intent.putExtra("CAL_RECOMMENDED_CALORIES_DATE", data.calRecommendedCaloriesDate)

                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ObesityCheck6Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ObesityCheck6Activity, "서버 에러: ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<RecommendedKcalDTO>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck6Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}