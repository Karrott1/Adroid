package com.example.th5_05


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        timeTextView = findViewById(R.id.timeTextView)

        // Bắt đầu thread đếm thời gian
        startTimerThread()
    }

    private fun startTimerThread() {
        thread {
            while (isRunning) {
                try {
                    Thread.sleep(1000) // Đợi 1 giây
                    seconds++ // Tăng giá trị thời gian


                    handler.post {
                        
                        timeTextView.text = "$seconds seconds"
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false // Dừng thread khi Activity bị hủy
    }
}