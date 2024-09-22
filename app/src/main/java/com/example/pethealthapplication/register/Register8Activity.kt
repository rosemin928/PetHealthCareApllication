package com.example.pethealthapplication.register

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.pethealthapplication.MainActivity
import com.example.pethealthapplication.R
import java.time.LocalDate
import java.time.ZoneId
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

            // 알람 설정
            scheduleNotificationForDate(selectedDate, isInjection)
        }, year, month, day).show()
    }

    // 인슐린 및 약물 투여 후 알람 설정
    private fun scheduleNotificationForDate(selectedDate: LocalDate, isInjection: Boolean) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 알람을 받을 PendingIntent 생성
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TITLE", if (isInjection) "심장사상충 주사 알림" else "심장사상충 약 알림")
            putExtra("NOTIFICATION_MESSAGE", if (isInjection) "심장사상충 접종 1년이 되었습니다." else "심장사상충 약 급여 한 달이 되었습니다.")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, if (isInjection) 1 else 2, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알람 시간 계산
        val alarmTime = if (isInjection) {
            selectedDate.plusYears(1).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        } else {
            selectedDate.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        }

        // 알람 설정
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

        Log.d("Register8Activity", if (isInjection) "인슐린 알람 설정" else "약물 알람 설정")
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 이상에서는 NotificationChannel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("heartworm_channel", "Heartworm Reminder", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림을 클릭하면 MainActivity로 이동
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // 알림 생성
        val notification = NotificationCompat.Builder(context, "heartworm_channel")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentTitle("심장사상충 알림")
            .setContentText("심장사상충 접종 날짜입니다.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}