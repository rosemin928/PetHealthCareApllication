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
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class SnsWriteActivity : AppCompatActivity() {
    private lateinit var imageBox: RelativeLayout
    private lateinit var addImage: ImageView
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_write)

        imageBox = findViewById(R.id.imageBox)
        addImage = findViewById(R.id.addImage)

        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    // 선택된 이미지를 imageBox에 설정
                    val imageView = ImageView(this).apply {
                        layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        setImageURI(imageUri)  // URI로 이미지를 설정
                        scaleType = ImageView.ScaleType.CENTER_CROP  // 적절한 스케일 타입
                    }

                    // 기존 뷰를 제거하고 새 이미지 뷰를 추가
                    imageBox.removeAllViews()
                    imageBox.addView(imageView)
                }
            }
        }

        // addImage 클릭 시 앨범 열기
        addImage.setOnClickListener {
            checkStoragePermission()
        }
    }

    //안드로이드 버전에 따라 필요한 권한을 확인하고 요청
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

    //권한 요청에 응답했을 때 호출
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()  // 권한이 허용되면 갤러리를 엽니다.
            } else {
                // 권한이 거부된 경우 처리
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //갤러리 앱 열기
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)  // ActivityResultLauncher를 사용하여 갤러리 열기
    }

    companion object {
        const val STORAGE_PERMISSION_REQUEST_CODE = 1001
    }
}