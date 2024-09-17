package com.example.pethealthapplication.obesitycheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pethealthapplication.R

class ObesityCheck5Activity : AppCompatActivity() {

    private var bcsButton: Button? = null
    private var bodyShape: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check5)

        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        //정보 받기
        val weightStatus = intent.getStringExtra("WEIGHT_STATUS")
        val waistRibVisibility = intent.getCharExtra("WAIST_RIB_VISIBILITY", 'y')
        val ribTouchability = intent.getCharExtra("RIB_TOUCHABILITY", 'y')

        //버튼 중복 클릭 금지
        button1.setOnClickListener {
            handleBcsButtonClick(button1)
            bodyShape = 1
        }
        button2.setOnClickListener {
            handleBcsButtonClick(button2)
            bodyShape = 2
        }
        button3.setOnClickListener {
            handleBcsButtonClick(button3)
            bodyShape = 3
        }

        //다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            intent = Intent(this@ObesityCheck5Activity, ObesityCheck6Activity::class.java)
            intent.putExtra("WEIGHT_STATUS", weightStatus)
            intent.putExtra("WAIST_RIB_VISIBILITY", waistRibVisibility)
            intent.putExtra("RIB_TOUCHABILITY", ribTouchability)
            intent.putExtra("BODY_SHAPE", bodyShape)
            startActivity(intent)
        }
    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleBcsButtonClick(button: Button) {
        if (button == bcsButton) {
            button.isSelected = false
            bcsButton = null
        }
        else {
            bcsButton?.isSelected = false
            button.isSelected = true
            bcsButton = button
        }
    }
}