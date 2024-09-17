package com.example.pethealthapplication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
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
                targetWalingTimeArea.setText(String.format("%02d시간 %02d분", hours, minutes))
            }
        }

    }


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
        // NumberPickers 설정
        val hourPicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 23
            value = initialHour
        }

        val minutePicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 59
            value = initialMinute
        }

        // LinearLayout에 NumberPicker 추가
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            addView(hourPicker)
            addView(minutePicker)
        }

        // AlertDialog를 이용하여 다이얼로그 구성
        AlertDialog.Builder(context)
            .setTitle("시간 범위 설정")
            .setView(layout)
            .setPositiveButton("확인") { _, _ ->
                val selectedHours = hourPicker.value
                val selectedMinutes = minutePicker.value
                onTimeSet(selectedHours, selectedMinutes)
            }
            .setNegativeButton("취소", null)
            .show()
    }
}