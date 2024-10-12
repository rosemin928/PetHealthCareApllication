package com.example.pethealthapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pethealthapplication.dto.ImagePostDTO
import com.example.pethealthapplication.snsapi.SnsApiClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SnsWriteActivity : AppCompatActivity() {
    private lateinit var imageBox: RelativeLayout
    private lateinit var addImage: ImageView
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var postButton: Button
    private lateinit var writeText: EditText
    private var selectedImageUri: Uri? = null  // 선택된 이미지 URI 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_write)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID_KEY", null)

        imageBox = findViewById(R.id.imageBox)
        addImage = findViewById(R.id.addImage)
        writeText = findViewById(R.id.writeText)
        postButton = findViewById(R.id.postBtn)

        // 값이 모두 들어있어야 게시하기 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateButton()
            }
        }
        writeText.addTextChangedListener(textWatcher)

        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    selectedImageUri = imageUri  // 선택된 이미지 URI 저장

                    // 선택된 이미지를 imageBox에 설정
                    val imageView = ImageView(this).apply {
                        layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        setImageURI(imageUri)  // URI로 이미지를 설정
                        scaleType = ImageView.ScaleType.CENTER_CROP  // 적절한 스케일 타입
                    }

                    imageBox.removeAllViews()
                    imageBox.addView(imageView)
                    validateButton() // 이미지가 추가되면 버튼 상태 업데이트
                }
            }
        }

        // addImage 클릭 시 앨범 열기
        addImage.setOnClickListener {
            checkStoragePermission()
        }

        // saveButton 클릭 시 게시물 업로드
        postButton.setOnClickListener {

            val content = writeText.text.toString()

            if (content.isNotEmpty()) {
                uploadPost(userId ?: "", content, selectedImageUri)
            } else {
                Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 텍스트가 있을 때 버튼 활성화
    private fun validateButton() {
        val writeTextString = writeText.text.toString()

        postButton.isEnabled = writeTextString.isNotEmpty()
    }

    // 업로드 메소드
    private fun uploadPost(userId: String, content: String, imageUri: Uri?) {
        val postRequestDTO = ImagePostDTO(
            isNicknamePublic = false, // 필요에 따라 값 설정
            content = content
        )
        val json = Gson().toJson(postRequestDTO)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        // 이미지 파일을 MultipartBody로 변환합니다.
        val imagePart: MultipartBody.Part? = imageUri?.let {
            val file = createFileFromUri(it)
            if (file != null) {
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            } else {
                null
            }
        }

        val apiService = SnsApiClient.getApiService(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.postWalking(userId, requestBody, imagePart)
                withContext(Dispatchers.Main) {
                    if (response.status == 200) {
                        Toast.makeText(this@SnsWriteActivity, "게시물 업로드 성공", Toast.LENGTH_SHORT).show()
                        finish() // 업로드 성공 후 액티비티 종료 또는 초기화
                    } else {
                        Toast.makeText(this@SnsWriteActivity, "게시물 업로드 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SnsWriteActivity, "업로드 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Uri를 파일로 변환하는 메소드
    private fun createFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "selectedImage_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Uri에서 실제 파일 경로를 가져오는 메소드 (Deprecated 방식 대신 대체)
    private fun getRealPathFromURI(contentUri: Uri): String? {
        // 최신 Android 버전에 맞게 Uri를 파일 경로로 변환
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createFileFromUri(contentUri)?.absolutePath
        } else {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            contentResolver.query(contentUri, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    cursor.getString(columnIndex)
                } else {
                    null
                }
            }
        }
    }

    // 안드로이드 버전에 따라 필요한 권한을 확인하고 요청
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery()
            }
        }
    }

    // 권한 요청에 응답했을 때 호출
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 갤러리 앱 열기
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)  // ActivityResultLauncher를 사용하여 갤러리 열기
    }

    companion object {
        const val STORAGE_PERMISSION_REQUEST_CODE = 1001
    }
}