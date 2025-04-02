package com.example.th05_11
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var ipEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var receivedMessagesTextView: TextView
    private val serverPort = 12345 // Cổng để nhận tin nhắn
    private val clientPort = 12346 // Cổng để gửi tin nhắn
    private var isRunning = false // Biến để kiểm soát luồng nhận tin nhắn
    private val mainHandler = Handler(Looper.getMainLooper()) // Handler để cập nhật UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các thành phần giao diện
        messageEditText = findViewById(R.id.message_edittext)
        ipEditText = findViewById(R.id.ip_edittext)
        sendButton = findViewById(R.id.send_button)
        receivedMessagesTextView = findViewById(R.id.received_messages_textview)

        // Bắt đầu luồng nhận tin nhắn
        startReceivingMessages()

        // Xử lý sự kiện khi nhấn nút Gửi
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

            // Gửi tin nhắn trong một luồng riêng
            thread {
                sendMessage(message, ipAddress)
            }

            // Xóa EditText sau khi gửi
            messageEditText.setText("")
        }
    }

    private fun sendMessage(message: String, ipAddress: String) {
        try {
            // Tạo DatagramSocket để gửi tin nhắn
            val socket = DatagramSocket(clientPort)
            val address = InetAddress.getByName(ipAddress)

            // Chuyển tin nhắn thành mảng byte
            val buffer = message.toByteArray()

            // Tạo DatagramPacket
            val packet = DatagramPacket(buffer, buffer.size, address, serverPort)

            // Gửi gói tin
            socket.send(packet)

            // Đóng socket
            socket.close()

            // Hiển thị thông báo gửi thành công trên UI thread
            mainHandler.post {
                Toast.makeText(this, "Đã gửi tin nhắn!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Hiển thị thông báo lỗi trên UI thread
            mainHandler.post {
                Toast.makeText(this, "Lỗi khi gửi tin nhắn: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startReceivingMessages() {
        isRunning = true
        thread {
            try {
                // Tạo DatagramSocket để nhận tin nhắn
                val socket = DatagramSocket(serverPort)

                while (isRunning) {
                    // Tạo buffer để nhận dữ liệu
                    val buffer = ByteArray(1024)
                    val packet = DatagramPacket(buffer, buffer.size)

                    // Nhận gói tin
                    socket.receive(packet)

                    // Trích xuất dữ liệu từ gói tin
                    val receivedMessage = String(packet.data, 0, packet.length)
                    val senderIp = packet.address.hostAddress

                    // Cập nhật TextView trên UI thread
                    mainHandler.post {
                        val currentText = receivedMessagesTextView.text.toString()
                        receivedMessagesTextView.text = "$currentText$senderIp: $receivedMessage\n"
                    }
                }

                // Đóng socket khi dừng nhận
                socket.close()

            } catch (e: Exception) {
                e.printStackTrace()
                if (isRunning) {
                    // Hiển thị thông báo lỗi trên UI thread
                    mainHandler.post {
                        Toast.makeText(this, "Lỗi khi nhận tin nhắn: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Dừng luồng nhận tin nhắn khi Activity bị hủy
        isRunning = false
    }
}