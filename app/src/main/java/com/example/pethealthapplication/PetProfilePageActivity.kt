package com.example.pethealthapplication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.dto.PetProfileDTO
import com.example.pethealthapplication.dto.PetProfileUpdateDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofileapi.PetProfileApiClient
import com.example.pethealthapplication.petprofileapi.PetProfileApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class PetProfilePageActivity : AppCompatActivity() {

    private lateinit var petNameText: EditText
    private lateinit var petBreedSpinner: Spinner
    private lateinit var petAgeText: EditText
    private lateinit var petWeightText: EditText
    private lateinit var femaleButton: Button
    private lateinit var maleButton: Button
    private lateinit var yesNeutered: RadioButton
    private lateinit var noNeutered: RadioButton
    private lateinit var yesDiabetes: Button
    private lateinit var noDiabetes: Button
    private lateinit var insulinTime1Layout: LinearLayout
    private lateinit var insulinTime1Text: TextView
    private lateinit var insulinTime2Layout: LinearLayout
    private lateinit var insulinTime2Text: TextView
    private lateinit var insulinTime3Layout: LinearLayout
    private lateinit var insulinTime3Text: TextView
    private lateinit var injection: RadioButton
    private lateinit var injectionText: TextView
    private lateinit var medicine: RadioButton
    private lateinit var medicineText: TextView
    private lateinit var saveBtn: Button

    private var genderSelectedButton: Button? = null
    private var diabetesSelectedButton: Button? = null
    private var neuteredSelectedButton: RadioButton? = null

    private val calendar = Calendar.getInstance()
    private val hour = calendar.get(Calendar.HOUR_OF_DAY)
    private val min = calendar.get(Calendar.MINUTE)
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val day = calendar.get(Calendar.DAY_OF_MONTH)
    private var heartWormShotDate: LocalDate? = null
    private var heartWormMedicineDate: LocalDate? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile_page)

        petNameText = findViewById(R.id.petNameText)
        petBreedSpinner = findViewById(R.id.petBreedSpinner)
        petAgeText = findViewById(R.id.petAgeText)
        petWeightText = findViewById(R.id.petWeightText)
        femaleButton = findViewById(R.id.female)
        maleButton = findViewById(R.id.male)
        yesNeutered = findViewById(R.id.yesNeutered)
        noNeutered = findViewById(R.id.noNeutered)
        yesDiabetes = findViewById(R.id.yesDiabetes)
        noDiabetes = findViewById(R.id.noDiabetes)
        insulinTime1Layout = findViewById(R.id.insulinTime1Layout)
        insulinTime1Text = findViewById(R.id.insulinTime1Text)
        insulinTime2Layout = findViewById(R.id.insulinTime2Layout)
        insulinTime2Text = findViewById(R.id.insulinTime2Text)
        insulinTime3Layout = findViewById(R.id.insulinTime3Layout)
        insulinTime3Text = findViewById(R.id.insulinTime3Text)
        injection = findViewById(R.id.injection)
        injectionText = findViewById(R.id.injectionText)
        medicine = findViewById(R.id.medicine)
        medicineText = findViewById(R.id.medicineText)

        saveBtn = findViewById(R.id.saveBtn)
        val deleteBtn = findViewById<Button>(R.id.deleteBtn)


        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        val apiService = PetProfileApiClient.getApiService(this)

        //반려동물 프로필 조회
        if (petId != null)
            fetchPetProfile(petId, apiService)



        // 품종 데이터를 배열로 정의
        val dogBreeds = arrayOf("치와와", "푸들(소형)", "포메", "말티즈", "비숑")
        val catBreeds = arrayOf("페르시안", "러시안블루", "코숏", "터키쉬앙고라", "스코티쉬폴드")

        // Spinner에 원래 설정되어 있는 값 가져오기 (fetchPetProfile에서 설정된 값)
        val currentBreed = petBreedSpinner.selectedItem?.toString() ?: ""

        // 어댑터를 설정할 배열을 결정
        val breedArray = if (dogBreeds.contains(currentBreed)) {
            dogBreeds
        } else if (catBreeds.contains(currentBreed)) {
            catBreeds
        } else {
            // 기본적으로 dogBreeds 배열을 사용하거나 필요에 따라 다른 처리를 추가할 수 있습니다.
            dogBreeds
        }

        // Spinner에 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, breedArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        petBreedSpinner.adapter = adapter

        // Spinner의 선택된 값을 currentBreed로 설정
        val currentBreedPosition = breedArray.indexOf(currentBreed)
        if (currentBreedPosition >= 0) {
            petBreedSpinner.setSelection(currentBreedPosition)
        }




        //성별 버튼 클릭 이벤트
        femaleButton.setOnClickListener {
            handleGenderButtonClick(femaleButton)
        }
        maleButton.setOnClickListener {
            handleGenderButtonClick(maleButton)
        }

        //당뇨 버튼 클릭 이벤트
        yesDiabetes.setOnClickListener {
            handleDiabetesButtonClick(yesDiabetes)
        }
        noDiabetes.setOnClickListener {
            handleDiabetesButtonClick(noDiabetes)
        }

        //중성화 버튼 클릭 이벤트
        yesNeutered.setOnClickListener {
            handleNeuteredButtonClick(yesNeutered)
        }
        noNeutered.setOnClickListener {
            handleNeuteredButtonClick(noNeutered)
        }



        //값이 모두 들어있어야 저장하기 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInputs()
            }
        }

        // Spinner의 선택 변경을 감지하는 리스너
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                validateInputs()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                validateInputs() // 아무것도 선택되지 않은 상태도 체크
            }
        }
        // TextWatcher와 Spinner의 리스너를 등록
        petNameText.addTextChangedListener(textWatcher)
        petAgeText.addTextChangedListener(textWatcher)
        petWeightText.addTextChangedListener(textWatcher)
        petBreedSpinner.onItemSelectedListener = itemSelectedListener



        //인슐린 시간 업데이트
        val insulinCalendar1 = findViewById<ImageView>(R.id.insulinCalendar1)
        val insulinCalendar2 = findViewById<ImageView>(R.id.insulinCalendar2)
        val insulinCalendar3 = findViewById<ImageView>(R.id.insulinCalendar3)

        insulinCalendar1.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                val second = 0 // 기본 0초로 설정
                insulinTime1Text.text = String.format("%02d:%02d:%02d", hourOfDay, minute, second)
            }
        }
        insulinCalendar2.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                val second = 0 // 기본 0초로 설정
                insulinTime2Text.text = String.format("%02d:%02d:%02d", hourOfDay, minute, second)
            }
        }
        insulinCalendar3.setOnClickListener {
            showTimePickerDialog { hourOfDay, minute ->
                val second = 0 // 기본 0초로 설정
                insulinTime3Text.text = String.format("%02d:%02d:%02d", hourOfDay, minute, second)
            }
        }




        //인슐린 시간 삭제
        val insulinDelete1 = findViewById<ImageView>(R.id.insulinDelete1)
        val insulinDelete2 = findViewById<ImageView>(R.id.insulinDelete2)
        val insulinDelete3 = findViewById<ImageView>(R.id.insulinDelete3)
        insulinDelete1.setOnClickListener {
            insulinTime1Text.text = null
        }
        insulinDelete2.setOnClickListener {
            insulinTime2Text.text = null
        }
        insulinDelete3.setOnClickListener {
            insulinTime3Text.text = null
        }



        // DatePickerDialog를 통해 날짜를 선택할 때 업데이트
        injection.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                heartWormShotDate = LocalDate.of(year, month + 1, day)
                injectionText.text = String.format("%02d-%02d-%02d", year, month + 1, day)

            }, year, month, day)
            datePickerDialog.show()
        }
        medicine.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                heartWormMedicineDate = LocalDate.of(year, month + 1, day)
                medicineText.text = String.format("%02d-%02d-%02d", year, month + 1, day)

            }, year, month, day)
            datePickerDialog.show()
        }


        //수정하기 버튼
        saveBtn.setOnClickListener {
            if (petId != null) {
                petProfileUpdate(petId, apiService)
            } else {
                Toast.makeText(this, "반려동물 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //삭제하기 버튼
        deleteBtn.setOnClickListener {
            if (petId != null) {
                petProfileDelete(petId, apiService)
            } else {
                Toast.makeText(this, "반려동물 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }


    //*기능*
    // 입력 필드들이 모두 채워졌는지 확인
    private fun validateInputs() {
        val petNameTextString = petNameText.text.toString()
        val petBreedSpinnerString = petBreedSpinner.selectedItem.toString()
        val petAgeTextString = petAgeText.text.toString()
        val petWeightTextString = petWeightText.text.toString()

        saveBtn.isEnabled = petNameTextString.isNotEmpty() && petBreedSpinnerString.isNotEmpty() && petAgeTextString.isNotEmpty() && petWeightTextString.isNotEmpty()
    }

    //반려동물 프로필 조회
    private fun fetchPetProfile(petId: String, apiService: PetProfileApiService) {
        apiService.petProfileCheck(petId).enqueue(object : Callback<ResponseDTO<PetProfileDTO>> {
            override fun onResponse(call: Call<ResponseDTO<PetProfileDTO>>, response: Response<ResponseDTO<PetProfileDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        val petName = data?.petName
                        val breed = data?.breed
                        val gender = data?.gender
                        val age = data?.age
                        val currentWeight = data?.currentWeight
                        val isNeutered = data?.isNeutered
                        val hasDiabetes = data?.hasDiabetes
                        val insulinTime1 = data?.insulinTime1
                        val insulinTime2 = data?.insulinTime2
                        val insulinTime3 = data?.insulinTime3
                        val heartWormShotDate = data?.heartwormShotDate
                        val heartWormMedicineDate = data?.heartwormMedicineDate

                        // UI 업데이트
                        petNameText.setText(petName)

                        // Spinner의 선택된 품종을 설정
                        val breedPosition = (petBreedSpinner.adapter as ArrayAdapter<String>).getPosition(breed)
                        petBreedSpinner.setSelection(breedPosition)

                        petAgeText.setText(age.toString())
                        petWeightText.setText(currentWeight.toString())

                        when (gender?.lowercaseChar()) {
                            'f' -> handleGenderButtonClick(femaleButton)
                            'm' -> handleGenderButtonClick(maleButton)
                        }

                        when (isNeutered) {
                            true -> handleNeuteredButtonClick(yesNeutered)
                            false -> handleNeuteredButtonClick(noNeutered)
                            null -> {
                                yesNeutered.isChecked = false
                                noNeutered.isChecked = false
                            }
                        }

                        when (hasDiabetes) {
                            true -> handleDiabetesButtonClick(yesDiabetes)
                            false -> handleDiabetesButtonClick(noDiabetes)
                            null -> {
                                yesDiabetes.isSelected = false
                                noDiabetes.isSelected = false
                            }
                        }

                        if (insulinTime1 != null) {
                            insulinTime1Text.text = insulinTime1          // insulinTime1 값을 텍스트로 설정
                        } else {
                            insulinTime1Text.text = null
                        }

                        if (insulinTime2 != null) {
                            insulinTime2Text.text = insulinTime2          // insulinTime1 값을 텍스트로 설정
                        } else {
                            insulinTime2Text.text = null
                        }

                        if (insulinTime3 != null) {
                            insulinTime3Text.text = insulinTime3          // insulinTime1 값을 텍스트로 설정
                        } else {
                            insulinTime3Text.text = null
                        }

                        // 심장사상충 주사/약 접종 날짜 UI 업데이트
                        when {
                            (heartWormShotDate != null) -> {
                                Log.d("PetProfilePageActivity", "HeartWormShotDate: $heartWormShotDate")
                                injection.isChecked = true
                                injectionText.text = heartWormShotDate.toString()
                            }
                            (heartWormMedicineDate != null) -> {
                                medicine.isChecked = true
                                medicineText.text = heartWormMedicineDate.toString()
                            }
                            else -> {
                                Log.d("PetProfilePageActivity", "HeartWormShotDate: $heartWormShotDate")
                                injection.isChecked = false
                                medicine.isChecked = false
                                injectionText.text = null
                                medicineText.text = null
                            }
                        }

                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@PetProfilePageActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PetProfilePageActivity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileDTO>>, t: Throwable) {
                Toast.makeText(
                    this@PetProfilePageActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //반려동물 프로필 수정
    private fun petProfileUpdate(petId: String, updateApiService: PetProfileApiService) {
        // 성별 확인
        val gender = if (femaleButton.isSelected) 'f' else 'm'

        // 중성화 여부 확인
        val isNeutered = yesNeutered.isChecked

        // 당뇨 여부 확인
        val hasDiabetes = yesDiabetes.isSelected

        // 반려동물의 품종, 나이, 체중 등의 텍스트 가져오기 (이름은 변경 불가)
        val petBreed = petBreedSpinner.selectedItem.toString()
        val petAge = petAgeText.text.toString().toByteOrNull() ?: 0
        val petWeight = petWeightText.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO

        // 인슐린 시간과 심장사상충 예방 날짜를 가져오기 (필요에 따라 추가)
        val insulinTime1 = insulinTime1Text.text.toString().takeIf { it.isNotBlank() }?.let {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        }
        val insulinTime2 = insulinTime2Text.text.toString().takeIf { it.isNotBlank() }?.let {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        }
        val insulinTime3 = insulinTime3Text.text.toString().takeIf { it.isNotBlank() }?.let {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        }

        // 날짜 형식을 정의합니다.
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // PetProfileDTO 객체 생성
        val petProfileUpdateDTO = PetProfileUpdateDTO(
            petBreed,
            gender,
            petAge,
            petWeight,
            isNeutered,
            hasDiabetes,
            insulinTime1?.toString() ?: "",
            insulinTime2?.toString() ?: "",
            insulinTime3?.toString() ?: "",
            heartWormShotDate?.format(dateFormatter) ?: "",  // 날짜 형식을 yyyy-MM-dd로 변환
            heartWormMedicineDate?.format(dateFormatter) ?: ""  // 날짜 형식을 yyyy-MM-dd로 변환
        )

        // API 호출을 통한 업데이트 요청
        updateApiService.petProfileUpdate(petId, petProfileUpdateDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                // 업데이트 성공/실패 처리
                if (response.isSuccessful) {
                    Toast.makeText(this@PetProfilePageActivity, "프로필 업데이트 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@PetProfilePageActivity, "프로필 업데이트 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(this@PetProfilePageActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //반려동물 프로필 삭제
    private fun petProfileDelete(petId: String, deleteApiService: PetProfileApiService) {
        deleteApiService.petProfileDelete(petId).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message

                    if (status == 200) {
                        Toast.makeText(this@PetProfilePageActivity, "반려동물 프로필 삭제 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(this@PetProfilePageActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PetProfilePageActivity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@PetProfilePageActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //성별 버튼 중복 클릭 불가
    private fun handleGenderButtonClick(button: Button) {
        if (button == genderSelectedButton) {
            button.isSelected = false
            genderSelectedButton = null
        }
        else {
            genderSelectedButton?.isSelected = false
            button.isSelected = true
            genderSelectedButton = button
        }
    }

    //당뇨 버튼 중복 클릭 불가
    private fun handleDiabetesButtonClick(button: Button) {
        if (button == diabetesSelectedButton) {
            button.isSelected = false
            diabetesSelectedButton = null
        }
        else {
            diabetesSelectedButton?.isSelected = false
            button.isSelected = true
            diabetesSelectedButton = button
        }
    }

    //중성화 버튼 중복 클릭 불가
    private fun handleNeuteredButtonClick(button: RadioButton) {
        if (button == neuteredSelectedButton) {
            button.isChecked = false
            neuteredSelectedButton = null
        }
        else {
            neuteredSelectedButton?.isChecked = false
            button.isChecked = true
            neuteredSelectedButton = button
        }
    }

    // 시간 선택 다이얼로그 표시
    private fun showTimePickerDialog(onTimeSet: (Int, Int) -> Unit) {
        TimePickerDialog(
            this, AlertDialog.THEME_HOLO_LIGHT, { _, hourOfDay, minute ->
                onTimeSet(hourOfDay, minute)
            }, hour, min, true
        ).show()
    }

}