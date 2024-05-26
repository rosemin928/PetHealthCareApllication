package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class JoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val join_button = findViewById<Button>(R.id.join_button)

        join_button.setOnClickListener {
            val intent = Intent(this@JoinActivity, Register0Activity::class.java)

            startActivity(intent)
        }
    }
}