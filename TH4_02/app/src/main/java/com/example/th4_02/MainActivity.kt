package com.example.th4_02

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper : DatabaseHelper
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
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

        // khoi tao database
        databaseHelper = DatabaseHelper(this)

        // anh xa
        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        resultTextView = findViewById(R.id.resultTextView)

        val addButton = findViewById<Button>(R.id.addButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val showButton = findViewById<Button>(R.id.showButton)

        // skien add
        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                val result = databaseHelper.addContact(name, phone)
                if (result != -1L) {
                    Toast.makeText(this, "Them thanh cong", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Toast.makeText(this, "Them that bai", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Hay dien day du thong tin cac truong", Toast.LENGTH_SHORT).show()
            }
        }
        // skien cap nhat
        updateButton.setOnClickListener {
            val oldName = nameEditText.text.toString()
            val newName = nameEditText.text.toString()
            val newPhone = phoneEditText.text.toString()

            if (oldName.isNotEmpty() && newPhone.isNotEmpty()) {
                val result = databaseHelper.updateContact(oldName, newName, newPhone)
                if (result > 0) {
                    Toast.makeText(this, "Cap nhat thanh cong", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Toast.makeText(this, "Cap nhat that bai", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Hay dien day du thong tin cac truong, hoac kiem tra lai ten", Toast.LENGTH_SHORT).show()
            }
        }

        // skien xoa
        deleteButton.setOnClickListener {
            val name = nameEditText.text.toString()

            if (name.isNotEmpty()) {
                val result = databaseHelper.deleteContact(name)
                if (result > 0) {
                    Toast.makeText(this, "Xoa thanh cong", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Toast.makeText(this, "Xoa that bai, xem lai ten trong danh sach", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui long nhap ten can xoa,", Toast.LENGTH_SHORT).show()
            }
        }

        // xu ly nut show
        showButton.setOnClickListener {
            val contacts = databaseHelper.getAllContacts()
            if (contacts.isNotEmpty()) {
                resultTextView.text = contacts.joinToString("\n")
            } else {
                resultTextView.text = "Khong co du lieu"
            }
        }
    }
    private fun clearFields() {
        nameEditText.text.clear()
        phoneEditText.text.clear()
    }
}