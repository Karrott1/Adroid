package com.example.th4_01

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.print.PrintHelper
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {

    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceHelper = PreferenceHelper(this)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        resultTextView = findViewById(R.id.resultTextView)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val showButton = findViewById<Button>(R.id.showButton)

        saveButton.setOnClickListener{
            val usr = usernameEditText.text.toString()
            val pass = passwordEditText.text.toString()

            if (usr.isNotEmpty() && pass.isNotEmpty()) {
                preferenceHelper.saveCredentials(usr, pass)
                Toast.makeText(this, "Luu thanh cong", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Hay nhap ca username va password",
                    Toast.LENGTH_SHORT).show()
            }
        }

        clearButton.setOnClickListener {
            preferenceHelper.clearCredentials()
            usernameEditText.text.clear()
            passwordEditText.text.clear()
            resultTextView.text = ""
            Toast.makeText(this, "Xoa thanh cong", Toast.LENGTH_SHORT).show()
        }

        showButton.setOnClickListener {
            val usr = preferenceHelper.getUsername()
            val pass = preferenceHelper.getPassword()

            if (usr != null && pass != null) {
                resultTextView.text = "Username: $usr \nPassword: $pass"
            } else {
                resultTextView.text = "Khong co data"
            }
        }
    }
}