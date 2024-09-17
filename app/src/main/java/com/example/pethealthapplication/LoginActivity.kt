package com.example.pethealthapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.pethealthapplication.dto.LoginDTO
import com.example.pethealthapplication.joinapi.JoinApiClient
import com.example.pethealthapplication.loginapi.LoginApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var userIdArea: EditText
    private lateinit var passwordArea: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userIdArea = findViewById(R.id.userIdArea)
        passwordArea = findViewById(R.id.passwordArea)
        loginBtn = findViewById(R.id.loginBtn)



        //모든 필드에 값이 있어야 버튼 활성화
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

                //모든 필드가 비어 있지 않을 때 버튼 활성화
                loginBtn.isEnabled = userId.isNotEmpty() && password.isNotEmpty()
            }
        }
        userIdArea.addTextChangedListener(textWatcher)
        passwordArea.addTextChangedListener(textWatcher)




        //로그인 & 로그인 이후 화면 넘어가기
        loginBtn.setOnClickListener {
            val userId = userIdArea.text.toString().trim()
            val password = passwordArea.text.toString().trim()

            val loginDTO = LoginDTO(userId, password)

            val loginApiService = LoginApiClient.getApiService(this) // Activity의 Context 전달
            val call = loginApiService.login(loginDTO)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                val token = sharedPreferences.getString("TOKEN_KEY", "No Token")
                                val userId = sharedPreferences.getString("USER_ID_KEY", "No UserId")

                                editor.putString("TOKEN_KEY", token) // 실제 토큰을 여기에 저장
                                editor.putString("USER_ID_KEY", userId)
                                editor.apply()

                                // 토큰과 userId 로그로 출력
                                Log.d("LoginActivity", "Stored Token: $token")
                                Log.d("LoginActivity", "Stored UserId: $userId")

                                //다음 화면으로 넘어가기
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            401 -> {
                                Toast.makeText(this@LoginActivity, "로그인 실패: 사용자 ID 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패: 사용자 ID 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}