package com.example.pethealthapplication.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.pethealthapplication.R

class Register4Activity : AppCompatActivity() {

    private var selectedButton: AppCompatButton? = null
    private lateinit var nextBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register4)

        nextBtn = findViewById(R.id.next_button)
        val petName = intent.getStringExtra("PET_NAME")
        val petAge = intent.getStringExtra("PET_AGE")


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



        //버튼 중복 클릭 불가
        for (id in buttonIds) {
            val button = findViewById<AppCompatButton>(id)
            button.setOnClickListener {
                handleButtonClick(button)
            }
        }




        //다음 화면으로 넘어가기
        nextBtn.setOnClickListener {
            val intent = Intent(this@Register4Activity, Register5Activity::class.java)

            //선택된 버튼의 텍스트를 다른 화면으로 넘기기
            selectedButton?.let {
                val buttonText = it.text.toString()
                intent.putExtra("PET_NAME", petName)
                intent.putExtra("PET_AGE", petAge)
                intent.putExtra("PET_BREED", buttonText)
            }

            startActivity(intent)
        }
    }


    //모든 버튼 활성화
    private fun enableAllButtons() {
        val buttonIds = listOf(
            R.id.button, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9, R.id.button10
        )

        for (id in buttonIds) {
            val button = findViewById<AppCompatButton>(id)
            button.isEnabled = true
        }
    }



    //클릭된 버튼 이외의 버튼 비활성화
    private fun disableOtherButtons(selectedButton: AppCompatButton) {
        val buttonIds = listOf(
            R.id.button, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9, R.id.button10
        )

        for (id in buttonIds) {
            val button = findViewById<AppCompatButton>(id)
            if (button != selectedButton) {
                button.isEnabled = false
            }
        }
    }



    // 이미 선택된 버튼 다시 클릭
    private fun handleButtonClick(button: AppCompatButton) {
        if (button == selectedButton) {
            // 버튼을 다시 클릭한 경우: 선택 해제
            button.isSelected = false
            selectedButton = null
            enableAllButtons()
            // 선택된 버튼이 없으므로 Next 버튼 비활성화
            nextBtn.isEnabled = false
        } else {
            // 새로운 버튼을 클릭한 경우
            if (selectedButton != null) {
                selectedButton?.isSelected = false // 이전에 선택된 버튼의 상태 해제
            }
            button.isSelected = true // 현재 버튼을 선택 상태로 설정
            selectedButton = button
            disableOtherButtons(button) // 다른 버튼을 비활성화
            // 버튼이 선택되었으므로 Next 버튼 활성화
            nextBtn.isEnabled = true
        }
    }

}