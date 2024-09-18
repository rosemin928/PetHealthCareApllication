package com.example.pethealthapplication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
import com.example.pethealthapplication.dailyreportapi.DailyReportApiClient
import com.example.pethealthapplication.dailyreportapi.DailyReportApiService
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.TargetWalkingTimeDTO
import com.example.pethealthapplication.dto.WalkingScheduleDTO
import com.example.pethealthapplication.petprofileapi.PetProfileApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.util.Calendar

class TargetWalkingTimeActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val hour = calendar.get(Calendar.HOUR_OF_DAY)
    private val min = calendar.get(Calendar.MINUTE)

    // 목표 산책 시각을 저장할 변수
    private var targetSchedule: LocalTime? = null
    private var targetWalkingTime: Short? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_walking_time)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val apiService = DailyReportApiClient.getApiService(this)


        // 목표 산책 시각 설정
        val targetScheduleArea = findViewById<EditText>(R.id.targetScheduleArea)
        targetScheduleArea.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                targetSchedule = LocalTime.of(hourOfDay, minute)
                targetScheduleArea.setText(String.format("%02d:%02d", hourOfDay, minute))
            }
        }


        // 목표 산책 시간 설정
        val targetWalingTimeArea = findViewById<EditText>(R.id.targetWalkingTimeArea)
        targetWalingTimeArea.setOnClickListener {
            showDurationPickerDialog(this, 0, 0) { hours, minutes ->
                targetWalkingTime = (hours * 60 + minutes).toShort() //분 단위로 저장
                targetWalingTimeArea.setText(String.format("%02d시간 %02d분", hours, minutes))
            }
        }


        // 값이 모두 들어있어야 저장하기 버튼 활성화
        val saveButton = findViewById<Button>(R.id.saveBtn)
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val targetScheduleAreaText = targetScheduleArea.text.toString()
                val targetWalkingTimeAreaText = targetWalingTimeArea.text.toString()

                saveButton.isEnabled = targetScheduleAreaText.isNotEmpty() && targetWalkingTimeAreaText.isNotEmpty()
            }
        }
        targetScheduleArea.addTextChangedListener(textWatcher)
        targetWalingTimeArea.addTextChangedListener(textWatcher)


        // 목표 산책 시각 & 시간 저장
        saveButton.setOnClickListener {
            updateWalkingTimeAndSchedule(petId ?: "", apiService)
            finish() // 홈화면으로 돌아가기
        }

    }


    // *기능들*
    // 시각 선택 다이얼로그 표시
    private fun showTimePickerDialog(onTimeSet: (Int, Int) -> Unit) {
        TimePickerDialog(
            this, AlertDialog.THEME_HOLO_LIGHT, { _, hourOfDay, minute ->
                onTimeSet(hourOfDay, minute)
            }, hour, min, true
        ).show()
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


    // 목표 산책 시간 및 시각 업데이트
    private fun updateWalkingTimeAndSchedule(petId: String, apiService: DailyReportApiService) {
        // 목표 산책 시간 DTO 생성
        val targetWalkingTimeDTO = TargetWalkingTimeDTO(targetWalkingTime ?: 0)
        val walkingScheduleDTO = WalkingScheduleDTO(targetSchedule.toString())

        // 첫 번째 API 호출 (목표 산책 시간 업데이트)
        apiService.targetWalkingTime(petId, targetWalkingTimeDTO).enqueue(object :
            Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful) {
                    // 첫 번째 요청이 성공하면 두 번째 API 호출 (목표 산책 시각 업데이트)
                    apiService.walkingSchedule(petId, walkingScheduleDTO).enqueue(object :
                        Callback<ResponseDTO<Any>> {
                        override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                            if (response.isSuccessful) {
                                // 두 번째 요청이 성공하면 성공 메시지 표시
                                Toast.makeText(this@TargetWalkingTimeActivity, "저장 성공", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                // 두 번째 요청이 실패하면 실패 메시지 표시
                                Toast.makeText(this@TargetWalkingTimeActivity, "저장 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                            // 두 번째 요청이 네트워크 오류일 경우 처리
                            Toast.makeText(this@TargetWalkingTimeActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // 첫 번째 요청이 실패하면 실패 메시지 표시
                    Toast.makeText(this@TargetWalkingTimeActivity, "저장 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                // 첫 번째 요청이 네트워크 오류일 경우 처리
                Toast.makeText(this@TargetWalkingTimeActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}