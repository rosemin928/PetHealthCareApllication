package com.example.pethealthapplication.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatButton
import com.example.pethealthapplication.MainActivity
import com.example.pethealthapplication.R

class Register6Activity : AppCompatActivity() {

    private var selectedButton: Button?= null
    private lateinit var nextBtn: Button
    private var selectedGender: String?= null
    private var isNeutered: Boolean?= null
    private var lastSelectedRadioButton: RadioButton?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register6)

        nextBtn = findViewById(R.id.next_button)
        val maleBtn = findViewById<Button>(R.id.male)
        val femaleBtn = findViewById<Button>(R.id.female)
        val yesBtn = findViewById<RadioButton>(R.id.yes)
        val noBtn = findViewById<RadioButton>(R.id.no)

        val petName = intent.getStringExtra("PET_NAME")
        val petAge = intent.getStringExtra("PET_AGE")
        val petBreed = intent.getStringExtra("PET_BREED")
        val petWeight = intent.getStringExtra("PET_WEIGHT")

        // 성별 버튼 클릭 시 처리
        maleBtn.setOnClickListener {
            handleButtonClick(maleBtn)
            selectedGender = if (maleBtn.isSelected) "m" else null
            checkNextButtonEnabled()
        }
        femaleBtn.setOnClickListener {
            handleButtonClick(femaleBtn)
            selectedGender = if (femaleBtn.isSelected) "f" else null
            checkNextButtonEnabled()
        }

        // 중성화 여부 버튼 클릭 시 처리
        yesBtn.setOnClickListener {
            handleRadioButtonClick(yesBtn)
            isNeutered = if (yesBtn.isChecked) true else null
            checkNextButtonEnabled()
        }
        noBtn.setOnClickListener {
            handleRadioButtonClick(noBtn)
            isNeutered = if (noBtn.isChecked) false else null
            checkNextButtonEnabled()
        }


        // 다음 버튼 클릭 시 처리
        nextBtn.setOnClickListener {
            val intent = Intent(this@Register6Activity, Register7Activity::class.java)
            intent.putExtra("PET_NAME", petName)
            intent.putExtra("PET_AGE", petAge)
            intent.putExtra("PET_BREED", petBreed)
            intent.putExtra("PET_WEIGHT", petWeight)
            intent.putExtra("PET_GENDER", selectedGender)
            intent.putExtra("PET_NEUTERED", isNeutered)
            startActivity(intent)
        }

    }

    // 다음 버튼 활성화 조건 확인
    private fun checkNextButtonEnabled() {
        nextBtn.isEnabled = (selectedGender != null && isNeutered != null)
    }

    // 성별 버튼 클릭 처리
    private fun handleButtonClick(button: Button) {
        if (button == selectedButton) {
            // 선택 해제
            button.isSelected = false
            selectedButton = null
            selectedGender = null  // 선택 해제 시 성별 초기화
            enableAllButtons()
        } else {
            // 새로운 버튼 선택
            selectedButton?.let {
                it.isSelected = false
            }
            button.isSelected = true
            selectedButton = button
            disableOtherButtons(button)
        }
        checkNextButtonEnabled() // 성별이 선택되었거나 해제되었을 때 nextBtn 상태 확인
    }

    // 라디오 버튼 클릭 처리
    private fun handleRadioButtonClick(button: RadioButton) {
        if (button == lastSelectedRadioButton) {
            // 동일한 라디오 버튼을 다시 클릭하면 선택 해제
            button.isChecked = false
            lastSelectedRadioButton = null
            isNeutered = null
        } else {
            // 새로운 라디오 버튼 선택
            lastSelectedRadioButton?.isChecked = false
            button.isChecked = true
            lastSelectedRadioButton = button
        }
        checkNextButtonEnabled() // 중성화 여부가 선택되었거나 해제되었을 때 nextBtn 상태 확인
    }

    // 모든 버튼 활성화
    private fun enableAllButtons() {
        val buttonIds = listOf(R.id.male, R.id.female)
        for (id in buttonIds) {
            val button = findViewById<Button>(id)
            button.isEnabled = true
        }
    }

    // 클릭된 버튼 이외의 버튼 비활성화
    private fun disableOtherButtons(selectedButton: Button) {
        val buttonIds = listOf(R.id.male, R.id.female)
        for (id in buttonIds) {
            val button = findViewById<Button>(id)
            if (button != selectedButton) {
                button.isEnabled = false
            }
        }
    }
}