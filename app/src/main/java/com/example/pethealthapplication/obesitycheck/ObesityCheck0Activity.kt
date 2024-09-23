package com.example.pethealthapplication.obesitycheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.weightapi.WeightApiClient
import com.example.pethealthapplication.weightapi.WeightApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class ObesityCheck0Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check0)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val apiService = WeightApiClient.getApiService(this)

        val currentWeightText = findViewById<TextView>(R.id.currentWeight)

        currentWeightCheck(petId ?: "", apiService) { currentWeight ->
            currentWeightText.text = currentWeight.toString()
        }


        val editButton = findViewById<Button>(R.id.editBtn)
        val nextButton = findViewById<Button>(R.id.nextBtn)
        editButton.setOnClickListener {
            intent = Intent(this@ObesityCheck0Activity, ObesityCheck1Activity::class.java)
            startActivity(intent)
        }
        nextButton.setOnClickListener {
            standardWeightCheck(petId ?: "", apiService) { standardWeightCheckText ->
                obesityWeightCheck(petId ?: "", apiService) { isObesityWeight ->

                    // 1. 표준 체중이 average이고 비만도가 null일 경우
                    if (standardWeightCheckText == "average" && isObesityWeight == null) {
                        val intent = Intent(this@ObesityCheck0Activity, ObesityCheck6Activity::class.java)
                        intent.putExtra("WEIGHT_STATUS", "average")
                        startActivity(intent)

                    // 2. 표준 체중이 over 또는 under이며 비만도가 null일 경우
                    } else if ((standardWeightCheckText == "over" || standardWeightCheckText == "under") && isObesityWeight == null) {
                        val intent = Intent(this@ObesityCheck0Activity, ObesityCheck3Activity::class.java)
                        startActivity(intent)

                    // 3. 비만도가 null이 아닌 경우
                    } else if (isObesityWeight != null) {
                        val intent = Intent(this@ObesityCheck0Activity, ObesityCheck2Activity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    //현재 몸무게 조회
    private fun currentWeightCheck(petId: String, apiService: WeightApiService, callback: (BigDecimal?) -> Unit) {
        apiService.currentWeightCheck(petId).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
            override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val data = responseDTO?.data

                    if (status == 200 && data != null) {
                        callback(data)
                    }
                } else {
                    Toast.makeText(this@ObesityCheck0Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck0Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //현재 몸무게 표준 몸무게와 비교
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
                        Toast.makeText(this@ObesityCheck0Activity, "상태 코드가 200이 아님 또는 데이터가 없음", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                } else {
                    Toast.makeText(this@ObesityCheck0Activity, "응답 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck0Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    //비만도 체크 몸무게 조회
    private fun obesityWeightCheck(petId: String, apiService: WeightApiService, callback: (BigDecimal?) -> Unit) {
        apiService.obesityWeightCheck(petId).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
            override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val data = responseDTO?.data

                    if (status == 200 && data != null) {
                        callback(data)
                    } else {
                        callback(data)
                    }
                } else {
                    Toast.makeText(this@ObesityCheck0Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck0Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}