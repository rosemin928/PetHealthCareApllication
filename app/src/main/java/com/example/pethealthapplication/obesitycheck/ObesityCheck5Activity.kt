package com.example.pethealthapplication.obesitycheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.PetProfileSummaryDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ObesityCheck5Activity : AppCompatActivity() {

    private lateinit var dogBcs: ImageView
    private lateinit var catBcs: ImageView

    private var bcsButton: Button? = null
    private var bodyShape: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obesity_check5)

        dogBcs = findViewById(R.id.dogBcs)
        catBcs = findViewById(R.id.catBcs)

        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val nextButton = findViewById<Button>(R.id.nextBtn)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val petApiService = PetProfileSummaryApiClient.getApiService(this)


        //정보 받기
        val weightStatus = intent.getStringExtra("WEIGHT_STATUS")
        val waistRibVisibility = intent.getCharExtra("WAIST_RIB_VISIBILITY", 'y')
        val ribTouchability = intent.getCharExtra("RIB_TOUCHABILITY", 'y')

        //bcs 사진 변경
        if (petId != null) {
            petProfileSummaryCheck(petId, petApiService)
        }

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

    //반려동물 프로필 조회(bcs 사진 변경 위함)
    private fun petProfileSummaryCheck(petId: String, apiService: PetProfileSummaryApiService) {
        apiService.petProfileSummary(petId).enqueue(object :
            Callback<ResponseDTO<PetProfileSummaryDTO>> {
            override fun onResponse(call: Call<ResponseDTO<PetProfileSummaryDTO>>, response: Response<ResponseDTO<PetProfileSummaryDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        if (data?.animalType == "Dog") {
                            dogBcs.visibility = View.VISIBLE
                            catBcs.visibility = View.GONE
                        } else {
                            dogBcs.visibility = View.GONE
                            catBcs.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@ObesityCheck5Activity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ObesityCheck5Activity, "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileSummaryDTO>>, t: Throwable) {
                Toast.makeText(this@ObesityCheck5Activity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}