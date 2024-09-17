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
import com.example.pethealthapplication.weightapi.WeightApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class DiabetesCheck1Activity : AppCompatActivity() {

    private var waterIntakeButton: Button? = null
    private var dailyWaterIntake: String? = null

    private lateinit var dailyWaterIntakeText: TextView
    private lateinit var petNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diabetes_check1)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val petApiService = PetProfileSummaryApiClient.getApiService(this)
        val diabetesApiService = DiabetesApiClient.getApiService(this)

        val greatIntake = findViewById<Button>(R.id.great)
        val mediumIntake = findViewById<Button>(R.id.medium)
        val lessIntake = findViewById<Button>(R.id.less)
        dailyWaterIntakeText = findViewById(R.id.dailyWaterIntakeText)
        petNameText = findViewById(R.id.petNameText)


        //정보 받아오기
        val isObesity = intent.getCharExtra("IS_OBESITY", 'y')
        val derCalories = intent.getStringExtra("DER_CALORIES")

        Log.e("DiabetesCheck1", "$derCalories")


        //평균 음수량 조회 + 반려동물 이름 조회
        if (petId != null) {
            dailyWaterIntakeCheck(petId, diabetesApiService)
            fetchPetProfileSummary(petId, petApiService)
        }


        //버튼 중복 클릭 금지
        greatIntake.setOnClickListener {
            handleWaterIntakeButtonClick(greatIntake)
            dailyWaterIntake = "이상"
        }
        mediumIntake.setOnClickListener {
            handleWaterIntakeButtonClick(mediumIntake)
            dailyWaterIntake = "동일"
        }
        lessIntake.setOnClickListener {
            handleWaterIntakeButtonClick(lessIntake)
            dailyWaterIntake = "이하"
        }


        //다음 화면으로 넘어가기 + 정보 넘기기
        val nextButton = findViewById<Button>(R.id.nextBtn)
        nextButton.setOnClickListener {
            intent = Intent(this@DiabetesCheck1Activity, DiabetesCheck2Activity::class.java)
            intent.putExtra("IS_OBESITY", isObesity)
            intent.putExtra("DER_CALORIES", derCalories)
            intent.putExtra("DAILY_WATER_INTAKE", dailyWaterIntake)
            startActivity(intent)
        }

    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleWaterIntakeButtonClick(button: Button) {
        if (button == waterIntakeButton) {
            button.isSelected = false
            waterIntakeButton = null
        }
        else {
            waterIntakeButton?.isSelected = false
            button.isSelected = true
            waterIntakeButton = button
        }
    }

    //평균 음수량 조회
    private fun dailyWaterIntakeCheck(petId: String, apiService: DiabetesApiService) {
        apiService.dailyWaterIntakeCheck(petId).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
            override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        val waterIntake = data.toString()

                        // UI 업데이트
                        dailyWaterIntakeText.text = waterIntake
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@DiabetesCheck1Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DiabetesCheck1Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(this@DiabetesCheck1Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                        Toast.makeText(this@DiabetesCheck1Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DiabetesCheck1Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileSummaryDTO>>, t: Throwable) {
                Toast.makeText(this@DiabetesCheck1Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}