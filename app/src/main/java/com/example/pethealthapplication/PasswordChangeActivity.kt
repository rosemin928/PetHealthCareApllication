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
import com.example.pethealthapplication.dto.PasswordCheckDTO
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.dto.UpdateNicknameDTO
import com.example.pethealthapplication.dto.UpdatePasswordDTO
import com.example.pethealthapplication.nicknameapi.NicknameApiClient
import com.example.pethealthapplication.nicknameapi.NicknameApiService
import com.example.pethealthapplication.passwordapi.PasswordApiClient
import com.example.pethealthapplication.passwordapi.PasswordApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordChangeActivity : AppCompatActivity() {

    private lateinit var passwordArea: EditText
    private lateinit var newPasswordArea: EditText
    private lateinit var newPasswordCheckArea: EditText
    private lateinit var passwordChangeBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)

        passwordArea = findViewById(R.id.passwordArea)
        newPasswordArea = findViewById(R.id.newPasswordArea)
        newPasswordCheckArea = findViewById(R.id.newNicknameCheckArea)
        passwordChangeBtn = findViewById(R.id.passwordChangeBtn)



        //필드에 값이 다 존재하면 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 여기는 사용하지 않음
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 여기는 사용하지 않음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = passwordArea.text.toString().trim()
                val newPassword = newPasswordArea.text.toString().trim()
                val newPasswordCheck = newPasswordCheckArea.text.toString().trim()

                //모든 필드가 비어 있지 않을 때 버튼 활성화
                passwordChangeBtn.isEnabled = password.isNotEmpty() && newPassword.isNotEmpty() && newPasswordCheck.isNotEmpty()
            }
        }
        passwordArea.addTextChangedListener(textWatcher)
        newPasswordArea.addTextChangedListener(textWatcher)
        newPasswordCheckArea.addTextChangedListener(textWatcher)




        //비밀번호 변경 & 비밀번호 변경 성공 이후 화면 전환
        passwordChangeBtn.setOnClickListener {
            val newPassword = newPasswordArea.text.toString().trim()
            val newPasswordCheck = newPasswordCheckArea.text.toString().trim()
            val password = passwordArea.text.toString().trim()

            if (newPassword.isNotEmpty() && newPassword == newPasswordCheck) {
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("USER_ID_KEY", null)

                val passwordApiService = PasswordApiClient.getApiService(this)

                if (userId != null) {

                    currentPasswordCheck(passwordApiService, userId, password) {currentPassword ->
                        if (currentPassword == "same") {
                            passwordChange(userId, passwordApiService, newPasswordCheck)
                        } else {
                            Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "유저 아이디를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "새 비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //비밀번호 변경
    private fun passwordChange(userId: String, apiService: PasswordApiService, newPassword: String) {

        val updatePasswordDTO = UpdatePasswordDTO(newPassword)

        apiService.changePassword(userId, updatePasswordDTO).enqueue(object : Callback<ResponseDTO<Any>> {
            override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    if (responseDTO?.status == 200) {
                        Toast.makeText(this@PasswordChangeActivity, "비밀번호 변경 성공", Toast.LENGTH_SHORT).show()
                        finish() //마이페이지로 돌아가기
                    } else {
                        Toast.makeText(this@PasswordChangeActivity, "비밀번호 변경 실패: ${responseDTO?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("NicknameChange", "Error Code: ${response.code()}, Message: ${response.message()}")
                    Toast.makeText(this@PasswordChangeActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                Toast.makeText(this@PasswordChangeActivity, "요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    //현재 비밀번호 조회
    private fun currentPasswordCheck(apiService: PasswordApiService, userId: String, password: String, callback: (String?) -> Unit) {

        val passwordCheckDTO = PasswordCheckDTO(userId, password)

        apiService.passwordCheck(passwordCheckDTO).enqueue(object : Callback<ResponseDTO<String>> {
            override fun onResponse(call: Call<ResponseDTO<String>>, response: Response<ResponseDTO<String>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDTO = response.body()
                    val data = responseDTO?.data

                    if (responseDTO?.status == 200) {
                        callback(data)
                    } else {
                        Toast.makeText(this@PasswordChangeActivity, "비밀번호 조회 실패: ${responseDTO?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("NicknameChange", "Error Code: ${response.code()}, Message: ${response.message()}")
                    Toast.makeText(this@PasswordChangeActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                Toast.makeText(this@PasswordChangeActivity, "요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}