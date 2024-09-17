package com.example.pethealthapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.UpdateNicknameDTO
import com.example.pethealthapplication.nicknameapi.NicknameApiClient
import com.example.pethealthapplication.register.Register0Activity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicknameChangeActivity : AppCompatActivity() {

    private lateinit var nicknameArea: EditText
    private lateinit var newNicknameArea: EditText
    private lateinit var newNicknameCheckArea: EditText
    private lateinit var nicknameChangeBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname_change)

        nicknameArea = findViewById(R.id.nicknameArea)
        newNicknameArea = findViewById(R.id.newNicknameArea)
        newNicknameCheckArea = findViewById(R.id.newNicknameCheckArea)
        nicknameChangeBtn = findViewById(R.id.nicknameChangeBtn)



        //필드에 값이 다 존재하면 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 여기는 사용하지 않음
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 여기는 사용하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = nicknameArea.text.toString()
                val newNickname = newNicknameArea.text.toString()
                val newNicknameCheck = newNicknameCheckArea.text.toString()

                //모든 필드가 비어 있지 않을 때 버튼 활성화
                nicknameChangeBtn.isEnabled = nickname.isNotEmpty() && newNickname.isNotEmpty() && newNicknameCheck.isNotEmpty()
            }
        }
        nicknameArea.addTextChangedListener(textWatcher)
        newNicknameArea.addTextChangedListener(textWatcher)
        newNicknameCheckArea.addTextChangedListener(textWatcher)




        //닉네임 변경 & 닉네임 변경 성공 이후 화면 전환
        nicknameChangeBtn.setOnClickListener {
            val newNickname = newNicknameArea.text.toString().trim()
            val newNicknameCheck = newNicknameCheckArea.text.toString().trim()

            if (newNickname.isNotEmpty() && newNickname == newNicknameCheck) {
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("USER_ID_KEY", null)

                if (userId != null) {
                    val updateNicknameDTO = UpdateNicknameDTO(newNickname)
                    val nicknameApiService = NicknameApiClient.getApiService(this)
                    val call = nicknameApiService.changeNickname(userId, updateNicknameDTO)

                    Log.d("NicknameChangeActivity", "Stored UserId: $userId")

                    call.enqueue(object : Callback<ResponseDTO<Any>> {
                        override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                            if (response.isSuccessful && response.body() != null) {
                                val responseDTO = response.body()
                                if (responseDTO?.status == 200) {
                                    Toast.makeText(this@NicknameChangeActivity, "닉네임 변경 성공", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@NicknameChangeActivity, MyPageFragment::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@NicknameChangeActivity, "닉네임 변경 실패: ${responseDTO?.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.e("NicknameChange", "Error Code: ${response.code()}, Message: ${response.message()}")
                                Toast.makeText(this@NicknameChangeActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                            Toast.makeText(this@NicknameChangeActivity, "요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "유저 아이디를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "새 닉네임을 확인해 주세요", Toast.LENGTH_SHORT).show()
            }
        }


    }
}




