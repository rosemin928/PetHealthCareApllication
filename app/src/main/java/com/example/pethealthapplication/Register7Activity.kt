package com.example.pethealthapplication

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import java.util.Calendar

class Register7Activity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val day = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register7)

        val calendarBtn = findViewById<ImageView>(R.id.calendar_btn)
        val injectionText = findViewById<TextView>(R.id.injection_text)

        calendarBtn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, {_, year, month, day ->
                injectionText.text = "$year/${month + 1}/$day"
            }, year, month, day)
            datePickerDialog.show()
        }

        val next_button = findViewById<Button>(R.id.next_button)

        next_button.setOnClickListener {
            val intent = Intent(this@Register7Activity, MainActivity::class.java)

            startActivity(intent)
        }
    }
}