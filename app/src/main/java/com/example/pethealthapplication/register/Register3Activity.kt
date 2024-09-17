package com.example.pethealthapplication.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import com.example.pethealthapplication.R
import com.google.android.material.textfield.TextInputEditText

class Register3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        val nextBtn = findViewById<Button>(R.id.next_button)
        val ageText = findViewById<TextInputEditText>(R.id.ageText)
        val petName = intent.getStringExtra("PET_NAME")


        //필드에 값이 있어야 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 여기는 사용하지 않음
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 여기는 사용하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val petAge = ageText.text.toString().trim()

                //모든 필드가 비어 있지 않을 때 버튼 활성화
                nextBtn.isEnabled = petAge.isNotEmpty()
            }
        }
        ageText.addTextChangedListener(textWatcher)



        //다음으로 넘어가기
        nextBtn.setOnClickListener {
            val age = ageText.text.toString()
            val intent = Intent(this@Register3Activity, Register4Activity::class.java)
            intent.putExtra("PET_NAME", petName)
            intent.putExtra("PET_AGE", age)

            startActivity(intent)
        }
    }
}