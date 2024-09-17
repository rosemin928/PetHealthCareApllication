package com.example.pethealthapplication.obesitycheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

class ObesityCheck2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check2)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val apiService = WeightApiClient.getApiService(this)

        val petWeightGuide1 = findViewById<TextView>(R.id.petWeightGuide1)
        val petWeightGuide2 = findViewById<TextView>(R.id.petWeightGuide2)

        currentWeightCheck(petId ?: "", apiService) { currentWeight ->
            obesityWeightCheck(petId ?: "", apiService) { obesityWeight ->
                // 1. 이전 비만도 체크 당시 몸무게와 같을 때
                if (currentWeight == obesityWeight) {
                    petWeightGuide1.visibility = View.VISIBLE
                    petWeightGuide2.visibility = View.GONE

                // 2. 이전 비만도 체크 당시 몸무게와 다를 때
                } else {
                    petWeightGuide1.visibility = View.GONE
                    petWeightGuide2.visibility = View.VISIBLE
                }
            }
        }


        val nextButton = findViewById<Button>(R.id.nextBtn)
        nextButton.setOnClickListener {
            standardWeightCheck(petId ?: "", apiService) { standardWeightCheckText ->
                // 1. 몸무게가 표준 범위 내에 있을 때
                if (standardWeightCheckText == "average") {
                    val intent = Intent(this@ObesityCheck2Activity, ObesityCheck6Activity::class.java)
                    startActivity(intent)

                // 2. 몸무게가 표준 범위를 벗어났을 때
                } else {
                    val intent = Intent(this@ObesityCheck2Activity, ObesityCheck3Activity::class.java)
                    startActivity(intent)
                }
            }
        }
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
                        Toast.makeText(this@ObesityCheck2Activity, "상태 코드가 200이 아님 또는 데이터가 없음", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                } else {
                    Toast.makeText(this@ObesityCheck2Activity, "응답 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck2Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
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
                    Toast.makeText(this@ObesityCheck2Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck2Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@ObesityCheck2Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck2Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}