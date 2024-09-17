package com.example.pethealthapplication.diabetescheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.pethealthapplication.R
import com.example.pethealthapplication.diabetesapi.DiabetesApiClient
import com.example.pethealthapplication.diabetesapi.DiabetesApiService
import com.example.pethealthapplication.dto.IsObesityDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.obesitycheck.ObesityCheck4Activity
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class DiabetesCheck0Activity : AppCompatActivity() {

    private var obesityCheckButton: Button? = null
    private var isObesity: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diabetes_check0)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val diabetesApiService = DiabetesApiClient.getApiService(this)

        val yesButton = findViewById<Button>(R.id.yes)
        val noButton = findViewById<Button>(R.id.no)

        //버튼 중복 클릭 금지
        yesButton.setOnClickListener {
            handleObesityCheckButtonClick(yesButton)
            isObesity = 'y'
        }
        noButton.setOnClickListener {
            handleObesityCheckButtonClick(noButton)
            isObesity = 'n'
        }

        //다음 화면으로 넘어가기 + 정보 넘기기
        val nextButton = findViewById<Button>(R.id.nextBtn)
        nextButton.setOnClickListener {
            // derCalorieCalculate 함수에서 API 응답을 받고 다음 액티비티로 이동
            if (petId != null) {
                derCalorieCalculate(petId, diabetesApiService)
            }
        }
    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleObesityCheckButtonClick(button: Button) {
        if (button == obesityCheckButton) {
            button.isSelected = false
            obesityCheckButton = null
        } else {
            obesityCheckButton?.isSelected = false
            button.isSelected = true
            obesityCheckButton = button
        }
    }

    //der 칼로리 계산
    private fun derCalorieCalculate(petId: String, apiService: DiabetesApiService) {

        val isObesityDTO = IsObesityDTO(isObesity ?: 'y')

        apiService.derCaloriesCalculate(petId, isObesityDTO).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
            override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        // API 응답 성공 시 다음 Activity로 이동
                        val intent = Intent(this@DiabetesCheck0Activity, DiabetesCheck1Activity::class.java)
                        intent.putExtra("IS_OBESITY", isObesity)
                        intent.putExtra("DER_CALORIES", data.toString()) // BigDecimal 데이터를 String으로 변환하여 전달
                        Log.e("DiabetesCheck0", "$data")
                        startActivity(intent)
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@DiabetesCheck0Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DiabetesCheck0Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(this@DiabetesCheck0Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}