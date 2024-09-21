package com.example.pethealthapplication.register

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pethealthapplication.InsulinAlarmReceiver
import com.example.pethealthapplication.R
import java.time.LocalTime
import java.util.Calendar
import java.time.ZoneId
import java.time.ZonedDateTime

class Register7Activity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val hour = calendar.get(Calendar.HOUR_OF_DAY)
    private val min = calendar.get(Calendar.MINUTE)
    private var selectedButton: Button? = null
    private var hasDiabetes: Boolean? = null
    private lateinit var nextBtn: Button

    // 변수 선언
    private var petName: String? = null
    private var petAge: String? = null
    private var petBreed: String? = null
    private var petWeight: String? = null
    private var petGender: String? = null
    private var petNeutered: Boolean = false

    // 인슐린 시간을 저장할 변수
    private var insulinTime1: LocalTime? = null
    private var insulinTime2: LocalTime? = null
    private var insulinTime3: LocalTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register7)

        // 인텐트에서 데이터 받기
        petName = intent.getStringExtra("PET_NAME")
        petAge = intent.getStringExtra("PET_AGE")
        petBreed = intent.getStringExtra("PET_BREED")
        petWeight = intent.getStringExtra("PET_WEIGHT")
        petGender = intent.getStringExtra("PET_GENDER")
        petNeutered = intent.getBooleanExtra("PET_NEUTERED", false)

        val yesBtn = findViewById<Button>(R.id.yes)
        val noBtn = findViewById<Button>(R.id.no)
        nextBtn = findViewById(R.id.next_button)

        // 인슐린 시간 선택
        val insulinTime1RadioButton = findViewById<RadioButton>(R.id.insulinTime1)
        val insulinTime2RadioButton = findViewById<RadioButton>(R.id.insulinTime2)
        val insulinTime3RadioButton = findViewById<RadioButton>(R.id.insulinTime3)
        val insulinTime1Text = findViewById<TextView>(R.id.insulinTime1Text)
        val insulinTime2Text = findViewById<TextView>(R.id.insulinTime2Text)
        val insulinTime3Text = findViewById<TextView>(R.id.insulinTime3Text)

        insulinTime1RadioButton.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                insulinTime1 = LocalTime.of(hourOfDay, minute)
                insulinTime1Text.text = String.format("%02d:%02d", hourOfDay, minute)
            }
        }

        insulinTime2RadioButton.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                insulinTime2 = LocalTime.of(hourOfDay, minute)
                insulinTime2Text.text = String.format("%02d:%02d", hourOfDay, minute)
            }
        }

        insulinTime3RadioButton.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                insulinTime3 = LocalTime.of(hourOfDay, minute)
                insulinTime3Text.text = String.format("%02d:%02d", hourOfDay, minute)
            }
        }

        // 당뇨 여부 버튼 클릭 시 처리
        yesBtn.setOnClickListener {
            handleButtonClick(yesBtn)
            hasDiabetes = true
            checkNextButtonEnabled()
        }

        noBtn.setOnClickListener {
            handleButtonClick(noBtn)
            hasDiabetes = false
            checkNextButtonEnabled()
        }

        // 다음 화면으로 넘어가기
        nextBtn.setOnClickListener {
            // 권한 요청 및 처리
            checkAndRequestNotificationPermission()
        }
    }

    // 권한 요청 메서드
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATION
                )
            } else {
                scheduleInsulinNotificationIfNeeded()
                proceedToNextActivity()
            }
        } else {
            scheduleInsulinNotificationIfNeeded()
            proceedToNextActivity()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scheduleInsulinNotificationIfNeeded()
            } else {
                Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        proceedToNextActivity()
    }

    // 인슐린 알림 설정
    private fun scheduleInsulinNotificationIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleInsulinNotification()
            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        } else {
            scheduleInsulinNotification()
        }
    }

    // 알람 설정
    private fun scheduleInsulinNotification() {
        // 알람 매니저 초기화
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // insulinTime1에 대해 알람 설정
        scheduleNotificationForTime(alarmManager, insulinTime1, 1)

        // insulinTime2에 대해 알람 설정
        scheduleNotificationForTime(alarmManager, insulinTime2, 2)

        // insulinTime3에 대해 알람 설정
        scheduleNotificationForTime(alarmManager, insulinTime3, 3)
    }

    // 개별 인슐린 시간에 대한 알림 설정
    private fun scheduleNotificationForTime(alarmManager: AlarmManager, insulinTime: LocalTime?, requestCode: Int) {
        // null 체크 (null이면 알림 설정 X)
        insulinTime ?: return

        // 알림을 받을 PendingIntent 생성
        val intent = Intent(this, InsulinAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 현재 시간 가져오기
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        // 알람 시간 설정
        var alarmTime = insulinTime.atDate(now.toLocalDate()).atZone(ZoneId.systemDefault())
        if (alarmTime.isBefore(now)) {
            alarmTime = alarmTime.plusDays(1) // 이미 시간이 지났다면 다음날로 설정
        }

        // 알람 설정
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime.toEpochSecond() * 1000,
            pendingIntent
        )
    }

    // 시간 선택 다이얼로그 표시
    private fun showTimePickerDialog(onTimeSet: (Int, Int) -> Unit) {
        TimePickerDialog(
            this, AlertDialog.THEME_HOLO_LIGHT, { _, hourOfDay, minute ->
                onTimeSet(hourOfDay, minute)
            }, hour, min, true
        ).show()
    }

    // 다음 버튼 활성화 여부 확인
    private fun checkNextButtonEnabled() {
        nextBtn.isEnabled = (hasDiabetes != null)
    }

    // 당뇨 여부 버튼 클릭 처리
    private fun handleButtonClick(button: Button) {
        if (button == selectedButton) {
            button.isSelected = false
            selectedButton = null
            hasDiabetes = null
            enableAllButtons()
        } else {
            selectedButton?.isSelected = false
            button.isSelected = true
            selectedButton = button
            disableOtherButtons(button)
        }
        checkNextButtonEnabled()
    }

    // 모든 버튼 활성화
    private fun enableAllButtons() {
        val buttonIds = listOf(R.id.yes, R.id.no)
        buttonIds.forEach { id ->
            findViewById<Button>(id).isEnabled = true
        }
    }

    // 클릭된 버튼 이외의 버튼 비활성화
    private fun disableOtherButtons(selectedButton: Button) {
        val buttonIds = listOf(R.id.yes, R.id.no)
        buttonIds.forEach { id ->
            val button = findViewById<Button>(id)
            if (button != selectedButton) {
                button.isEnabled = false
            }
        }
    }

    // 화면에서 정보들 전달하기
    private fun proceedToNextActivity() {
        val intent = Intent(this, Register8Activity::class.java)
        intent.putExtra("PET_NAME", petName)
        intent.putExtra("PET_AGE", petAge)
        intent.putExtra("PET_BREED", petBreed)
        intent.putExtra("PET_WEIGHT", petWeight)
        intent.putExtra("PET_GENDER", petGender)
        intent.putExtra("PET_NEUTERED", petNeutered)
        intent.putExtra("PET_DIABETES", hasDiabetes)
        intent.putExtra("INSULIN_TIME1", insulinTime1?.toString() ?: "")
        intent.putExtra("INSULIN_TIME2", insulinTime2?.toString() ?: "")
        intent.putExtra("INSULIN_TIME3", insulinTime3?.toString() ?: "")

        Log.d("Register7Activity", "insulinTime1: $insulinTime1")
        Log.d("Register7Activity", "insulinTime2: $insulinTime2")
        Log.d("Register7Activity", "insulinTime3: $insulinTime3")

        startActivity(intent)
    }

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATION = 1
    }
}