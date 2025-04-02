package com.example.callreceive

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtPhoneNumber = findViewById<EditText>(R.id.edtPhoneNumber)
        val btnBlock = findViewById<Button>(R.id.btnBlock)

        btnBlock.setOnClickListener {
            val phoneNumber = edtPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty()) {
                BlockedNumbersList.addNumber(phoneNumber)
                Toast.makeText(this, "Đã chặn số: $phoneNumber", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
