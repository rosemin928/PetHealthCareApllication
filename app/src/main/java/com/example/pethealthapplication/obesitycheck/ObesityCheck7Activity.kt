package com.example.pethealthapplication.obesitycheck

import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.MainActivity
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.ObesityUpdateDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.weightapi.WeightApiClient
import com.example.pethealthapplication.weightapi.WeightApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class ObesityCheck7Activity : AppCompatActivity() {

    private lateinit var petNameText1: TextView
    private lateinit var petNameText2: TextView
    private lateinit var obesityText: TextView
    private lateinit var kcalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check7)

        petNameText1 = findViewById(R.id.petNameText1)
        petNameText2 = findViewById(R.id.petNameText2)
        obesityText = findViewById(R.id.obesity)
        kcalText = findViewById(R.id.kcal)

        val normalCat = findViewById<ImageView>(R.id.normalCat)
        val fatCat = findViewById<ImageView>(R.id.fatCat)
        val thinCat = findViewById<ImageView>(R.id.thinCat)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val weightApiService = WeightApiClient.getApiService(this)

        // 전달된 데이터 받기
        val petName = intent.getStringExtra("PET_NAME")
        val obesityDegree = intent.getStringExtra("OBESITY_DEGREE")
        val recommendedCalories = intent.getStringExtra("RECOMMENDED_CALORIES")

        //UI 업데이트
        petNameText1.text = petName
        petNameText2.text = petName
        obesityText.text = obesityDegree
        kcalText.text = recommendedCalories
        when (obesityDegree) {
            "정상" ->{
                normalCat.visibility = View.VISIBLE
                fatCat.visibility = View.GONE
                thinCat.visibility = View.GONE
            } "과체중" -> {
            normalCat.visibility = View.GONE
            fatCat.visibility = View.VISIBLE
            thinCat.visibility = View.GONE
            } "저체중" -> {
            normalCat.visibility = View.GONE
            fatCat.visibility = View.GONE
            thinCat.visibility = View.VISIBLE
            }
        }

        //등록
        val registerButton = findViewById<Button>(R.id.registerBtn)
        registerButton.setOnClickListener {
            obesityUpdate(petId ?: "", weightApiService)
        }

        //홈으로 돌아가기
        val closeButton = findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener {
            // 홈으로 이동
            val intent = Intent(this@ObesityCheck7Activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //비만도 + 하루 적정 칼로리 등록
    private fun obesityUpdate(petId: String, apiService: WeightApiService) {

        // Intent로 받은 값을 DTO에 할당
        val obesityDegree = intent.getStringExtra("OBESITY_DEGREE")
        val recommendedCalories = intent.getStringExtra("RECOMMENDED_CALORIES")?.let {
            try {
                BigDecimal(it) // String을 BigDecimal로 변환
            } catch (e: NumberFormatException) {
                null // 변환이 실패하면 null로 처리
            }
        }
        val weightCalRecommendedCalories = intent.getStringExtra("WEIGHT_CAL_RECOMMENDED_CALORIES")?.let {
            try {
                BigDecimal(it) // String을 BigDecimal로 변환
            } catch (e: NumberFormatException) {
                null // 변환이 실패하면 null로 처리
            }
        }
        val calRecommendedCaloriesDate = intent.getStringExtra("CAL_RECOMMENDED_CALORIES_DATE")

        // DTO 객체 생성 및 값 설정
        val obesityUpdateDTO = ObesityUpdateDTO(
            obesityDegree = obesityDegree ?: "",
            recommendedCalories = recommendedCalories ?: BigDecimal.ZERO,
            weightCalRecommendedCalories = weightCalRecommendedCalories ?: BigDecimal.ZERO,
            calRecommendedCaloriesDate = calRecommendedCaloriesDate ?: ""
        )

        apiService.obesityUpdate(petId, obesityUpdateDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message

                    if (status == 200) {
                        Toast.makeText(this@ObesityCheck7Activity, "등록 성공!", Toast.LENGTH_SHORT).show()
                        // 홈으로 이동
                        val intent = Intent(this@ObesityCheck7Activity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ObesityCheck7Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ObesityCheck7Activity, "서버 에러: ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck7Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}