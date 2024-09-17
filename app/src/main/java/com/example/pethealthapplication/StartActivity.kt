package com.example.pethealthapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pethealthapplication.register.Register0Activity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val intent = Intent(this@StartActivity, LoginActivity::class.java)

            startActivity(intent)
        }

        val joinBtn = findViewById<Button>(R.id.joinBtn)

        joinBtn.setOnClickListener {
            val intent = Intent(this@StartActivity, JoinIdActivity::class.java)

            startActivity(intent)
        }

    }

}