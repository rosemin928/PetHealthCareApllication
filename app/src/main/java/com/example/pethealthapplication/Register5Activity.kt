package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Register5Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register5)

        val next_button = findViewById<Button>(R.id.next_button)

        next_button.setOnClickListener {
            val intent = Intent(this@Register5Activity, Register6Activity::class.java)

            startActivity(intent)
        }
    }
}