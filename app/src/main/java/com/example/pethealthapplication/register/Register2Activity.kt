package com.example.pethealthapplication.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import com.example.pethealthapplication.R
import com.google.android.material.textfield.TextInputEditText

class Register2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)

        val nextBtn = findViewById<Button>(R.id.next_button)
        val petNameText = findViewById<TextInputEditText>(R.id.petName)


        //필드에 값이 있어야 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 여기는 사용하지 않음
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 여기는 사용하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val petName = petNameText.text.toString().trim()

                //모든 필드가 비어 있지 않을 때 버튼 활성화
                nextBtn.isEnabled = petName.isNotEmpty()
            }
        }
        petNameText.addTextChangedListener(textWatcher)




        //다음으로 넘어가기

        nextBtn.setOnClickListener {
            val petName = petNameText.text.toString()
            val intent = Intent(this, Register3Activity::class.java)
            intent.putExtra("PET_NAME", petName)

            startActivity(intent)
        }
    }
}