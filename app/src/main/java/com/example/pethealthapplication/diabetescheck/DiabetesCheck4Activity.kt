package com.example.pethealthapplication.diabetescheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.pethealthapplication.MainActivity
import com.example.pethealthapplication.R
import com.example.pethealthapplication.diabetesapi.DiabetesApiClient
import com.example.pethealthapplication.diabetesapi.DiabetesApiService
import com.example.pethealthapplication.dto.DiabetesAnalyzeDTO
import com.example.pethealthapplication.dto.DiabetesCheckDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class DiabetesCheck4Activity : AppCompatActivity() {

    private var urinationCheckButton: Button? = null
    private var isIncreasedUrination: Char? = null

    private var isObesity: Char? = null
    private var dailyWaterIntake: String? = null
    private var dailyFoodIntake: String? = null
    private var isWeightLoss: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diabetes_check4)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val diabetesApiService = DiabetesApiClient.getApiService(this)

        val yesButton = findViewById<Button>(R.id.yes)
        val noButton = findViewById<Button>(R.id.no)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        //정보 받아오기
        isObesity = intent.getCharExtra("IS_OBESITY", 'y')
        dailyWaterIntake = intent.getStringExtra("DAILY_WATER_INTAKE")
        dailyFoodIntake = intent.getStringExtra("DAILY_FOOD_INTAKE")
        isWeightLoss = intent.getCharExtra("IS_WEIGHT_LOSS", 'y')


        //버튼 중복 클릭 금지
        yesButton.setOnClickListener {
            handleUrinationCheckButtonClick(yesButton)
            isIncreasedUrination = 'y'
        }
        noButton.setOnClickListener {
            handleUrinationCheckButtonClick(noButton)
            isIncreasedUrination = 'n'
        }


        //다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            diabetesCheck(petId ?: "", diabetesApiService)
        }

    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleUrinationCheckButtonClick(button: Button) {
        if (button == urinationCheckButton) {
            button.isSelected = false
            urinationCheckButton = null
        }
        else {
            urinationCheckButton?.isSelected = false
            button.isSelected = true
            urinationCheckButton = button
        }
    }

    //당뇨 위험도 분석
    private fun diabetesCheck(petId: String, apiService: DiabetesApiService) {

        val diabetesAnalyzeDTO = DiabetesAnalyzeDTO(
            isObesity ?: 'y',
            dailyWaterIntake ?: "",
            dailyFoodIntake ?: "",
            isWeightLoss ?: 'y',
            isIncreasedUrination ?: 'y')

        apiService.diabetesAnalyze(petId, diabetesAnalyzeDTO).enqueue(object : Callback<ResponseDTO<DiabetesCheckDTO>> {
            override fun onResponse(call: Call<ResponseDTO<DiabetesCheckDTO>>, response: Response<ResponseDTO<DiabetesCheckDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        // recommendedNote를 가져와 SharedPreferences에 저장
                        val recommendedNote = data?.recommendedNote
                        if (recommendedNote != null) {
                            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("RECOMMENDED_NOTE_KEY", recommendedNote)
                            editor.apply() // 비동기 저장

                            Log.d("DiabetesActivity", "$recommendedNote")
                        }

                        // MainActivity로 이동
                        val intent = Intent(this@DiabetesCheck4Activity, MainActivity::class.java)
                        // analysis2Fragment를 표시할 수 있도록 데이터를 전달
                        intent.putExtra("SHOW_ANALYSIS2_FRAGMENT", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish() // 현재 액티비티를 종료하여 뒤로가기를 눌렀을 때 돌아오지 않도록 처리
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@DiabetesCheck4Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DiabetesCheck4Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DiabetesCheckDTO>>, t: Throwable) {
                Toast.makeText(this@DiabetesCheck4Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}