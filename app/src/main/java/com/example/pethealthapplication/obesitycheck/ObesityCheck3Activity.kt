package com.example.pethealthapplication.obesitycheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.PetProfileSummaryDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiService
import com.example.pethealthapplication.weightapi.WeightApiClient
import com.example.pethealthapplication.weightapi.WeightApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ObesityCheck3Activity : AppCompatActivity() {

    private lateinit var petNameText: TextView
    private var waistRibVisibleButton: Button? = null
    private var standardWeightCheckText: String? = null
    private var waistRibVisibility: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check3)

        petNameText = findViewById(R.id.petNameText)
        val petWeightText = findViewById<TextView>(R.id.petWeightText)
        val yesButton = findViewById<Button>(R.id.yes)
        val noButton = findViewById<Button>(R.id.no)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val petApiService = PetProfileSummaryApiClient.getApiService(this)
        val weightApiService = WeightApiClient.getApiService(this)

        //이름 조회
        fetchPetProfileSummary(petId ?: "", petApiService)

        //표준 몸무게와 비교
        standardWeightCheck(petId ?: "", weightApiService) { result ->
            standardWeightCheckText = result
            if (standardWeightCheckText == "over") {
                petWeightText.text = "높은"
            } else {
                petWeightText.text = "낮은"
            }
        }

        //버튼 중복 클릭 금지
        yesButton.setOnClickListener {
            handleWaistRibVisibleButtonClick(yesButton)
            waistRibVisibility = 'y'
        }
        noButton.setOnClickListener {
            handleWaistRibVisibleButtonClick(noButton)
            waistRibVisibility = 'n'
        }

        //다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            intent = Intent(this@ObesityCheck3Activity, ObesityCheck4Activity::class.java)
            intent.putExtra("WEIGHT_STATUS", standardWeightCheckText)
            intent.putExtra("WAIST_RIB_VISIBILITY", waistRibVisibility)
            startActivity(intent)
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
                        Toast.makeText(this@ObesityCheck3Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ObesityCheck3Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileSummaryDTO>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck3Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //반려동물 표준 몸무게와 비교
    private fun standardWeightCheck(petId: String, apiService: WeightApiService, callback: (String?) -> Unit) {
        apiService.standardWeightCheck(petId).enqueue(object : Callback<ResponseDTO<String>> {
            override fun onResponse(call: Call<ResponseDTO<String>>, response: Response<ResponseDTO<String>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val data = responseDTO?.data

                    if (status == 200) {
                        callback(data)
                    } else {
                        Toast.makeText(this@ObesityCheck3Activity, "상태 코드가 200이 아님 또는 데이터가 없음", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                } else {
                    Toast.makeText(this@ObesityCheck3Activity, "응답 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck3Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleWaistRibVisibleButtonClick(button: Button) {
        if (button == waistRibVisibleButton) {
            button.isSelected = false
            waistRibVisibleButton = null
        }
        else {
            waistRibVisibleButton?.isSelected = false
            button.isSelected = true
            waistRibVisibleButton = button
        }
    }
}