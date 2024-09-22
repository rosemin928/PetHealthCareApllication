package com.example.pethealthapplication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.pethealthapplication.dailyreportapi.DailyReportApiClient
import com.example.pethealthapplication.dailyreportapi.DailyReportApiService
import com.example.pethealthapplication.dto.DailyReportDTO
import com.example.pethealthapplication.dto.PetProfileSummaryDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiClient
import com.example.pethealthapplication.petprofilesummary.PetProfileSummaryApiService
import com.github.mikephil.charting.charts.LineChart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.LocalDate
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import com.example.pethealthapplication.dto.BloodSugarLevelDTO
import com.example.pethealthapplication.dto.PetWeightDTO
import com.example.pethealthapplication.dto.ResponseListDTO
import com.example.pethealthapplication.dto.TargetWeightDTO
import com.example.pethealthapplication.graph.GraphApiClient
import com.example.pethealthapplication.graph.GraphApiService
import com.example.pethealthapplication.weightapi.WeightApiService
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.text.Editable
import android.text.TextWatcher
import android.widget.PopupMenu
import android.widget.RelativeLayout
import com.example.pethealthapplication.dto.DailyWalkingRecordCheckDTO
import com.example.pethealthapplication.dto.RecommendedKcalCheckDTO
import com.example.pethealthapplication.obesitycheck.ObesityCheck0Activity
import com.example.pethealthapplication.register.Register2Activity
import com.example.pethealthapplication.weightapi.WeightApiClient
import com.github.mikephil.charting.components.LimitLine
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var petNameText: TextView
    private lateinit var petAgeText: TextView
    private lateinit var petWeightText: TextView

    private lateinit var obesityCheck: TextView
    private lateinit var obesityCheckArea: RelativeLayout
    private lateinit var recommendCaloriesLayout: LinearLayout
    private lateinit var recommendDateLayout: LinearLayout
    private lateinit var recommendedKcal: TextView
    private lateinit var recommendedDate: TextView

    private lateinit var calendarView: MaterialCalendarView

    private var memoDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃을 inflate하여 view를 생성합니다.
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val diagnosisText = view.findViewById<TextView>(R.id.diagnosisText)
        val medicineText = view.findViewById<TextView>(R.id.medicineText)
        val petKgText = view.findViewById<TextView>(R.id.petKgText)
        val bloodSugarText = view.findViewById<TextView>(R.id.bloodSugarText)
        val specialMemoText = view.findViewById<TextView>(R.id.specialMemoText)

        // 1. 반려동물 정보 요약 조회
        // onCreate 또는 onViewCreated 내 코드
        petNameText = view.findViewById(R.id.petName)
        petAgeText = view.findViewById(R.id.petAge)
        petWeightText = view.findViewById(R.id.petWeight)

        val newPetAddArea = view.findViewById<RelativeLayout>(R.id.newPetAddArea)
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID_KEY", null)

        val petApiService = PetProfileSummaryApiClient.getApiService(requireContext())
        val graphApiService = GraphApiClient.getApiService(requireContext())
        val weightApiService = WeightApiClient.getApiService(requireContext())

        val petId = sharedPreferences.getString("PET_ID_KEY", null)

        Log.d("HomeFragment", "userId: $userId")
        Log.d("HomeFragment", "petId: $petId")


        if (userId != null) {
            // SharedPreferences에서 petId 가져오기
            val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val petId = sharedPreferences.getString("PET_ID_KEY", null)

            if (petId == null) {
                // petId가 없을 경우에만 첫 번째 petId를 가져오기
                fetchFirstPetId(userId, petApiService) { firstPetId ->
                    if (firstPetId != null) {
                        // 첫 번째 petId로 UI 업데이트
                        fetchPetProfileSummary(firstPetId, petApiService) { petProfile ->
                            petProfile?.let {
                                petNameText.text = it.petName
                                petAgeText.text = "${it.age}살"
                                petWeightText.text = "${it.currentWeight}kg"
                            }
                        }

                        // 선택 가능한 petId 목록 가져오기
                        fetchPetIdsAndNames(userId, petApiService)

                        if (memoDate == null) {

                            memoDate = LocalDate.now()

                            // 해당 petId로 추가 정보 조회
                            fetchDailyReport(
                                sharedPreferences,
                                diagnosisText,
                                medicineText,
                                petKgText,
                                bloodSugarText,
                                specialMemoText
                            )
                        }

                        recommendedCaloriesCheck(firstPetId, weightApiService) // 비만도 체크 조회
                        fetchBloodSugarLevel(firstPetId, graphApiService) // 혈당 데이터 조회
                    } else {
                        Toast.makeText(requireContext(), "저장된 반려동물이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // SharedPreferences에 저장된 petId가 있는 경우, 이를 사용하여 UI 업데이트
                fetchPetProfileSummary(petId, petApiService) { petProfile ->
                    petProfile?.let {
                        petNameText.text = it.petName
                        petAgeText.text = "${it.age}살"
                        petWeightText.text = "${it.currentWeight}kg"
                    }
                }

                // 선택 가능한 petId 목록 가져오기
                fetchPetIdsAndNames(userId, petApiService)

                // 해당 petId로 추가 정보 조회
                fetchDailyReport(
                    sharedPreferences,
                    diagnosisText,
                    medicineText,
                    petKgText,
                    bloodSugarText,
                    specialMemoText
                )

                recommendedCaloriesCheck(petId, weightApiService) // 비만도 체크 조회
                fetchBloodSugarLevel(petId, graphApiService) // 혈당 데이터 조회
            }
        } else {
            Toast.makeText(requireContext(), "User ID가 없습니다.", Toast.LENGTH_SHORT).show()
        }





        // 2. 반려동물 새로 등록
        val newPetAddButton = view.findViewById<ImageView>(R.id.newPetAddBtn)
        newPetAddButton.setOnClickListener {
            val intent = Intent(requireContext(), Register2Activity::class.java)
            startActivity(intent)
        }




        // 3. 펫 변경 (드롭다운 메뉴 연결)
        val petNameArea = view.findViewById<LinearLayout>(R.id.petNameArea)

        fetchPetIdsAndNames(userId ?: "", petApiService)

        // petNameArea 클릭 시, 드롭다운으로 반려동물 선택
        petNameArea.setOnClickListener {
            // 이미 정의된 showPetNameDropdown 함수로 드롭다운 메뉴 생성
            val apiService = PetProfileSummaryApiClient.getApiService(requireContext())
            fetchPetIdsAndNames(userId ?: "", apiService)
        }






        // 4. 그래프
        val bloodSugarGraph = view.findViewById<TextView>(R.id.bloodSugarGraph)
        val weightGraph = view.findViewById<TextView>(R.id.weightGraph)

        if (petId != null) {

            //기본은 혈당그래프
            fetchBloodSugarLevel(petId, graphApiService)

            bloodSugarGraph.setOnClickListener {
                fetchBloodSugarLevel(petId, graphApiService)
            }
            weightGraph.setOnClickListener {
                fetchWeight(petId, graphApiService, weightApiService)
            }
        }
        else {
            updateLineChartWithBloodSugar(emptyList()) //petId가 없을 때는 빈 그래프
            updateLineChartWithWeight(emptyList(), null)
        }




        // 5. 반려동물 프로필 정보 페이지로 이동
        val profileEditButton = view.findViewById<ImageView>(R.id.profileEditBtn)
        profileEditButton.setOnClickListener {
            val intent = Intent(requireActivity(), PetProfilePageActivity::class.java)
            startActivity(intent)
        }



        // 6. 비만도 체크 버튼
        obesityCheckArea = view.findViewById(R.id.obesityCheckArea)
        obesityCheckArea.setOnClickListener {
            val intent = Intent(requireActivity(), ObesityCheck0Activity::class.java)
            startActivity(intent)
        }



        // 7. 하루 적정 칼로리 조회
        obesityCheck = view.findViewById(R.id.obesityCheck)
        recommendCaloriesLayout = view.findViewById(R.id.recommendCaloriesLayout)
        recommendDateLayout = view.findViewById(R.id.recommendDateLayout)
        recommendedKcal = view.findViewById(R.id.recommendedCalories)
        recommendedDate = view.findViewById(R.id.recommendedDate)

        if (petId != null)
            recommendedCaloriesCheck(petId, weightApiService)





        // 8. 목표 몸무게 설정 + 조회 + 삭제
        // 목표 몸무게 저장 + 수정
        val targetWeightAddButton = view.findViewById<ImageView>(R.id.targetWeightAddBtn)
        val targetWeightEditButton = view.findViewById<ImageView>(R.id.targetWeightEditBtn)
        targetWeightAddButton.setOnClickListener {
            if (petId != null)
                showTargetWeightDialog(petId, weightApiService)
        }
        targetWeightEditButton.setOnClickListener {
            if (petId != null)
                showTargetWeightDialog(petId, weightApiService)
        }

        //목표 몸무게 삭제
        val targetWeightDeleteButton = view.findViewById<ImageView>(R.id.targetWeightDeleteBtn)
        targetWeightDeleteButton.setOnClickListener {
            if (petId != null)
                targetWeightDelete(petId, weightApiService)
        }

        // 목표 몸무게 체크 및 UI 업데이트
        targetWeightCheck(petId ?: "", weightApiService) { targetWeight ->
            val targetWeightText = view.findViewById<TextView>(R.id.targetWeightText)
            val targetWeightShow = view.findViewById<TextView>(R.id.targetWeightShow)
            val targetWeightGuide1 = view.findViewById<TextView>(R.id.targetWeightGuide1)
            val targetWeightGuide2 = view.findViewById<TextView>(R.id.targetWeightGuide2)
            val targetWeightGuide3 = view.findViewById<TextView>(R.id.targetWeightGuide3)

            if (targetWeight != null) {
                currentWeightCheck(petId ?: "", weightApiService) { currentWeight ->
                    if (currentWeight != null) {
                        // 목표 몸무게가 존재할 때 targetWeigthText 숨기기
                        targetWeightText.visibility = View.GONE
                        targetWeightAddButton.visibility = View.GONE

                        targetWeightShow.visibility = View.VISIBLE

                        if (targetWeight >= currentWeight) {
                            targetWeightShow.text = String.format(Locale.getDefault(), "%.2f", targetWeight - currentWeight) // 목표 몸무게까지의 감량or증량 kg을 표시
                        } else {
                            targetWeightShow.text = String.format(Locale.getDefault(), "%.2f", currentWeight - targetWeight) // 목표 몸무게까지의 감량or증량 kg을 표시
                        }


                        targetWeightEditButton.visibility = View.VISIBLE
                        targetWeightDeleteButton.visibility = View.VISIBLE
                        targetWeightGuide1.visibility = View.VISIBLE
                        targetWeightGuide2.visibility = View.VISIBLE
                        targetWeightGuide3.visibility = View.VISIBLE
                    }
                }
            } else {
                // 목표 몸무게가 없으면 보여주기
                targetWeightText.visibility = View.VISIBLE
                targetWeightAddButton.visibility = View.VISIBLE

                targetWeightShow.visibility = View.GONE
                targetWeightEditButton.visibility = View.GONE
                targetWeightDeleteButton.visibility = View.GONE
                targetWeightGuide1.visibility = View.GONE
                targetWeightGuide2.visibility = View.GONE
                targetWeightGuide3.visibility = View.GONE
            }
        }




        // 9. 메모 추가하기 & 수정하기 & 삭제하기
        //(1) memoDateSelect 누르면 날짜 선택
        val memoDateSelect = view.findViewById<LinearLayout>(R.id.memoDateSelect)
        val memoDateSelectText = view.findViewById<TextView>(R.id.memoDateSelectText)

        // 오늘 날짜를 기본값으로 설정
        val currentDate = LocalDate.now()
        memoDate = currentDate
        memoDateSelectText.text = String.format(
            "%02d-%02d-%02d",
            currentDate.year,
            currentDate.monthValue,
            currentDate.dayOfMonth
        )

        // DatePickerDialog를 통해 날짜를 선택할 때 업데이트
        memoDateSelect.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
                memoDate = LocalDate.of(year, month + 1, day)
                memoDateSelectText.text = String.format("%02d-%02d-%02d", year, month + 1, day)

                // 날짜가 변경될 때마다 메모 조회 및 버튼 가시성 설정
                fetchDailyReport(
                    sharedPreferences,
                    diagnosisText,
                    medicineText,
                    petKgText,
                    bloodSugarText,
                    specialMemoText
                )
            }, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)
            datePickerDialog.show()
        }

        //(2) addButton -> 메모 추가
        //    editButton -> 메모 수정
        //    deleteButton -> 메모 삭제
        val memoAddButton = view.findViewById<ImageView>(R.id.memoAddBtn)
        val memoEditButton = view.findViewById<ImageView>(R.id.memoEditBtn)
        val memoDeleteButton = view.findViewById<ImageView>(R.id.memoDeleteBtn)

        // 메모 추가 버튼 클릭 시
        memoAddButton.setOnClickListener {
            val intent = Intent(requireContext(), DailyReportActivity::class.java)
            intent.putExtra("ACTION_TYPE", "ADD") // 추가 동작
            intent.putExtra("DATE", memoDate.toString()) // 필요에 따라 날짜 또는 다른 식별자 전달
            startActivity(intent)
        }

        // 메모 수정 버튼 클릭 시
        memoEditButton.setOnClickListener {
            val intent = Intent(requireContext(), DailyReportActivity::class.java)
            intent.putExtra("ACTION_TYPE", "EDIT") // 수정 동작
            intent.putExtra("DATE", memoDate.toString()) // 필요에 따라 날짜 또는 다른 식별자 전달
            startActivity(intent)
        }

        memoDeleteButton.setOnClickListener {
            val dailyReportApiService = DailyReportApiClient.getApiService(requireContext())
            memoDelete(sharedPreferences, dailyReportApiService)
        }



        // 10. 날짜별 산책 기록
        // (1) 캘린더 커스텀
        val dailyReportApiService = DailyReportApiClient.getApiService(requireContext())
        calendarView = view.findViewById(R.id.calendarView)

        // 초기 설정
        calendarView.addDecorator(CalendarDecorators.getTodayDecorator(requireContext()))
        calendarView.addDecorator(CalendarDecorators.getSelectedMonthDecorator(CalendarDay.today().month, requireContext()))

        // 처음 달이 표시될 때 기록을 가져와 파란 점 표시
        fetchWalkingRecordsAndApplyDots(petId ?: "", calendarView, dailyReportApiService, requireContext())


        // 달이 변경될 때마다 데코레이터 업데이트
        calendarView.setOnMonthChangedListener { _, newMonth ->
            calendarView.removeDecorators() // 기존 데코레이터 제거

            calendarView.addDecorator(CalendarDecorators.getTodayDecorator(requireContext()))
            calendarView.addDecorator(CalendarDecorators.getSelectedMonthDecorator(newMonth.month, requireContext()))
        }


        // (2) 날짜별 기록
        calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                // 선택된 날짜 가져오기
                val selectedDate = date.date

                // 선택된 날짜 데코레이터 적용
                calendarView.addDecorator(CalendarDecorators.SelectedDayDecorator(date, requireContext()))

                // petId가 존재하면 해당 날짜에 대한 산책 기록 조회
                if (petId != null)
                    checkTargetWalkingTimeAndNavigate(
                        petId,
                        dailyReportApiService,
                        selectedDate.toString()
                    )
            }
        }


        // (3) 목표 산책 시간 & 시각 조회
        if (petId != null)
            targetWalkingScheduleTimeCheck(petId, dailyReportApiService) { walkingTime, scheduleTime ->
                val walkingScheduleText = view?.findViewById<TextView>(R.id.walkingScheduleText)
                val targetWalkingTimeText = view?.findViewById<TextView>(R.id.targetWalkingTimeText)

                // 목표 산책 시간 텍스트뷰 설정
                if (walkingTime != null) {
                    targetWalkingTimeText?.text = walkingTime
                } else {
                    targetWalkingTimeText?.text = "목표 산책 시간"
                }

                // 산책 시각 텍스트뷰 설정
                if (scheduleTime != null) {
                    walkingScheduleText?.text = "매일 $scheduleTime"
                } else {
                    walkingScheduleText?.text = "산책 시각"
                }
            }

        // (4) 목표 산책 시간 & 시각 설정
        val targetTimeEditButton = view.findViewById<ImageView>(R.id.targetTimeEditBtn)
        targetTimeEditButton.setOnClickListener {
            val intent = Intent(requireContext(), TargetWalkingTimeActivity::class.java)
            startActivity(intent)
        }

        // (5) 목표 산책 시간 & 시각 삭제
        val targetTimeDeleteButton = view.findViewById<ImageView>(R.id.targetTimeDeleteBtn)
        targetTimeDeleteButton.setOnClickListener {
            if (petId != null)
                targetWalkingScheduleTimeDelete(petId,dailyReportApiService)
        }
    }


    //*기능*
    //메모 조회
    private fun fetchDailyReport(
        sharedPreferences: SharedPreferences,
        diagnosisText: TextView?,
        medicineText: TextView?,
        petKgText: TextView?,
        bloodSugarText: TextView?,
        specialMemoText: TextView?
    ) {
        val dailyReportCheckApiService = DailyReportApiClient.getApiService(requireContext())
        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val memoDateString = memoDate?.toString() ?: ""

        // 버튼들 가져오기
        val editButton = view?.findViewById<ImageView>(R.id.memoEditBtn)
        val deleteButton = view?.findViewById<ImageView>(R.id.memoDeleteBtn)
        val addButton = view?.findViewById<ImageView>(R.id.memoAddBtn)

        if (petId != null) {
            dailyReportCheckApiService.dailyReportCheck(petId, memoDateString)
                .enqueue(object : Callback<ResponseDTO<DailyReportDTO>> {
                    override fun onResponse(
                        call: Call<ResponseDTO<DailyReportDTO>>,
                        response: Response<ResponseDTO<DailyReportDTO>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val responseDTO = response.body()
                            val data = responseDTO?.data

                            if (responseDTO?.status == 200 && data != null) {
                                // 각 필드 값이 null이거나 빈 문자열인지 확인하고, 해당 경우 "입력값이 없습니다"로 설정
                                diagnosisText?.text =
                                    if (data.diagnosis.isNullOrBlank()) "입력값이 없습니다" else data.diagnosis

                                medicineText?.text =
                                    if (data.medicine.isNullOrBlank()) "입력값이 없습니다" else data.medicine

                                petKgText?.text =
                                    if (data.weight == null || data.weight == BigDecimal.ZERO) "입력값이 없습니다" else data.weight.toString()

                                bloodSugarText?.text =
                                    if (data.bloodSugarLevel == null) "입력값이 없습니다" else data.bloodSugarLevel.toString()

                                specialMemoText?.text =
                                    if (data.specialNote.isNullOrBlank()) "입력값이 없습니다" else data.specialNote

                                // 데이터가 존재할 경우 editBtn과 deleteBtn 보이기, addBtn 숨기기
                                editButton?.visibility = View.VISIBLE
                                deleteButton?.visibility = View.VISIBLE
                                addButton?.visibility = View.GONE
                            } else {
                                // 메모가 없을 때 UI 초기화
                                diagnosisText?.text = "입력값이 없습니다"
                                medicineText?.text = "입력값이 없습니다"
                                petKgText?.text = "입력값이 없습니다"
                                bloodSugarText?.text = "입력값이 없습니다"
                                specialMemoText?.text = "입력값이 없습니다"

                                // 데이터가 없을 경우 editBtn과 deleteBtn 숨기기, addBtn 보이기
                                editButton?.visibility = View.GONE
                                deleteButton?.visibility = View.GONE
                                addButton?.visibility = View.VISIBLE
                            }
                        } else {
                            // 메모가 없을 때 UI 초기화
                            diagnosisText?.text = "입력값이 없습니다"
                            medicineText?.text = "입력값이 없습니다"
                            petKgText?.text = "입력값이 없습니다"
                            bloodSugarText?.text = "입력값이 없습니다"
                            specialMemoText?.text = "입력값이 없습니다"

                            // 데이터가 없을 경우 editBtn과 deleteBtn 숨기기, addBtn 보이기
                            editButton?.visibility = View.GONE
                            deleteButton?.visibility = View.GONE
                            addButton?.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<DailyReportDTO>>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


    //메모 삭제
    private fun memoDelete(sharedPreferences: SharedPreferences, apiService: DailyReportApiService) {

        val petId = sharedPreferences.getString("PET_ID_KEY", null)
        val memoDateString = memoDate?.toString() ?: ""

        if (petId != null) {
            apiService.dailyReportDelete(petId, memoDateString)
                .enqueue(object : Callback<ResponseDTO<Any>> {
                    override fun onResponse(
                        call: Call<ResponseDTO<Any>>,
                        response: Response<ResponseDTO<Any>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val responseDTO = response.body()

                            if (responseDTO?.status == 200) {
                                // 요청 성공 시 토스트 메시지 표시
                                Toast.makeText(requireContext(), "메모 삭제 성공", Toast.LENGTH_SHORT).show()

                                // 메모 삭제 후 UI 초기화
                                fetchDailyReport(
                                    sharedPreferences,
                                    view?.findViewById(R.id.diagnosisText),
                                    view?.findViewById(R.id.medicineText),
                                    view?.findViewById(R.id.petKgText),
                                    view?.findViewById(R.id.bloodSugarText),
                                    view?.findViewById(R.id.specialMemoText)
                                )
                            }
                        } else {
                            // 실패 시 토스트 메시지 표시
                            Toast.makeText(requireContext(), "메모 삭제 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


    //혈당 데이터 조회
    private fun fetchBloodSugarLevel(petId: String, apiService: GraphApiService) {
        apiService.bloodSugarGraph(petId)
            .enqueue(object : Callback<ResponseListDTO<BloodSugarLevelDTO>> {
                override fun onResponse(call: Call<ResponseListDTO<BloodSugarLevelDTO>>, response: Response<ResponseListDTO<BloodSugarLevelDTO>>) {
                    val entries = ArrayList<Entry>() // 데이터 포인트 리스트 초기화

                    if (response.isSuccessful && response.body() != null) {
                        val responseDTO = response.body()
                        val status = responseDTO?.status
                        val data = responseDTO?.data

                        if (status == 200 && data != null && data.isNotEmpty()) {
                            // recordDate를 기준으로 정렬
                            val sortedData = data.sortedBy { it.recordDate }

                            // 최근 데이터 최대 7개만 사용하여 그래프 표시
                            sortedData.reversed().forEachIndexed { index, bloodSugarLevelDTO ->
                                if (index < 7) {
                                    val xValue = index.toFloat()
                                    val yValue = bloodSugarLevelDTO.bloodSugarLevel.toFloat() // Y축 값
                                    entries.add(Entry(xValue, yValue))
                                }
                            }

                            // 그래프에 데이터를 표시
                            updateLineChartWithBloodSugar(entries)
                        }
                    } else {
                        Toast.makeText(requireContext(), "응답 실패", Toast.LENGTH_SHORT).show()
                    }

                    // 데이터를 업데이트 (데이터가 없더라도 기본 틀을 그립니다)
                    updateLineChartWithBloodSugar(entries)
                }

                override fun onFailure(call: Call<ResponseListDTO<BloodSugarLevelDTO>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    // 실패 시에도 빈 데이터로 기본 틀을 그립니다.
                    updateLineChartWithBloodSugar(emptyList())
                }
            })
    }


    //혈당 그래프를 업데이트
    private fun updateLineChartWithBloodSugar(entries: List<Entry>) {
        val lineChart = requireView().findViewById<LineChart>(R.id.lineChart)

        // 만약 entries가 비어있으면 빈 MutableList를 생성해서 처리
        val mutableEntries = if (entries.isEmpty()) mutableListOf(Entry(0f, 0f)) else entries.toMutableList()

        val dataSet = LineDataSet(mutableEntries, "혈당 수치").apply {
            color = Color.parseColor("#FFBF00")
            setCircleColor(Color.parseColor("#FFBF00"))
            circleRadius = 5f
            lineWidth = 3f
            mode = LineDataSet.Mode.LINEAR
        }

        // x축 및 y축 범위 설정
        lineChart.xAxis.apply {
            axisMinimum = 0f  // x축 최소값
            axisMaximum = 6f // x축 최대값
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f // X축 라벨 간격 설정 (1 간격으로 설정)
            setLabelCount(7, true) // 정확히 7개의 라벨을 설정

            // X축 라벨
            valueFormatter = IndexAxisValueFormatter(arrayOf("1", "2", "3", "4", "5", "6", "7"))
        }

        lineChart.axisLeft.apply {
            axisMinimum = 40f  // y축 최소값
            axisMaximum = 100f  // y축 최대값
        }

        // 오른쪽 y축 숨기기
        lineChart.axisRight.isEnabled = false

        // LineData 생성 및 설정
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // 그래프 갱신
        lineChart.invalidate()
    }


    //몸무게 데이터 조회
    private fun fetchWeight(petId: String, graphApiService: GraphApiService, weightApiService: WeightApiService) {
        // 먼저 목표 몸무게를 가져오는 API 호출
        targetWeightCheck(petId, weightApiService) { targetWeight ->
            // 목표 몸무게를 가져온 후에 몸무게 데이터를 가져오는 API 호출
            graphApiService.weightGraph(petId).enqueue(object : Callback<ResponseListDTO<PetWeightDTO>> {
                override fun onResponse(call: Call<ResponseListDTO<PetWeightDTO>>, response: Response<ResponseListDTO<PetWeightDTO>>) {
                    val entries = ArrayList<Entry>() // 데이터 포인트 리스트 초기화

                    if (response.isSuccessful && response.body() != null) {
                        val responseDTO = response.body()
                        val status = responseDTO?.status
                        val data = responseDTO?.data

                        if (status == 200 && data != null && data.isNotEmpty()) {
                            // recordDate를 기준으로 정렬
                            val sortedData = data.sortedBy { it.weightRecordDate }

                            // 최근 데이터 최대 7개만 사용하여 그래프 표시
                            sortedData.forEachIndexed { index, petWeightDTO ->
                                if (index < 7) {
                                    val xValue = index.toFloat()
                                    val yValue = petWeightDTO.weight.toFloat() // Y축 값
                                    entries.add(Entry(xValue, yValue))
                                }
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "응답 실패", Toast.LENGTH_SHORT).show()
                    }

                    // 그래프에 데이터를 표시
                    updateLineChartWithWeight(entries, targetWeight)
                }

                override fun onFailure(call: Call<ResponseListDTO<PetWeightDTO>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("몸무게 업데이트", "")
                    // 데이터 값이 비어있어도 그래프는 표시
                    updateLineChartWithWeight(emptyList(), targetWeight)
                }
            })
        }
    }


    //몸무게 그래프 업데이트
    private fun updateLineChartWithWeight(entries: List<Entry>, targetWeight: BigDecimal?) {
        val lineChart = requireView().findViewById<LineChart>(R.id.lineChart)

        // 만약 entries가 비어있으면 빈 MutableList를 생성해서 처리
        val mutableEntries = if (entries.isEmpty()) mutableListOf(Entry(0f, 0f)) else entries.toMutableList()

        val dataSet = LineDataSet(mutableEntries, "몸무게").apply {
            color = Color.parseColor("#FFBF00")
            setCircleColor(Color.parseColor("#FFBF00"))
            circleRadius = 5f
            lineWidth = 3f
            mode = LineDataSet.Mode.LINEAR
        }

        // x축 및 y축 범위 설정
        lineChart.xAxis.apply {
            axisMinimum = 0f  // x축 최소값
            axisMaximum = 6f // x축 최대값
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f // X축 라벨 간격 설정 (1 간격으로 설정)
            setLabelCount(7, true) // 정확히 7개의 라벨을 설정

            // X축 라벨
            valueFormatter =
                IndexAxisValueFormatter(arrayOf("1", "2", "3", "4", "5", "6", "7"))
        }

        lineChart.axisLeft.apply {
            axisMinimum = 0f  // y축 최소값
            axisMaximum = 15f  // y축 최대값

            // 기존의 모든 LimitLine 제거
            removeAllLimitLines()

            // targetWeight가 null이 아닐 때만 LimitLine 추가
            targetWeight?.let { weight ->
                val limitLine = LimitLine(weight.toFloat(), "목표 몸무게").apply {
                    lineColor = Color.parseColor("#B40404")
                    lineWidth = 2f
                    /*enableDashedLine(10f, 10f, 0f) // 대시 선 설정(점선)*/
                }
                addLimitLine(limitLine)
            }
        }

        // 오른쪽 y축 숨기기
        lineChart.axisRight.isEnabled = false

        // LineData 생성 및 설정
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // 그래프 갱신
        lineChart.invalidate()
    }


    //목표 몸무게 다이얼로그
    private fun showTargetWeightDialog(petId: String, apiService: WeightApiService) {
        // 다이얼로그 레이아웃을 인플레이트
        val dialogView = layoutInflater.inflate(R.layout.target_weight_dialog, null)
        val editTextWeight = dialogView.findViewById<EditText>(R.id.targetWeightArea)
        val saveButton = dialogView.findViewById<Button>(R.id.saveBtn)

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // EditText의 텍스트가 변할 때마다 감지
        editTextWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val weightText = editTextWeight.text.toString()
                saveButton.isEnabled = try {
                    val weight = BigDecimal(weightText)
                    weight.signum() > 0 // 양수인지 확인
                } catch (e: Exception) {
                    false // 변환 불가능하면 비활성화 유지
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 저장 버튼 클릭 시 처리 로직
        saveButton.setOnClickListener {
            val weightText = editTextWeight.text.toString()
            try {
                val weight = BigDecimal(weightText)

                // API 호출 및 성공 시 다이얼로그 닫기
                targetWeightUpdate(petId, apiService, weight) {
                    // API 성공 시 다이얼로그 닫기
                    Toast.makeText(requireContext(), "목표 몸무게 업데이트 완료", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // 다이얼로그 닫기
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "유효한 몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }


    //목표 몸무게 저장 + 수정
    private fun targetWeightUpdate(petId: String, apiService: WeightApiService, weight: BigDecimal, onSuccess: () -> Unit) {

        val targetWeightDTO = TargetWeightDTO(weight)

        apiService.targetWeightUpdate(petId, targetWeightDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                // 업데이트 성공/실패 처리
                if (response.isSuccessful) {
                    onSuccess() // 성공 시 콜백 호출
                } else {
                    Toast.makeText(requireContext(), "목표 몸무게 업데이트 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    //목표 몸무게 조회
    private fun targetWeightCheck(petId: String, apiService: WeightApiService, callback: (BigDecimal?) -> Unit) {
        apiService.targetWeightCheck(petId).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
            override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val data = responseDTO?.data

                    if (status == 200 && data != null) {
                        // 목표 몸무게를 callback으로 전달
                        callback(data)
                    } else {
                        // 실패 시 null 전달
                        callback(null)
                    }
                } else {
                    // 실패 시 null 전달
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<BigDecimal>>, t: Throwable) {
                // 네트워크 오류 및 예외 정보를 로그에 출력
                Log.e("목표 몸무게 조회", "네트워크 오류: ${t.message}", t)
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                // 실패 시 null 전달
                callback(null)
            }
        })
    }


    //목표 몸무게 삭제
    private fun targetWeightDelete(petId: String, apiService: WeightApiService) {
        apiService.targetWeightDelete(petId).enqueue(object : Callback<ResponseDTO<Any>> {
                override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val responseDTO = response.body()

                        if (responseDTO?.status == 200) {
                            // 요청 성공 시 토스트 메시지 표시
                            Toast.makeText(requireContext(), "메모 삭제 성공", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // 실패 시 토스트 메시지 표시
                        Toast.makeText(requireContext(), "메모 삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    //현재 몸무게 조회
    private fun currentWeightCheck(petId: String, apiService: WeightApiService, callback: (BigDecimal?) -> Unit) {
        apiService.currentWeightCheck(petId).enqueue(object : Callback<ResponseDTO<BigDecimal>> {
                override fun onResponse(call: Call<ResponseDTO<BigDecimal>>, response: Response<ResponseDTO<BigDecimal>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val responseDTO = response.body()
                        val status = responseDTO?.status
                        val data = responseDTO?.data

                        if (status == 200 && data != null) {
                            callback(data)
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
                        val calRecommendedCaloriesDate = data?.calRecommendedCaloriesDate
                        val recommendedCalories = data?.recommendedCalories

                        if (calRecommendedCaloriesDate == null && recommendedCalories == null) {
                            // 둘 다 null인 경우 TextView를 보이게 함
                            obesityCheck.visibility = View.VISIBLE
                            recommendCaloriesLayout.visibility = View.GONE
                            recommendDateLayout.visibility = View.GONE
                        } else {
                            // 둘 다 null이 아닌 경우 TextView를 안 보이게 함
                            obesityCheck.visibility = View.GONE
                            recommendCaloriesLayout.visibility = View.VISIBLE
                            recommendDateLayout.visibility = View.VISIBLE

                            recommendedKcal.text = recommendedCalories.toString()
                            recommendedDate.text = calRecommendedCaloriesDate
                        }
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


    //목표 산책 시간 & 시각 삭제
    private fun targetWalkingScheduleTimeDelete(petId: String, apiService: DailyReportApiService) {
        // 첫 번째 API 호출 (목표 산책 시간 업데이트)
        apiService.targetWalkingTimeDelete(petId).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful) {
                    // 첫 번째 요청이 성공하면 두 번째 API 호출 (목표 산책 시각 업데이트)
                    apiService.walkingScheduleDelete(petId).enqueue(object :
                        Callback<ResponseDTO<Any>> {
                        override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                            if (response.isSuccessful) {
                                // 두 번째 요청이 성공하면 성공 메시지 표시
                                Toast.makeText(requireContext(), "삭제 성공", Toast.LENGTH_SHORT).show()
                            } else {
                                // 두 번째 요청이 실패하면 실패 메시지 표시
                                Toast.makeText(requireContext(), "삭제 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                            // 두 번째 요청이 네트워크 오류일 경우 처리
                            Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // 첫 번째 요청이 실패하면 실패 메시지 표시
                    Toast.makeText(requireContext(), "삭제 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                // 첫 번째 요청이 네트워크 오류일 경우 처리
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    //목표 산책 시간 & 시각 조회
    private fun targetWalkingScheduleTimeCheck(
        petId: String,
        apiService: DailyReportApiService,
        callback: (String?, String?) -> Unit
    ) {
        // 첫 번째 API 호출 (목표 산책 시간 확인)
        apiService.targetWalkingTimeCheck(petId).enqueue(object : Callback<ResponseDTO<Short>> {
            override fun onResponse(call: Call<ResponseDTO<Short>>, response: Response<ResponseDTO<Short>>) {
                if (response.isSuccessful) {
                    val targetWalkingTime = response.body()?.data
                    if (targetWalkingTime != null) {
                        // 분을 시간과 분으로 변환
                        val hours = targetWalkingTime / 60
                        val minutes = targetWalkingTime % 60
                        val formattedWalkingTime = "${hours}시간 ${minutes}분"

                        // 두 번째 API 호출 (목표 산책 시각 확인)
                        apiService.walkingScheduleCheck(petId).enqueue(object : Callback<ResponseDTO<String>> {
                            override fun onResponse(call: Call<ResponseDTO<String>>, response: Response<ResponseDTO<String>>) {
                                if (response.isSuccessful) {
                                    val walkingSchedule = response.body()?.data
                                    if (walkingSchedule != null) {
                                        // 시간 문자열 포맷 변환
                                        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                        val outputFormat = SimpleDateFormat("HH시 mm분", Locale.getDefault())
                                        val date = inputFormat.parse(walkingSchedule)
                                        val formattedScheduleTime = date?.let { outputFormat.format(it) } ?: "시간 없음"

                                        // 두 API 호출 결과를 콜백으로 전달
                                        callback(formattedWalkingTime, formattedScheduleTime)
                                    } else {
                                        callback(formattedWalkingTime, null)  // 산책 시각 없음
                                    }
                                } else {
                                    callback(formattedWalkingTime, null)  // 두 번째 API 실패
                                }
                            }

                            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                                callback(formattedWalkingTime, null)  // 네트워크 오류
                            }
                        })
                    } else {
                        callback(null, null)  // 첫 번째 API 성공하지만 데이터 없음
                    }
                } else {
                    callback(null, null)  // 첫 번째 API 실패
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Short>>, t: Throwable) {
                callback(null, null)  // 네트워크 오류
            }
        })
    }


    //목표 산책 시간만 조회 + null일 경우 날짜별 기록 넘어가지 않음
    private fun checkTargetWalkingTimeAndNavigate(petId: String, apiService: DailyReportApiService, selectedDate: String) {
        apiService.targetWalkingTimeCheck(petId).enqueue(object : Callback<ResponseDTO<Short>> {
            override fun onResponse(call: Call<ResponseDTO<Short>>, response: Response<ResponseDTO<Short>>) {
                if (response.isSuccessful) {
                    val targetWalkingTime = response.body()?.data
                    if (targetWalkingTime != null) {

                        dailyWalkingRecordCheck2(petId, selectedDate, apiService) {targetWalkingTimeResult ->
                            if (targetWalkingTimeResult == null) {
                                // 목표 산책 시간이 있고 새로 작성
                                val intent = Intent(requireContext(), DailyWalkingActivity::class.java)
                                intent.putExtra("SELECTED_DATE", selectedDate)
                                intent.putExtra("ACTION", "POST")
                                startActivity(intent)
                            } else {
                                val intent = Intent(requireContext(), DailyWalkingActivity::class.java)
                                intent.putExtra("SELECTED_DATE", selectedDate)
                                intent.putExtra("ACTION", "UPDATE")
                                startActivity(intent)
                            }
                        }
                    } else {
                        // 목표 산책 시간이 null이면 메시지 표시
                        Toast.makeText(requireContext(), "목표 산책 시간을 먼저 설정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "목표 산책 시간 조회 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Short>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    //날짜별 산책 기록 조회 + 해당 날짜에 파란점 찍기
    fun applyBlueDotDecorator(datesWithRecords: Set<CalendarDay>, context: Context, calendarView: MaterialCalendarView) {
        val decorator = CalendarDecorators.BlueDotDecorator(datesWithRecords, context)
        calendarView.addDecorator(decorator)  // BlueDotDecorator를 MaterialCalendarView에 추가
    }

    fun getVisibleMonthDays(calendarView: MaterialCalendarView): List<CalendarDay> {
        val currentMonth = calendarView.currentDate.month  // 현재 달의 월 가져오기
        val currentYear = calendarView.currentDate.year    // 현재 달의 년 가져오기
        val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
        val lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth()) // 해당 달의 마지막 날 계산

        val visibleDays = mutableListOf<CalendarDay>()
        var currentDate = firstDayOfMonth
        while (!currentDate.isAfter(lastDayOfMonth)) {
            visibleDays.add(CalendarDay.from(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth))
            currentDate = currentDate.plusDays(1)
        }
        return visibleDays
    }

    fun fetchWalkingRecordsAndApplyDots(petId: String, calendarView: MaterialCalendarView, apiService: DailyReportApiService, context: Context) {
        val visibleDays = getVisibleMonthDays(calendarView)
        val datesWithRecords = mutableSetOf<CalendarDay>()

        visibleDays.forEach { day ->
            val recordDate = "${day.year}-${String.format("%02d", day.month)}-${String.format("%02d", day.day)}" // recordDate 형식 변환
            dailyWalkingRecordCheck(petId, recordDate, apiService) { result ->
                if (result) {  // targetWalkingResult가 true인 경우에만 파란 점 추가
                    datesWithRecords.add(day)
                }

                // 모든 날짜에 대한 처리가 완료되면 데코레이터 적용
                if (day == visibleDays.last()) {
                    applyBlueDotDecorator(datesWithRecords, context, calendarView)
                }
            }
        }
    }


    // dailyWalkingRecordCheck 수정하여 결과를 콜백으로 받음
    fun dailyWalkingRecordCheck(petId: String, recordDate: String, apiService: DailyReportApiService, onResult: (Boolean) -> Unit) {
        apiService.dailyWalkingRecordCheck(petId, recordDate).enqueue(object : Callback<ResponseDTO<DailyWalkingRecordCheckDTO>> {
            override fun onResponse(call: Call<ResponseDTO<DailyWalkingRecordCheckDTO>>, response: Response<ResponseDTO<DailyWalkingRecordCheckDTO>>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    onResult(data?.targetWalkingResult == true)  // true일 때만 파란 점을 찍기 위한 결과 반환
                } else {
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DailyWalkingRecordCheckDTO>>, t: Throwable) {
                onResult(false)
            }
        })
    }

    // 날짜별 산책 기록 조회
    private fun dailyWalkingRecordCheck2(petId: String, recordDate: String, apiService: DailyReportApiService, callback: (Boolean?) -> Unit) {
        apiService.dailyWalkingRecordCheck(petId, recordDate).enqueue(object : Callback<ResponseDTO<DailyWalkingRecordCheckDTO>> {
            override fun onResponse(call: Call<ResponseDTO<DailyWalkingRecordCheckDTO>>, response: Response<ResponseDTO<DailyWalkingRecordCheckDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val message = responseDTO?.message
                    val data = responseDTO?.data

                    val targetWalkingResult = data?.targetWalkingResult

                    if (status == 200) {
                        callback(targetWalkingResult)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<DailyWalkingRecordCheckDTO>>, t: Throwable) {
                callback(null)
            }
        })
    }

    // petId에 따라 반려동물 요약 정보 조회
    private fun fetchPetProfileSummary(petId: String, apiService: PetProfileSummaryApiService, onFetchComplete: (PetProfileSummaryDTO?) -> Unit) {
        apiService.petProfileSummary(petId).enqueue(object : Callback<ResponseDTO<PetProfileSummaryDTO>> {
            override fun onResponse(call: Call<ResponseDTO<PetProfileSummaryDTO>>, response: Response<ResponseDTO<PetProfileSummaryDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val status = responseDTO?.status
                    val data = responseDTO?.data

                    // 데이터 처리
                    if (status == 200) {
                        onFetchComplete(data)
                    } else {
                        onFetchComplete(null)
                    }
                } else {
                    onFetchComplete(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<PetProfileSummaryDTO>>, t: Throwable) {
                onFetchComplete(null)
            }
        })
    }

    // User ID로 petId 목록을 가져오는 함수
    fun fetchPetIdsAndNames(userId: String, apiService: PetProfileSummaryApiService) {
        apiService.getPetIdByUserId(userId)
            .enqueue(object : Callback<ResponseDTO<List<String>>> {
                override fun onResponse(call: Call<ResponseDTO<List<String>>>, response: Response<ResponseDTO<List<String>>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val responseDTO = response.body()
                        val petIdList = responseDTO?.data

                        if (!petIdList.isNullOrEmpty()) {
                            val firstPetId = petIdList.firstOrNull()
                            val secondPetId = if (petIdList.size > 1) petIdList[1] else null

                            // 첫 번째 petName 가져오기
                            if (firstPetId != null) {
                                fetchPetProfileSummary(firstPetId, apiService) { firstPetProfile ->
                                    val firstPetName = firstPetProfile?.petName ?: "반려동물1"

                                    // 두 번째 petName 가져오기
                                    if (secondPetId != null) {
                                        fetchPetProfileSummary(secondPetId, apiService) { secondPetProfile ->
                                            val secondPetName = secondPetProfile?.petName ?: "반려동물2"

                                            // 두 개의 이름을 드롭다운 메뉴에 추가
                                            showPetNameDropdown(firstPetId, firstPetName, secondPetId, secondPetName)
                                        }
                                    } else {
                                        // 두 번째 반려동물이 없는 경우
                                        showPetNameDropdown(firstPetId, firstPetName, null, null)
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Pet ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "응답 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseDTO<List<String>>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // 드롭다운 메뉴 보여주기
    private fun showPetNameDropdown(firstPetId: String, firstPetName: String, secondPetId: String?, secondPetName: String?) {
        val petNameArea = view?.findViewById<LinearLayout>(R.id.petNameArea)

        petNameArea?.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), petNameArea)

            // 첫 번째 반려동물 이름 추가
            popupMenu.menu.add(0, 0, 0, firstPetName)

            // 두 번째 반려동물이 있으면 이름 추가
            if (secondPetId != null && secondPetName != null) {
                popupMenu.menu.add(0, 1, 1, secondPetName)
            }

            // 메뉴 항목 클릭 리스너
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    0 -> {
                        // 첫 번째 반려동물 선택 시
                        savePetIdToSharedPreferences(firstPetId)
                        Toast.makeText(requireContext(), "$firstPetName 선택됨", Toast.LENGTH_SHORT).show()
                        true
                    }
                    1 -> {
                        // 두 번째 반려동물 선택 시
                        savePetIdToSharedPreferences(secondPetId ?: "")
                        Toast.makeText(requireContext(), "$secondPetName 선택됨", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            // 드롭다운 메뉴 표시
            popupMenu.show()
        }
    }

    // 선택한 petId를 SharedPreferences에 저장
    private fun savePetIdToSharedPreferences(petId: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("PET_ID_KEY", petId)
        editor.apply()
    }

    //반려동물 Id 조회
    private fun fetchFirstPetId(userId: String, apiService: PetProfileSummaryApiService, callback: (String?) -> Unit) {
        // 기존의 API 호출 코드
        apiService.getPetIdByUserId(userId).enqueue(object : Callback<ResponseDTO<List<String>>> {
            override fun onResponse(call: Call<ResponseDTO<List<String>>>, response: Response<ResponseDTO<List<String>>>) {
                if (response.isSuccessful && response.body() != null) {
                    val petIdList = response.body()?.data
                    if (!petIdList.isNullOrEmpty()) {
                        val firstPetId = petIdList.first()
                        // SharedPreferences에 petId 저장
                        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("PET_ID_KEY", firstPetId).apply()
                        callback(firstPetId) // 콜백 호출
                    } else {
                        callback(null) // petId가 없을 경우
                    }
                } else {
                    callback(null) // 응답 실패
                }
            }

            override fun onFailure(call: Call<ResponseDTO<List<String>>>, t: Throwable) {
                callback(null) // 네트워크 오류
            }
        })
    }

}