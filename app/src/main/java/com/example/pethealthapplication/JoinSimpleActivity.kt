package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pethealthapplication.register.Register0Activity

class JoinSimpleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_simple)

        val join_button = findViewById<Button>(R.id.join_button)

        join_button.setOnClickListener {
            val intent = Intent(this@JoinSimpleActivity, Register0Activity::class.java)

            startActivity(intent)
        }
    }
}