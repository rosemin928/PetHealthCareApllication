package com.example.pethealthapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.diabetesapi.DiabetesApiClient
import com.example.pethealthapplication.diabetesapi.DiabetesApiService
import com.example.pethealthapplication.diabetescheck.DiabetesCheck0Activity
import com.example.pethealthapplication.dto.DiabetesCheck2DTO
import com.example.pethealthapplication.dto.ObesityCheckDTO
import com.example.pethealthapplication.dto.PetProfileSummaryDTO
import com.example.pethealthapplication.dto.RecommendedKcalCheckDTO
import com.example.pethealthapplication.dto.RecommendedKcalDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.obesitycheck.ObesityCheck7Activity
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiService
import com.example.pethealthapplication.weightapi.WeightApiClient
import com.example.pethealthapplication.weightapi.WeightApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class Analysis2Fragment : Fragment() {

    private lateinit var petNameText: TextView
    private lateinit var dailyWaterIntakeText: TextView
    private lateinit var diabetesRiskText: TextView
    private lateinit var analysisDateText: TextView
    private lateinit var dailyRecommendedCalories: TextView
    private lateinit var happyDog: ImageView
    private lateinit var sadDog: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis2, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val recommendedNote = sharedPreferences.getString("RECOMMENDED_NOTE_KEY", null)

        val petApiService = PetProfileSummaryApiClient.getApiService(requireContext())
        val diabetesApiService = DiabetesApiClient.getApiService(requireContext())
        val weightApiService = WeightApiClient.getApiService(requireContext())

        // 반려동물 이름 조회
        petNameText = view.findViewById(R.id.petNameText)
        if (petId != null)
            fetchPetProfileSummary(petId, petApiService)

        // 당뇨 위험도 조회
        diabetesRiskText = view.findViewById(R.id.diabetesRiskText)
        analysisDateText = view.findViewById(R.id.analysisDateText)
        happyDog = view.findViewById(R.id.happyDog)
        sadDog = view.findViewById(R.id.sadDog)
        if (petId != null)
            diabetesCheck(petId, diabetesApiService)

        // 음수량 조회
        dailyWaterIntakeText = view.findViewById(R.id.dailyWaterIntakeText)
        if (petId != null)
            dailyWaterIntakeCheck(petId, diabetesApiService)

        //하루 적정 칼로리 조회
        dailyRecommendedCalories = view.findViewById(R.id.dailyRecommendedCalories)
        if (petId != null)
            recommendedCaloriesCheck(petId, weightApiService)

        //recommendedNote 조회
        val recommendedNoteText = view.findViewById<TextView>(R.id.recommendedNoteText)
        recommendedNoteText.text = recommendedNote

        //다시 분석하기
        val analysisButton = view.findViewById<Button>(R.id.analysisBtn)
        analysisButton.setOnClickListener {
            val intent = Intent(requireContext(), DiabetesCheck0Activity::class.java)
            startActivity(intent)
        }

        return view
    }

    //반려동물 프로필 요약 조회(이름을 가져오기 위함)
    private fun fetchPetProfileSummary(petId: String, apiService: PetProfileSummaryApiService) {
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
                        val petName = data?.petName

                        // UI 업데이트
                        petNameText.text = petName
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileSummaryDTO>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //평균 음수량 조회
    private fun dailyWaterIntakeCheck(petId: String, apiService: DiabetesApiService) {
        apiService.dailyWaterIntakeCheck(petId).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
            override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        val waterIntake = data.toString()

                        // UI 업데이트
                        dailyWaterIntakeText.text = waterIntake
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //당뇨 위험도 조회
    private fun diabetesCheck(petId: String, apiService: DiabetesApiService) {
        apiService.diabetesCheck(petId).enqueue(object : Callback<ResponseDTO<DiabetesCheck2DTO>> {
            override fun onResponse(call: Call<ResponseDTO<DiabetesCheck2DTO>>, response: Response<ResponseDTO<DiabetesCheck2DTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        val diabetesRisk = data?.diabetesRisk
                        val diabetesRiskCheckDate = data?.diabetesRiskCheckDate

                        // UI 업데이트
                        diabetesRiskText.text = when (diabetesRisk) {
                            "당뇨 의심" -> "당뇨 고위험군"
                            "당뇨 위험 보통" -> "당뇨 중위험군"
                            else -> "당뇨 저위험군"
                        }
                        analysisDateText.text = diabetesRiskCheckDate
                        when (diabetesRisk) {
                            "당뇨 의심" -> {
                                happyDog.visibility = View.GONE
                                sadDog.visibility = View.VISIBLE
                            }
                            else -> {
                                happyDog.visibility = View.VISIBLE
                                sadDog.visibility = View.GONE
                            }
                        }
                    } else {
                        // 실패 시 메시지 처리
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DiabetesCheck2DTO>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //하루 적정 칼로리 조회
    private fun recommendedCaloriesCheck(petId: String, apiService: WeightApiService) {
        apiService.recommendedCaloriesCheck(petId).enqueue(object : Callback<ResponseDTO<RecommendedKcalCheckDTO>> {
            override fun onResponse(call: Call<ResponseDTO<RecommendedKcalCheckDTO>>, response: Response<ResponseDTO<RecommendedKcalCheckDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    if (status == 200) {
                        val recommendedCalories = data?.recommendedCalories

                        //UI 업데이트
                        dailyRecommendedCalories.text = recommendedCalories?.toPlainString()
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "서버 에러: ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<RecommendedKcalCheckDTO>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}