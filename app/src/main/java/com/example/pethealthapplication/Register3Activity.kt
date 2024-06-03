package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Register3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        val next_button = findViewById<Button>(R.id.next_button)

        next_button.setOnClickListener {
            val intent = Intent(this@Register3Activity, Register4Activity::class.java)

            startActivity(intent)
        }
    }
}