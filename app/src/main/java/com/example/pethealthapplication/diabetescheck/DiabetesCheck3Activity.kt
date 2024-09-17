package com.example.pethealthapplication.diabetescheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pethealthapplication.R

class DiabetesCheck3Activity : AppCompatActivity() {

    private var weightLossCheckButton: Button? = null
    private var isWeightLoss: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diabetes_check3)

        val yesButton = findViewById<Button>(R.id.yes)
        val noButton = findViewById<Button>(R.id.no)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        //정보 받아오기
        val isObesity = intent.getCharExtra("IS_OBESITY", 'y')
        val dailyWaterIntake = intent.getStringExtra("DAILY_WATER_INTAKE")
        val dailyFoodIntake = intent.getStringExtra("DAILY_FOOD_INTAKE")


        //버튼 중복 클릭 금지
        yesButton.setOnClickListener {
            handleWeightLossCheckButtonClick(yesButton)
            isWeightLoss = 'y'
        }
        noButton.setOnClickListener {
            handleWeightLossCheckButtonClick(noButton)
            isWeightLoss = 'n'
        }


        //다음 화면으로 넘어가기 + 정보 넘기기
        nextButton.setOnClickListener {
            intent = Intent(this@DiabetesCheck3Activity, DiabetesCheck4Activity::class.java)
            intent.putExtra("IS_OBESITY", isObesity)
            intent.putExtra("DAILY_WATER_INTAKE", dailyWaterIntake)
            intent.putExtra("DAILY_FOOD_INTAKE", dailyFoodIntake)
            intent.putExtra("IS_WEIGHT_LOSS", isWeightLoss)
            startActivity(intent)
        }

    }

    //버튼 중복 클릭 금지 이벤트
    private fun handleWeightLossCheckButtonClick(button: Button) {
        if (button == weightLossCheckButton) {
            button.isSelected = false
            weightLossCheckButton = null
        }
        else {
            weightLossCheckButton?.isSelected = false
            button.isSelected = true
            weightLossCheckButton = button
        }
    }
}