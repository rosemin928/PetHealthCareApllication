package com.example.pethealthapplication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.dailyreportapi.DailyReportApiClient
import com.example.pethealthapplication.dailyreportapi.DailyReportApiService
import com.example.pethealthapplication.dto.DailyWalkingRecordCheckDTO
import com.example.pethealthapplication.dto.DailyWalkingRecordDTO
import com.example.pethealthapplication.dto.DailyWalkingRecordUpdateDTO
import com.example.pethealthapplication.dto.ResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.LocalTime
import java.util.Calendar

class DailyWalkingActivity : AppCompatActivity() {

    // 산책 날짜 저장
    private var selectedDate: String? = null

    // 목표 산책 시간 저장
    private var targetWalkingTimeAdd: Short? = null

    // 선택된 산책 강도 저장
    private var selectedIntensity: Char? = null

    // 산책 시간 저장
    private var walkingTime: Short? = null

    private lateinit var strongButton: TextView
    private lateinit var mediumButton: TextView
    private lateinit var weakButton: TextView
    private lateinit var targetWalkingTimeText: TextView
    private lateinit var walkingTimeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_walking)

        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val dailyReportApiService = DailyReportApiClient.getApiService(this)

        val saveButton = findViewById<Button>(R.id.saveBtn)
        val deleteButton = findViewById<Button>(R.id.deleteBtn)

        val actionType = intent.getStringExtra("ACTION")
        Log.d("DailyWalkingActivity", "actionType: $actionType")

        updateSaveButtonState(saveButton) // 초기 상태 업데이트

        // 선택 날짜 가져오기
        selectedDate = intent.getStringExtra("SELECTED_DATE")

        // 선택 날짜와 목표 산책 시간 조회 + 값 저장
        val selectedDateText = findViewById<TextView>(R.id.selectedDateText)
        targetWalkingTimeText = findViewById(R.id.targetWalkingTimeText)
        selectedDateText.text = selectedDate
        if (petId != null)
            checkTargetWalkingTime(petId, dailyReportApiService) { targetWalkingTime ->
                if (targetWalkingTime != null) {
                    targetWalkingTimeAdd = targetWalkingTime

                    // 분을 시간과 분으로 변환
                    val hours = targetWalkingTime / 60
                    val minutes = targetWalkingTime % 60
                    val formattedWalkingTime = "${hours}시간 ${minutes}분"

                    targetWalkingTimeText.text = formattedWalkingTime
                } else {
                    // targetWalkingTime이 null일 경우 처리
                    Toast.makeText(this, "목표 산책 시간이 설정되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }


        // 나머지 값들 조회
        if (petId != null) {
            dailyWalkingRecordCheck(petId, selectedDate ?: "", dailyReportApiService)
        }


        // 산책 강도 선택
        strongButton = findViewById(R.id.strong)
        mediumButton = findViewById(R.id.medium)
        weakButton = findViewById(R.id.weak)

        strongButton.setOnClickListener {
            selectedIntensity = '강'
            updateBackgroundForSelected(strongButton)
            updateSaveButtonState(saveButton)
        }
        mediumButton.setOnClickListener {
            selectedIntensity = '중'
            updateBackgroundForSelected(mediumButton)
            updateSaveButtonState(saveButton)
        }
        weakButton.setOnClickListener {
            selectedIntensity = '하'
            updateBackgroundForSelected(weakButton)
            updateSaveButtonState(saveButton)
        }


        // 산책 시간 선택
        val walkingTimeSelect = findViewById<ImageView>(R.id.walkingTimeSelect)
        walkingTimeText = findViewById(R.id.walkingTimeText)
        walkingTimeSelect.setOnClickListener {
            showDurationPickerDialog(this, 0, 0) { hours, minutes ->
                walkingTime = (hours * 60 + minutes).toShort() //분 단위로 저장
                walkingTimeText.text = String.format("%02d시간 %02d분", hours, minutes)
            }
        }


        // 값이 모두 들어있어야 저장하기 버튼 활성화
        // TextWatcher로 입력 필드 업데이트 (예: 시간 입력 등)
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 예를 들어 목표 산책 시간, 날짜 등이 변경되면 값 업데이트
                updateSaveButtonState(saveButton)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        selectedDateText.addTextChangedListener(textWatcher)
        targetWalkingTimeText.addTextChangedListener(textWatcher)
        walkingTimeText.addTextChangedListener(textWatcher)


        // 날짜별 산책 기록 저장
        saveButton.setOnClickListener {
            //새로 작성
            if (actionType == "POST") {
                if (petId != null)
                    dailyWalkingRecordAdd(petId, dailyReportApiService)
                finish() //홈화면으로 돌아가기

            //업데이트
            } else {
                if (petId != null)
                    dailyWalkingRecordUpdate(petId, dailyReportApiService)
                finish() //홈화면으로 돌아가기
            }
        }


        //날짜별 산책 기록 삭제
        deleteButton.setOnClickListener {
            if (petId != null)
                dailyWalkingRecordDelete(petId, selectedDate ?: "", dailyReportApiService)
            finish() //홈화면으로 돌아가기
        }

    }


    // 목표 산책 시간 조회
    private fun checkTargetWalkingTime(petId: String, apiService: DailyReportApiService, callback: (Short?) -> Unit) {
        apiService.targetWalkingTimeCheck(petId).enqueue(object : Callback<ResponseDTO<Short>> {
            override fun onResponse(call: Call<ResponseDTO<Short>>, response: Response<ResponseDTO<Short>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val data = responseDTO?.data

                    if (status == 200 && data != null) {
                        callback(data)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Short>>, t: Throwable) {
                callback(null)
            }
        })
    }

    // 산책 강도 선택
    private fun updateBackgroundForSelected(view: TextView) {
        // 모든 배경을 기본값으로 설정
        strongButton.background = null
        mediumButton.background = null
        weakButton.background = null

        // 클릭된 view에만 배경 적용
        view.setBackgroundResource(R.drawable.calendar_today)
    }

    // 시간 선택 다이얼로그 표시
    private fun showDurationPickerDialog(
        context: Context,
        initialHour: Int = 0,
        initialMinute: Int = 0,
        onTimeSet: (Int, Int) -> Unit
    ) {
        // 시간 Picker
        val hourPicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 23
            value = initialHour
            setFormatter { value -> "$value 시간" }
            setDividerColor(this, Color.parseColor("#58ACFA"))
        }

        // 분 Picker
        val minutePicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 59
            value = initialMinute
            setFormatter { value -> "$value 분" }
            setDividerColor(this, Color.parseColor("#58ACFA"))
        }

        // 레이아웃 설정
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(50, 40, 50, 40)
            gravity = Gravity.CENTER
            addView(hourPicker)
            addView(minutePicker)

            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hourPicker.layoutParams = params
            minutePicker.layoutParams = params
        }

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(context).create()

        // 버튼을 위한 레이아웃
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(20, 10, 20, 10)
            weightSum = 2f

            // 취소 버튼
            val cancelButton = Button(context).apply {
                text = "취소"
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    dialog.dismiss()  // 취소 버튼을 누르면 다이얼로그 닫기
                }
            }

            // 확인 버튼
            val confirmButton = Button(context).apply {
                text = "확인"
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    val selectedHours = hourPicker.value
                    val selectedMinutes = minutePicker.value
                    onTimeSet(selectedHours, selectedMinutes)
                    dialog.dismiss()  // 확인 버튼을 누르면 선택 후 다이얼로그 닫기
                }
            }

            // 버튼을 두 칸으로 나누기
            val buttonParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            cancelButton.layoutParams = buttonParams
            confirmButton.layoutParams = buttonParams

            addView(cancelButton)
            addView(confirmButton)
        }

        // 다이얼로그 레이아웃 설정
        val dialogLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(layout)
            addView(buttonLayout)
        }

        // 다이얼로그에 레이아웃 설정 및 표시
        dialog.setView(dialogLayout)
        dialog.show()
    }

    // NumberPicker 구분선 색상 변경을 위한 함수
    private fun setDividerColor(picker: NumberPicker, color: Int) {
        try {
            val pickerFields = NumberPicker::class.java.declaredFields
            for (field in pickerFields) {
                if (field.name == "mSelectionDivider") {
                    field.isAccessible = true
                    field.set(picker, ColorDrawable(color))
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 날짜별 산책 기록 저장
    private fun dailyWalkingRecordAdd(petId: String, apiService: DailyReportApiService) {

        val dailyWalkingRecordDTO = DailyWalkingRecordDTO(
            selectedDate ?: "",
            targetWalkingTimeAdd ?: 0,
            selectedIntensity ?: '중',
            walkingTime ?: 0)

        apiService.dailyWalkingRecordAdd(petId, dailyWalkingRecordDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    if (status == 200) {
                        Toast.makeText(this@DailyWalkingActivity, "저장 성공!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DailyWalkingActivity, "저장 실패: $message", Toast.LENGTH_SHORT).show()
                        Log.e("DailyWalkingActivity", "저장 실패: $message")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 에러"
                    Toast.makeText(this@DailyWalkingActivity, "응답 실패 $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("DailyWalkingActivity", "응답 실패: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@DailyWalkingActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("DailyWalkingActivity", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 4가지 값이 모두 null이 아니면 버튼 활성화
    private fun updateSaveButtonState(saveButton: Button) {
        saveButton.isEnabled = selectedDate != null && targetWalkingTimeAdd != null && selectedIntensity != null && walkingTime != null
    }

    // 날짜별 산책 기록 삭제
    private fun dailyWalkingRecordDelete(petId: String, recordDate: String, apiService: DailyReportApiService) {
        apiService.dailyWalkingRecordDelete(petId, recordDate).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message

                    if (status == 200) {
                        Toast.makeText(this@DailyWalkingActivity, "삭제 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DailyWalkingActivity, "삭제 실패: $message", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 에러"
                    Toast.makeText(this@DailyWalkingActivity, "응답 실패 $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@DailyWalkingActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 날짜별 산책 기록 조회
    private fun dailyWalkingRecordCheck(petId: String, recordDate: String, apiService: DailyReportApiService) {
        apiService.dailyWalkingRecordCheck(petId, recordDate).enqueue(object : Callback<ResponseDTO<DailyWalkingRecordCheckDTO>> {
            override fun onResponse(call: Call<ResponseDTO<DailyWalkingRecordCheckDTO>>, response: Response<ResponseDTO<DailyWalkingRecordCheckDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    val targetWalkingTime = data?.targetWalkingTime
                    val walkingIntensity = data?.walkingIntensity
                    val walkingTime = data?.walkingTime

                    if (status == 200) {
                        // 분을 시간과 분으로 변환
                        if (targetWalkingTime != null && walkingTime != null) {
                            val targetHours = targetWalkingTime / 60
                            val targetMinutes = targetWalkingTime % 60
                            val formattedTargetWalkingTime = "${targetHours}시간 ${targetMinutes}분"

                            val hours = walkingTime / 60
                            val minutes = walkingTime % 60
                            val formattedWalkingTime = "${hours}시간 ${minutes}분"

                            targetWalkingTimeText.text = formattedTargetWalkingTime
                            walkingTimeText.text = formattedWalkingTime

                            if (walkingIntensity == '강') {
                                selectedIntensity = '강'
                                updateBackgroundForSelected(strongButton)
                            } else if (walkingIntensity == '중') {
                                selectedIntensity = '중'
                                updateBackgroundForSelected(mediumButton)
                            } else {
                                selectedIntensity = '하'
                                updateBackgroundForSelected(weakButton)
                            }
                        }
                    } else {
                        //아무것도 하지 않음
                    }
                } else {
                    //아무것도 하지 않음
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DailyWalkingRecordCheckDTO>>, t: Throwable) {
                Toast.makeText(this@DailyWalkingActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 날짜별 산책 기록 업데이트
    private fun dailyWalkingRecordUpdate(petId: String, apiService: DailyReportApiService) {

        val dailyWalkingRecordUpdateDTO = DailyWalkingRecordUpdateDTO(
            selectedDate ?: "",
            selectedIntensity ?: '중',
            walkingTime ?: 0
        )

        apiService.dailyWalkingRecordUpdate(petId, dailyWalkingRecordUpdateDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message

                    if (status == 200) {
                        Toast.makeText(this@DailyWalkingActivity, "업데이트 성공!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DailyWalkingActivity, "업데이트 실패: $message", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 에러"
                    Toast.makeText(this@DailyWalkingActivity, "응답 실패 $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@DailyWalkingActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}