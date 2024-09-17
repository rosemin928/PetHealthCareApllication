package com.example.pethealthapplication.register

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import com.example.pethealthapplication.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar

class Register8Activity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val day = calendar.get(Calendar.DAY_OF_MONTH)
    private lateinit var nextBtn: Button

    private var injectionDate: LocalDate? = null
    private var medicineDate: LocalDate? = null

    private var selectedRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register8)

        // 이전 화면에서 전달된 데이터 받기
        val petName = intent.getStringExtra("PET_NAME")
        val petAge = intent.getStringExtra("PET_AGE")
        val petBreed = intent.getStringExtra("PET_BREED")
        val petWeight = intent.getStringExtra("PET_WEIGHT")
        val petGender = intent.getStringExtra("PET_GENDER")
        val petNeutered = intent.getBooleanExtra("PET_NEUTERED", false)
        val petDiabetes = intent.getBooleanExtra("PET_DIABETES", false)
        val insulinTime1 = intent.getStringExtra("INSULIN_TIME1")
        val insulinTime2 = intent.getStringExtra("INSULIN_TIME2")
        val insulinTime3 = intent.getStringExtra("INSULIN_TIME3")

        // 뷰 참조
        val injectionBtn = findViewById<RadioButton>(R.id.injection)
        val medicineBtn = findViewById<RadioButton>(R.id.medicine)
        val injectionText = findViewById<TextView>(R.id.injection_text)
        val medicineText = findViewById<TextView>(R.id.medicine_text)
        nextBtn = findViewById(R.id.next_button)

        // 라디오 버튼 클릭 시 중복 선택 방지
        injectionBtn.setOnClickListener {
            handleRadioButtonClick(injectionBtn, injectionText, true)
        }

        medicineBtn.setOnClickListener {
            handleRadioButtonClick(medicineBtn, medicineText, false)
        }

        // 다음 버튼 클릭 시 데이터를 Register9Activity로 전달
        nextBtn.setOnClickListener {
            val intent = Intent(this@Register8Activity, Register9Activity::class.java).apply {
                putExtra("PET_NAME", petName)
                putExtra("PET_AGE", petAge)
                putExtra("PET_BREED", petBreed)
                putExtra("PET_WEIGHT", petWeight)
                putExtra("PET_GENDER", petGender)
                putExtra("PET_NEUTERED", petNeutered)
                putExtra("PET_DIABETES", petDiabetes)

                // 인슐린 시간 전달
                putExtra("INSULIN_TIME1", insulinTime1)
                putExtra("INSULIN_TIME2", insulinTime2)
                putExtra("INSULIN_TIME3", insulinTime3)

                putExtra("INJECTION_DATE", injectionDate.toString())
                putExtra("MEDICINE_DATE", medicineDate.toString())
            }

            Log.d("Register8Activity", "injectionDate: $injectionDate")
            Log.d("Register8Activity", "medicineDate: $medicineDate")

            startActivity(intent)
        }
    }

    // 라디오 버튼 클릭 처리
    private fun handleRadioButtonClick(button: RadioButton, textView: TextView, isInjection: Boolean) {
        if (button == selectedRadioButton) {
            // 동일한 버튼 클릭 시 선택 해제
            button.isChecked = false
            selectedRadioButton = null
            textView.text = ""
            if (isInjection) {
                injectionDate = null
            } else {
                medicineDate = null
            }
        } else {
            // 다른 버튼 클릭 시 이전 선택 해제하고, 현재 선택
            selectedRadioButton?.isChecked = false
            button.isChecked = true
            selectedRadioButton = button

            // 날짜 선택 다이얼로그 표시
            showDatePickerDialog(isInjection, textView)
        }
    }

    // 날짜 선택 다이얼로그 표시
    private fun showDatePickerDialog(isInjection: Boolean, textView: TextView) {
        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            if (isInjection) {
                injectionDate = selectedDate
                medicineDate = null
                findViewById<TextView>(R.id.medicine_text).text = "" // 다른 날짜 필드를 초기화
            } else {
                medicineDate = selectedDate
                injectionDate = null
                findViewById<TextView>(R.id.injection_text).text = "" // 다른 날짜 필드를 초기화
            }
            textView.text = String.format("%02d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
        }, year, month, day).show()
    }
}