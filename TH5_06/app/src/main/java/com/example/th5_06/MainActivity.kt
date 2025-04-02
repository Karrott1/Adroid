package com.example.th5_06;
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.th5_06.R
import java.io.File

@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity() {

    private lateinit var btnStartRecording: Button
    private lateinit var btnStopRecording: Button
    private lateinit var btnPlayRecording: Button
    private lateinit var listViewRecordings: ListView

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var outputFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartRecording = findViewById(R.id.btnStartRecording)
        btnStopRecording = findViewById(R.id.btnStopRecording)
        btnPlayRecording = findViewById(R.id.btnPlayRecording)
        listViewRecordings = findViewById(R.id.listViewRecordings)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100)
        }


        val folder = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings")
        if (!folder.exists()) folder.mkdirs()
        outputFile = "${folder.absolutePath}/recording_${System.currentTimeMillis()}.3gp"

        // Bắt đầu ghi âm
        btnStartRecording.setOnClickListener {
            startRecording()
        }

        // Dừng ghi âm
        btnStopRecording.setOnClickListener {
            stopRecording()
        }

        // Phát lại bản ghi
        btnPlayRecording.setOnClickListener {
            playRecording()
        }

        loadRecordings()
    }

    // Hàm bắt đầu ghi âm
    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)
            prepare()
            start()
        }
        btnStartRecording.isEnabled = false
        btnStopRecording.isEnabled = true
        Toast.makeText(this, "Bắt đầu ghi âm...", Toast.LENGTH_SHORT).show()
    }

    // Hàm dừng ghi âm
    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        btnStartRecording.isEnabled = true
        btnStopRecording.isEnabled = false
        btnPlayRecording.isEnabled = true
        Toast.makeText(this, "Ghi âm hoàn tất!", Toast.LENGTH_SHORT).show()
        loadRecordings()
    }

    // Hàm phát lại bản ghi
    private fun playRecording() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(outputFile)
            prepare()

            start()
        }
        Toast.makeText(this, "Đang phát...", Toast.LENGTH_SHORT).show()
    }

    // Hàm tải danh sách file ghi âm vào ListView
    private fun loadRecordings() {
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings")
        val files = folder.listFiles()?.map { it.name } ?: emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, files)
        listViewRecordings.adapter = adapter

        // Khi chọn file từ danh sách, phát lại file đó
        listViewRecordings.setOnItemClickListener { _, _, position, _ ->
            val selectedFile = File(folder, files[position])
            playSelectedRecording(selectedFile.absolutePath)
        }
    }

    // Phát file ghi âm từ danh sách
    private fun playSelectedRecording(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
        Toast.makeText(this, "Đang phát: ${filePath.split("/").last()}", Toast.LENGTH_SHORT).show()
    }
}