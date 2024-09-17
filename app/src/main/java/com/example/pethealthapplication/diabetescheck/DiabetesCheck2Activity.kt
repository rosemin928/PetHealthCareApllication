package com.example.pethealthapplication.diabetescheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.R
import com.example.pethealthapplication.diabetesapi.DiabetesApiClient
import com.example.pethealthapplication.diabetesapi.DiabetesApiService
import com.example.pethealthapplication.dto.PetProfileSummaryDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class DiabetesCheck2Activity : AppCompatActivity() {

    private var foodIntakeButton: Button? = null
    private var dailyFoodIntake: String? = null

    private lateinit var petNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diabetes_check2)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val petApiService = PetProfileSummaryApiClient.getApiService(this)

        val greatIntake = findViewById<Button>(R.id.great)
        val mediumIntake = findViewById<Button>(R.id.medium)
        val lessIntake = findViewById<Button>(R.id.less)
        val derCaloriesText = findViewById<TextView>(R.id.derCaloriesText)
        petNameText = findViewById(R.id.petNameText)

        val nextButton = findViewById<Button>(R.id.nextBtn)


        //정보 받아오기
        val isObesity = intent.getCharExtra("IS_OBESITY", 'y')
        val derCalories = intent.getStringExtra("DER_CALORIES")
        val dailyWaterIntake = intent.getStringExtra("DAILY_WATER_INTAKE")

        Log.e("DiabetesCheck2", "$derCalories")


        //der 칼로리 조회 + 반려동물 이름 조회
        derCaloriesText.text = derCalories
        if (petId != null) {
            fetchPetProfileSummary(petId, petApiService)
        }


        //버튼 중복 클릭 금지
        greatIntake.setOnClickListener {
            handleFoodIntakeButtonClick(greatIntake)
            dailyFoodIntake = "많음"
        }
        mediumIntake.setOnClickListener {
            handleFoodIntakeButtonClick(mediumIntake)
            dailyFoodIntake = "보통"
        }
        lessIntake.setOnClickListener {
            handleFoodIntakeButtonClick(lessIntake)
            dailyFoodIntake = "적음"
        }


        //다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            intent = Intent(this@DiabetesCheck2Activity, DiabetesCheck3Activity::class.java)
            intent.putExtra("IS_OBESITY", isObesity)
            intent.putExtra("DAILY_WATER_INTAKE", dailyWaterIntake)
            intent.putExtra("DAILY_FOOD_INTAKE", dailyFoodIntake)
            startActivity(intent)
        }

    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleFoodIntakeButtonClick(button: Button) {
        if (button == foodIntakeButton) {
            button.isSelected = false
            foodIntakeButton = null
        }
        else {
            foodIntakeButton?.isSelected = false
            button.isSelected = true
            foodIntakeButton = button
        }
    }

    //반려동물 프로필 요약 조회(이름을 가져오기 위함)
    private fun fetchPetProfileSummary(petId: String, apiService: PetProfileSummaryApiService) {
        apiService.petProfileSummary(petId).enqueue(object :
            Callback<ResponseDTO<PetProfileSummaryDTO>> {
            override fun onResponse(call: Call<ResponseDTO<PetProfileSummaryDTO>>, response: Response<ResponseDTO<PetProfileSummaryDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        val petName = data?.petName

                        // UI 업데이트
                        petNameText.text = petName
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@DiabetesCheck2Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DiabetesCheck2Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileSummaryDTO>>, t: Throwable) {
                Toast.makeText(this@DiabetesCheck2Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}