package com.example.pethealthapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        val logoutButton = findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener {
                // SharedPreferences 가져오기
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                // userId, token, petId 삭제
                editor.remove("USER_ID_KEY")
                editor.remove("TOKEN_KEY")
                editor.remove("PET_ID_KEY")

                // 변경사항 저장
                editor.apply() // 또는 editor.commit() 사용 가능

                // 로그아웃 후 로그인 화면으로 이동 또는 다른 동작 처리
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)

                // 현재 액티비티 종료
                finish()
        }
    }
}