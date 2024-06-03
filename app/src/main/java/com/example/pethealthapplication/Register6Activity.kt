package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Register6Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register6)

        val buttonIds = listOf(
            R.id.button,
            R.id.button2
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                it.isSelected = true // 클릭 시 선택 상태로 설정
            }
        }

        val next_button = findViewById<Button>(R.id.next_button)

        next_button.setOnClickListener {
            val intent = Intent(this@Register6Activity, Register7Activity::class.java)

            startActivity(intent)
        }
    }
}