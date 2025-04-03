package com.example.myapplication2


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var dataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        dataTextView = findViewById(R.id.dataTextView)

        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val showDataButton = findViewById<Button>(R.id.showDataButton)


        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            clearFields()
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            // Lưu thông tin người dùng vào Realtime Database
                            val user = auth.currentUser
                            if (user != null) {
                                val userId = user.uid
                                val userData = mapOf(
                                    "email" to email,
                                    "lastLogin" to System.currentTimeMillis().toString()
                                )
                                database.getReference("users").child(userId).setValue(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Lưu dữ liệu thất bại", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            clearFields()
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }


        showDataButton.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                database.getReference("users").child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val email = snapshot.child("email").getValue(String::class.java)
                                val lastLogin = snapshot.child("lastLogin").getValue(String::class.java)
                                dataTextView.text = "Email: $email\nLast Login: $lastLogin"
                            } else {
                                dataTextView.text = "Không tìm thấy dữ liệu"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@MainActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show()
                dataTextView.text = ""
            }
        }
    }


    private fun clearFields() {
        emailEditText.text.clear()
        passwordEditText.text.clear()
    }
}