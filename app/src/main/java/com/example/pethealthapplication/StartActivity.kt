package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val kakao_login_button = findViewById<ImageButton>(R.id.kakao_login_button)

        kakao_login_button.setOnClickListener {
            val intent = Intent(this@StartActivity, JoinActivity::class.java)

            startActivity(intent)
        }
    }

}