package com.example.pethealthapplication.obesitycheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pethealthapplication.R

class ObesityCheck4Activity : AppCompatActivity() {

    private var ribTouchableButton: Button? = null
    private var ribTouchability: Char? = null
    private lateinit var nextButton: Button  // 변경: nextButton을 전역으로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check4)

        val yesButton = findViewById<Button>(R.id.yes)
        val noButton = findViewById<Button>(R.id.no)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        //정보 받기
        val weightStatus = intent.getStringExtra("WEIGHT_STATUS")
        val waistRibVisibility = intent.getCharExtra("WAIST_RIB_VISIBILITY", 'y')

        //버튼 중복 클릭 금지
        yesButton.setOnClickListener {
            handleRibTouchableButtonClick(yesButton, 'y')
        }
        noButton.setOnClickListener {
            handleRibTouchableButtonClick(noButton, 'n')
        }

        //다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            intent = Intent(this@ObesityCheck4Activity, ObesityCheck5Activity::class.java)
            intent.putExtra("WEIGHT_STATUS", weightStatus)
            intent.putExtra("WAIST_RIB_VISIBILITY", waistRibVisibility)
            intent.putExtra("RIB_TOUCHABILITY", ribTouchability)
            startActivity(intent)
        }
    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleRibTouchableButtonClick(button: Button, touchability: Char) {
        if (button == ribTouchableButton) {
            button.isSelected = false
            ribTouchableButton = null
            ribTouchability = null
        }
        else {
            ribTouchableButton?.isSelected = false
            button.isSelected = true
            ribTouchableButton = button
            ribTouchability = touchability
        }

        enableNextButton()
    }

    // yesButton 또는 noButton이 눌렸을 때만 nextButton 활성화
    private fun enableNextButton() {
        nextButton.isEnabled = ribTouchability != null
    }
}