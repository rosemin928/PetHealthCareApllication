package com.example.pethealthapplication.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pethealthapplication.MainActivity
import com.example.pethealthapplication.R

class Register0Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register0)

        val register_button = findViewById<Button>(R.id.register_button)

        register_button.setOnClickListener {
            val intent = Intent(this@Register0Activity, Register2Activity::class.java)

            startActivity(intent)
        }

        val later_button = findViewById<Button>(R.id.later_button)

        later_button.setOnClickListener {
            val intent = Intent(this@Register0Activity, MainActivity::class.java)

            startActivity(intent)
        }
    }
}