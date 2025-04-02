package com.example.th05_10

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var ipEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var receivedMessagesTextView: TextView
    private val serverPort = 12345 // Cổng của Server
    private var socket: Socket? = null
    private var isRunning = false // Biến để kiểm soát luồng nhận tin nhắn
    private val mainHandler = Handler(Looper.getMainLooper()) // Handler để cập nhật UI
    private var writer: PrintWriter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        messageEditText = findViewById(R.id.message_edittext)
        ipEditText = findViewById(R.id.ip_edittext)
        sendButton = findViewById(R.id.send_button)
        receivedMessagesTextView = findViewById(R.id.received_messages_textview)

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            val ipAddress = ipEditText.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tin nhắn!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ipAddress.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ IP!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            thread {
                connectToServer(ipAddress)
                sendMessage(message)
            }


            messageEditText.setText("")
        }
    }

    private fun connectToServer(ipAddress: String) {
        try {

            if (socket == null || socket?.isClosed == true) {
                socket = Socket(ipAddress, serverPort)
                isRunning = true
                writer = PrintWriter(socket!!.getOutputStream(), true)


                startReceivingMessages()
                mainHandler.post {
                    Toast.makeText(this, "Đã kết nối đến Server!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mainHandler.post {
                Toast.makeText(this, "Lỗi khi kết nối đến Server: ${e.message}", Toast.LENGTH_LONG).show()
            }
            isRunning = false
        }
    }

    private fun sendMessage(message: String) {
        try {

            writer?.println(message)
            writer?.flush()

            mainHandler.post {
                Toast.makeText(this, "Đã gửi tin nhắn!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mainHandler.post {
                Toast.makeText(this, "Lỗi khi gửi tin nhắn: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startReceivingMessages() {
        thread {
            try {
                val reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                while (isRunning) {
                    val message = reader.readLine()
                    if (message == null) break
                    mainHandler.post {
                        val currentText = receivedMessagesTextView.text.toString()
                        receivedMessagesTextView.text = "$currentText$message\n"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (isRunning) {
                    mainHandler.post {
                        Toast.makeText(this, "Lỗi khi nhận tin nhắn: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } finally {
                closeConnection()
            }
        }
    }

    private fun closeConnection() {
        try {
            isRunning = false
            writer?.close()
            socket?.close()
            socket = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        closeConnection()
    }
}