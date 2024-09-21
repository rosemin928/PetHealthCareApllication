package com.example.pethealthapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pethealthapplication.dto.JoinDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.joinapi.JoinApiClient
import com.example.pethealthapplication.register.Register0Activity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinIdActivity : AppCompatActivity() {

    private lateinit var userIdArea: EditText
    private lateinit var passwordArea: EditText
    private lateinit var passwordCheckArea: EditText
    private lateinit var nicknameArea: EditText
    private lateinit var joinBtn: Button
    private lateinit var passwordCheckMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_id)

        userIdArea = findViewById(R.id.userIdArea)
        passwordArea = findViewById(R.id.passwordArea)
        passwordCheckArea = findViewById(R.id.passwordCheckArea)
        nicknameArea = findViewById(R.id.nicknameArea)
        joinBtn = findViewById(R.id.joinBtn)
        passwordCheckMessage = findViewById(R.id.passwordCheckMessage)


        // 입력창에 모든 값이 들어가야 버튼 활성화 & 비밀번호 일치 확인
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 여기는 사용하지 않음
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 여기는 사용하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userId = userIdArea.text.toString().trim()
                val password = passwordArea.text.toString().trim()
                val passwordCheck = passwordCheckArea.text.toString().trim()
                val nickname = nicknameArea.text.toString().trim()

                //모든 필드가 비어 있지 않을 때 버튼 활성화
                joinBtn.isEnabled =
                    userId.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty() && nickname.isNotEmpty()

                //비밀번호 일치 확인
                if (password.isNotEmpty() && passwordCheck.isNotEmpty()) {
                    if (password == passwordCheck) {
                        passwordCheckMessage.setText("비밀번호가 일치합니다")
                        passwordCheckMessage.setTextColor(Color.BLUE)
                    } else {
                        passwordCheckMessage.setText("비밀번호가 일치하지 않습니다")
                        passwordCheckMessage.setTextColor(Color.RED)
                    }
                }
            }
        }
        userIdArea.addTextChangedListener(textWatcher)
        passwordArea.addTextChangedListener(textWatcher)
        passwordCheckArea.addTextChangedListener(textWatcher)
        nicknameArea.addTextChangedListener(textWatcher)


        // 회원가입 & 가입 성공 이후 화면 넘어가기
        joinBtn.setOnClickListener {
            val userId = userIdArea.text.toString().trim()
            val password = passwordArea.text.toString().trim()
            val passwordCheck = passwordCheckArea.text.toString().trim()
            val nickname = nicknameArea.text.toString().trim()

            if (password == passwordCheck) {
                val joinDTO = JoinDTO(userId, password, nickname)

                val joinApiService = JoinApiClient.joinApiService
                val call = joinApiService.join(joinDTO)
                call.enqueue(object : Callback<ResponseDTO<Any>> {
                    override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                        if (response.isSuccessful && response.body() != null) {

                            val responseDTO = response.body()

                            if (responseDTO?.status == 200) {
                                Toast.makeText(this@JoinIdActivity, "가입 성공", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@JoinIdActivity, LoginAfterJoinActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@JoinIdActivity, "가입 실패: ${responseDTO?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (response.code() == 409) {
                                Toast.makeText(this@JoinIdActivity, "가입 실패: 이미 존재하는 사용자 ID입니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@JoinIdActivity, "서버 에러: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                        Toast.makeText(this@JoinIdActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("JOIN", "${t.message}")
                    }
                })
            } else {
                Toast.makeText(this@JoinIdActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}