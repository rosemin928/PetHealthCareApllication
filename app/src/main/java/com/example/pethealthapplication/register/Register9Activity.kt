package com.example.pethealthapplication.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.MainActivity
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.PetProfileDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofileapi.PetProfileApiClient
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.format.DateTimeParseException

class Register9Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register9)

        val registerBtn = findViewById<Button>(R.id.register_button)

        // 각 화면에서 정보 받아오기
        val nameText = findViewById<TextView>(R.id.nameText)
        val ageText = findViewById<TextView>(R.id.ageText)
        val breedText = findViewById<TextView>(R.id.breedText)
        val weightText = findViewById<TextView>(R.id.weightText)
        val genderText = findViewById<TextView>(R.id.genderText)
        val neuteringText = findViewById<TextView>(R.id.neuteringText)
        val diabetesText = findViewById<TextView>(R.id.diabetesText)
        val heartwormText = findViewById<TextView>(R.id.heartwormText)

        // 인슐린 시간 값 받아오기
        val insulinTime1 = intent.getStringExtra("INSULIN_TIME1")?.let {
            try {
                LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        val insulinTime2 = intent.getStringExtra("INSULIN_TIME2")?.let {
            try {
                LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        val insulinTime3 = intent.getStringExtra("INSULIN_TIME3")?.let {
            try {
                LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        // 날짜 값 받아오기
        val heartWormShotDate = intent.getStringExtra("INJECTION_DATE")?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        val heartWormMedicineDate = intent.getStringExtra("MEDICINE_DATE")?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        val petName: String? = intent.getStringExtra("PET_NAME")
        val petNameNonNull: String = petName ?: "기본값"
        nameText.text = petNameNonNull

        val petAgeString = intent.getStringExtra("PET_AGE")
        val petAge: Byte = petAgeString?.toByteOrNull() ?: 0
        ageText.text = "${petAge}살"

        val petBreed = intent.getStringExtra("PET_BREED")
        val petBreedNonNull: String = petBreed ?: "기본값"
        breedText.text = petBreedNonNull

        val petWeightString = intent.getStringExtra("PET_WEIGHT")
        val petWeight = petWeightString?.let { BigDecimal(it) } ?: BigDecimal.ZERO
        weightText.text = "${petWeight}kg"

        val petGenderString = intent.getStringExtra("PET_GENDER")
        val petGender: Char = petGenderString?.firstOrNull() ?: 'N'
        genderText.text = if (petGender == 'm') "남" else "여"

        val petNeutered = intent.getBooleanExtra("PET_NEUTERED", false)
        neuteringText.text = if (petNeutered) "O" else "X"

        val petDiabetes = intent.getBooleanExtra("PET_DIABETES", false)
        diabetesText.text = if (petDiabetes) "O" else "X"

        // heartwormText 설정 로직 수정
        heartwormText.text = when {
            heartWormShotDate != null -> heartWormShotDate.toString()
            heartWormMedicineDate != null -> heartWormMedicineDate.toString()
            else -> "기본값"
        }

        registerBtn.setOnClickListener {

            val petProfileDTO = PetProfileDTO(
                petNameNonNull,
                petBreedNonNull,
                petGender,
                petAge,
                petWeight,
                petNeutered,
                petDiabetes,
                insulinTime1?.toString() ?: "",
                insulinTime2?.toString() ?: "",
                insulinTime3?.toString() ?: "",
                heartWormShotDate?.toString() ?: "",
                heartWormMedicineDate?.toString() ?: ""
            )

            Log.d("Register9Activity", "insulinTime1: $insulinTime1")
            Log.d("Register9Activity", "insulinTime2: $insulinTime2")
            Log.d("Register9Activity", "insulinTime3: $insulinTime3")
            Log.d("Register9Activity", "injectionDate: $heartWormShotDate")
            Log.d("Register9Activity", "medicineDate: $heartWormMedicineDate")

            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("USER_ID_KEY", null)

            if (userId != null) {
                val petProfileApiService = PetProfileApiClient.getApiService(this)
                val call = petProfileApiService.petProfile(userId, petProfileDTO)

                Log.d("PetProfileActivity", "Stored UserId: $userId")

                call.enqueue(object : Callback<ResponseDTO<Any>> {
                    override fun onResponse(
                        call: Call<ResponseDTO<Any>>,
                        response: Response<ResponseDTO<Any>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val responseDTO = response.body()
                            if (responseDTO?.status == 200) {
                                Toast.makeText(this@Register9Activity, "등록 성공!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Register9Activity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@Register9Activity, "등록 실패: ${responseDTO?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (response.code() == 409) {
                                Toast.makeText(this@Register9Activity, "등록 실패: 이미 존재하는 반려동물 ID입니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Register9Activity, "서버 에러: ${response.message()}", Toast.LENGTH_SHORT).show()
                                Log.e("PetProfile", "Error Code: ${response.code()}, Message: ${response.message()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                        Toast.makeText(this@Register9Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}