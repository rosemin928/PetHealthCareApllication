package com.example.pethealthapplication

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.pethealthapplication.dto.HospitalVisitDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.hospitavisitapi.HospitalVisitApiClient
import com.example.pethealthapplication.hospitavisitapi.HospitalVisitApiService
import com.example.pethealthapplication.petprofileapi.PetProfileApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class NextHospitalVisitActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val day = calendar.get(Calendar.DAY_OF_MONTH)

    private lateinit var nextHospitalVisitDateArea: EditText
    private var hospitalVisitDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_hospital_visit)

        val calendar = findViewById<ImageView>(R.id.calendar)
        nextHospitalVisitDateArea = findViewById(R.id.nextHospitalVisitDateText)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val deleteBtn = findViewById<Button>(R.id.deleteBtn)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val apiService = HospitalVisitApiClient.getApiService(this)


        //다음 병원 내원일 조회
        if (petId != null)
            NextHospitalVisitCheck(petId, apiService)


        //날짜 선택
        calendar.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                hospitalVisitDate = LocalDate.of(year, month + 1, day)
                nextHospitalVisitDateArea.setText(String.format("%02d-%02d-%02d", year, month + 1, day))

            }, year, month, day).show()
        }


        //저장하기 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nextHospitalVisitDateText = nextHospitalVisitDateArea.text.toString()

                saveBtn.isEnabled = nextHospitalVisitDateText.isNotEmpty()
            }
        }
        nextHospitalVisitDateArea.addTextChangedListener(textWatcher)


        //다음 병원 내원일 저장+수정
        saveBtn.setOnClickListener {
            if (petId != null)
                NextHospitalVisitUpdate(petId, apiService)
        }

        //다음 병원 내원일 삭제
        deleteBtn.setOnClickListener {
            if (petId != null)
                NextHospitalVisitDelete(petId, apiService)
        }

    }



    //*기능*
    //다음 병원 내원일 저장+수정
    private fun NextHospitalVisitUpdate(petId: String, apiService: HospitalVisitApiService) {

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val hospitalVisitDTO = HospitalVisitDTO(hospitalVisitDate?.format(dateFormatter) ?: "")

        apiService.hospitalVisitUpdate(petId, hospitalVisitDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                // 업데이트 성공/실패 처리
                if (response.isSuccessful) {
                    Toast.makeText(this@NextHospitalVisitActivity, "다음 병원 내원일 저장 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@NextHospitalVisitActivity, "다음 병원 내원일 저장 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(this@NextHospitalVisitActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //다음 병원 내원일 삭제
    private fun NextHospitalVisitDelete(petId: String, apiService: HospitalVisitApiService) {
        apiService.hospitalVisitDelete(petId).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                // 업데이트 성공/실패 처리
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message

                    if (status == 200) {
                        Toast.makeText(this@NextHospitalVisitActivity, "반려동물 프로필 삭제 성공", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@NextHospitalVisitActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@NextHospitalVisitActivity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@NextHospitalVisitActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //다음 병원 내원일 조회
    private fun NextHospitalVisitCheck(petId: String, apiService: HospitalVisitApiService) {
        apiService.hospitalVisitCheck(petId).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val nextVisitDate = responseDTO?.data // LocalDate 타입의 데이터

                    if (status == 200) {
                        val dateText = nextVisitDate?.toString() ?: ""
                        nextHospitalVisitDateArea.setText(dateText)
                    } else {
                        Toast.makeText(this@NextHospitalVisitActivity, "다음 병원 내원일 조회 실패: ${message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@NextHospitalVisitActivity, "다음 병원 내원일 조회 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(this@NextHospitalVisitActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}