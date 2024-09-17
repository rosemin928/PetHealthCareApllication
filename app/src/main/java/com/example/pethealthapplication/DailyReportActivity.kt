package com.example.pethealthapplication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.pethealthapplication.dailyreportapi.DailyReportApiClient
import com.example.pethealthapplication.dto.DailyReportDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class DailyReportActivity : AppCompatActivity() {

    private var actionType: String? = null
    private var memoDate: String? = null // 또는 다른 식별자
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_report)

        // Intent로부터 데이터 가져오기
        val intent = intent
        actionType = intent.getStringExtra("ACTION_TYPE")
        memoDate = intent.getStringExtra("DATE")

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)


        // UI 요소 초기화
        val diagnosisArea = findViewById<EditText>(R.id.diagnosis)
        val medicineArea = findViewById<EditText>(R.id.medicine)
        val petKgArea = findViewById<EditText>(R.id.petKg)
        val bloodSugarArea = findViewById<EditText>(R.id.bloodSugar)
        val specialMemoArea = findViewById<EditText>(R.id.specialMemo)
        val saveBtn = findViewById<Button>(R.id.saveBtn)

        // 기존 메모 데이터 가져오기 (수정 모드일 때)
        if (actionType == "EDIT") {
            fetchDailyReport(
                diagnosisArea,
                medicineArea,
                petKgArea,
                bloodSugarArea,
                specialMemoArea
            )
        }

        // TextWatcher 및 Save 버튼 클릭 이벤트 설정
        setupSaveButton(saveBtn, diagnosisArea, medicineArea, petKgArea, bloodSugarArea, specialMemoArea)

    }





    // 메모 추가 & 수정
    private fun setupSaveButton(
        saveBtn: Button,
        diagnosisArea: EditText,
        medicineArea: EditText,
        petKgArea: EditText,
        bloodSugarArea: EditText,
        specialMemoArea: EditText
    ) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val diagnosis = diagnosisArea.text.toString()
                val medicine = medicineArea.text.toString()
                val petKgString = petKgArea.text.toString()
                val bloodSugarString = bloodSugarArea.text.toString()
                val specialMemo = specialMemoArea.text.toString()

                saveBtn.isEnabled = when {
                    medicine.isNotEmpty() && diagnosis.isEmpty() -> false
                    else -> diagnosis.isNotEmpty() || medicine.isNotEmpty() || petKgString.isNotEmpty() || bloodSugarString.isNotEmpty() || specialMemo.isNotEmpty()
                }
            }
        }
        diagnosisArea.addTextChangedListener(textWatcher)
        medicineArea.addTextChangedListener(textWatcher)
        petKgArea.addTextChangedListener(textWatcher)
        bloodSugarArea.addTextChangedListener(textWatcher)
        specialMemoArea.addTextChangedListener(textWatcher)

        saveBtn.setOnClickListener {
            val diagnosis = diagnosisArea.text.toString().takeIf { it.isNotBlank() } ?: ""
            val medicine = medicineArea.text.toString().takeIf { it.isNotBlank() } ?: ""
            val petKgString = petKgArea.text.toString().takeIf { it.isNotBlank() }
            val petKg = petKgString?.let { BigDecimal(it) } // 빈 문자열일 경우 null이 됩니다.
            val bloodSugarString = bloodSugarArea.text.toString().takeIf { it.isNotBlank() }
            val bloodSugar = bloodSugarString?.toShortOrNull()
            val specialMemo = specialMemoArea.text.toString().takeIf { it.isNotBlank() } ?: ""

            val dailyReportDTO = DailyReportDTO(
                memoDate ?: "", // 날짜 정보
                diagnosis, medicine, petKg, bloodSugar, specialMemo
            )

            val petId = sharedPreferences.getString("PET_ID_KEY", null)

            if (petId != null) {
                val dailyReportApiService = DailyReportApiClient.getApiService(this)

                when (actionType) {
                    "EDIT" -> {
                        // 업데이트 API 호출
                        val updateCall = dailyReportApiService.dailyReportUpdate(petId, dailyReportDTO)
                        updateCall.enqueue(object : Callback<ResponseDTO<Any>> {
                            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                                if (response.isSuccessful && response.body() != null) {
                                    val responseDTO = response.body()
                                    if (responseDTO?.status == 200) {
                                        Toast.makeText(this@DailyReportActivity, "메모 수정 성공", Toast.LENGTH_SHORT).show()
                                        fetchDailyReport(
                                            diagnosisArea,
                                            medicineArea,
                                            petKgArea,
                                            bloodSugarArea,
                                            specialMemoArea
                                        )
                                        finish()
                                    }
                                } else {
                                    Toast.makeText(this@DailyReportActivity, "서버 에러: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    Log.e("DailyReport", "Error Code: ${response.code()}, Message: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                                Toast.makeText(this@DailyReportActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    "ADD" -> {
                        // 추가 API 호출
                        val addCall = dailyReportApiService.dailyReport(petId, dailyReportDTO)
                        addCall.enqueue(object : Callback<ResponseDTO<Any>> {
                            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                                if (response.isSuccessful && response.body() != null) {
                                    val responseDTO = response.body()
                                    if (responseDTO?.status == 200) {
                                        Toast.makeText(this@DailyReportActivity, "메모 저장 성공", Toast.LENGTH_SHORT).show()
                                        fetchDailyReport(
                                            diagnosisArea,
                                            medicineArea,
                                            petKgArea,
                                            bloodSugarArea,
                                            specialMemoArea
                                        )
                                        finish()
                                        Log.d("DailyReportActivity", "memoDate: $memoDate")
                                    } else if (response.code() == 409) {
                                        Toast.makeText(this@DailyReportActivity, "등록 실패: 해당 날짜에 이미 메모가 존재합니다.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@DailyReportActivity, "서버 에러: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    Log.e("DailyReport", "Error Code: ${response.code()}, Message: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                                Toast.makeText(this@DailyReportActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }
    }


    //메모 조회
    private fun fetchDailyReport(
        diagnosisArea: EditText,
        medicineArea: EditText,
        petKgArea: EditText,
        bloodSugarArea: EditText,
        specialMemoArea: EditText
    ) {
        val dailyReportCheckApiService = DailyReportApiClient.getApiService(this)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val memoDateString = memoDate ?: ""

        if (petId != null) {
            dailyReportCheckApiService.dailyReportCheck(petId, memoDateString)
                .enqueue(object : Callback<ResponseDTO<DailyReportDTO>> {
                    override fun onResponse(
                        call: Call<ResponseDTO<DailyReportDTO>>,
                        response: Response<ResponseDTO<DailyReportDTO>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val responseDTO = response.body()
                            val data = responseDTO?.data

                            if (responseDTO?.status == 200 && data != null) {
                                // 각 필드 값이 null인지 확인하고, null일 경우 빈 문자열로 설정
                                diagnosisArea.setText(data.diagnosis?.takeIf { it.isNotEmpty() } ?: "")
                                medicineArea.setText(data.medicine?.takeIf { it.isNotEmpty() } ?: "")
                                petKgArea.setText(if (data.weight == BigDecimal.ZERO) "" else data.weight.toString())
                                bloodSugarArea.setText(data.bloodSugarLevel?.toString() ?: "")
                                specialMemoArea.setText(data.specialNote?.takeIf { it.isNotEmpty() } ?: "")
                            } else {
                                // 데이터가 없을 때 UI를 빈 문자열로 설정
                                diagnosisArea.setText("")
                                medicineArea.setText("")
                                petKgArea.setText("")
                                bloodSugarArea.setText("")
                                specialMemoArea.setText("")
                            }
                        } else {
                            // API 호출 실패 시 UI를 빈 문자열로 설정
                            diagnosisArea.setText("")
                            medicineArea.setText("")
                            petKgArea.setText("")
                            bloodSugarArea.setText("")
                            specialMemoArea.setText("")
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<DailyReportDTO>>, t: Throwable) {
                        Toast.makeText(this@DailyReportActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
