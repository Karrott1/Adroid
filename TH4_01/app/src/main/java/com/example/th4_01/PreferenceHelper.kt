package com.example.th4_01

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper (context : Context){
    private val  PRE_NAME = "PRE_TEST"
    private val USR_KEY = "username"
    private val PASS_KEY = "password"

    private val preferences: SharedPreferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE)

    fun saveCredentials (username: String , password: String) {
        val editor = preferences.edit()
        editor.putString(USR_KEY, username)
        editor.putString(PASS_KEY,password)
        editor.apply()
    }

    fun getUsername(): String? {
        return preferences.getString(USR_KEY, null)
    }

    fun getPassword(): String? {
        return preferences.getString(PASS_KEY, null)
    }

    fun clearCredentials() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}