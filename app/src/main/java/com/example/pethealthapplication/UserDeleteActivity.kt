package com.example.pethealthapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.example.pethealthapplication.dto.ResponseDTO
import com.example.pethealthapplication.nicknameapi.NicknameApiClient
import com.example.pethealthapplication.userdeleteapi.UserDeleteApiClient
import com.example.pethealthapplication.userdeleteapi.UserDeleteApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_delete)

        val agreeCheck = findViewById<CheckBox>(R.id.agreeCheck)
        val userDeleteBtn = findViewById<Button>(R.id.userDeleteBtn)



        //동의하면 탈퇴 버튼 활성화
        agreeCheck.setOnCheckedChangeListener { _, isChecked ->
            userDeleteBtn.isEnabled = true
        }




        //유저 탈퇴
        userDeleteBtn.setOnClickListener{
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("USER_ID_KEY", null)

            if (userId != null) {
                val userDeleteApiService = UserDeleteApiClient.getApiService(this)
                val call = userDeleteApiService.deleteUser(userId)

                call.enqueue(object : Callback<ResponseDTO<Any>> {
                    override fun onResponse(call: Call<ResponseDTO<Any>>, response: Response<ResponseDTO<Any>>) {
                        if (response.isSuccessful && response.body() != null) {
                            val responseDTO = response.body()

                            if (responseDTO?.status == 200) {
                                // 요청 성공 시 토스트 메시지 표시
                                Toast.makeText(this@UserDeleteActivity, "탈퇴 성공", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@UserDeleteActivity, StartActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            // 실패 시 토스트 메시지 표시
                            Toast.makeText(this@UserDeleteActivity, "탈퇴 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseDTO<Any>>, t: Throwable) {
                        // 네트워크 오류 또는 요청 실패 시 토스트 메시지 표시
                        Toast.makeText(this@UserDeleteActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}