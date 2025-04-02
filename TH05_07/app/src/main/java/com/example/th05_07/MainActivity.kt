package com.example.th05_07

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.MediaController
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ComponentActivity

@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnPickVideo: Button
    private lateinit var edtVideoUrl: EditText
    private lateinit var btnPlayUrl: Button
    private lateinit var mediaController: MediaController

    private val PICK_VIDEO_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        btnPickVideo = findViewById(R.id.btnPickVideo)
        edtVideoUrl = findViewById(R.id.edtVideoUrl)
        btnPlayUrl = findViewById(R.id.btnPlayUrl)

        mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Xử lý chọn video từ bộ nhớ
        btnPickVideo.setOnClickListener {
            pickVideoFromGallery()
        }

        // Xử lý phát video từ URL
        btnPlayUrl.setOnClickListener {
            playVideoFromUrl()
        }
    }

    // Hàm chọn video từ bộ nhớ
    private fun pickVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    // Xử lý kết quả chọn video
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedVideoUri: Uri? = data?.data
            if (selectedVideoUri != null) {
                videoView.setVideoURI(selectedVideoUri)
                videoView.start()
            } else {
                Toast.makeText(this, "Không thể chọn video!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm phát video từ URL
    private fun playVideoFromUrl() {
        val videoUrl = edtVideoUrl.text.toString().trim()
        if (videoUrl.isNotEmpty()) {
            try {
                val uri = Uri.parse(videoUrl)
                videoView.setVideoURI(uri)
                videoView.start()
            } catch (e: Exception) {
                Toast.makeText(this, "Lỗi phát video!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Vui lòng nhập URL video!", Toast.LENGTH_SHORT).show()
        }
    }
}
