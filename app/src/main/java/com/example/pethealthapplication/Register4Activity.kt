package com.example.pethealthapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pethealthapplication.R

class Register4Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register4)

        val buttonIds = listOf(
            R.id.button,
            R.id.button2,
            R.id.button3,
            R.id.button4,
            R.id.button5,
            R.id.button6,
            R.id.button7,
            R.id.button8,
            R.id.button9,
            R.id.button10
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                it.isSelected = true // 클릭 시 선택 상태로 설정
            }
        }

        val next_button = findViewById<Button>(R.id.next_button)

        next_button.setOnClickListener {
            val intent = Intent(this@Register4Activity, Register5Activity::class.java)

            startActivity(intent)
        }
    }
}