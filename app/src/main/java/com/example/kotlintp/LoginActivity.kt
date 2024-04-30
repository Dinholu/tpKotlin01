package com.example.kotlintp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameInput : EditText
    private lateinit var connectionButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameInput = findViewById(R.id.username)
        connectionButton = findViewById(R.id.login)
        connectionButton.isEnabled = true

        connectionButton.setOnClickListener {
            saveUsernameToSharedPreferences()
            openGameActivity()

            // For testing purpose, send username from sharedpreferences in logcat
            val sharedPreferences: SharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE)
            val retrievedUsername = sharedPreferences.getString("username", "")
            Log.d("LoginActivity", "Retrieved Username: $retrievedUsername")
        }
    }

    private fun saveUsernameToSharedPreferences() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val username = usernameInput.text.toString()
        editor.putString("username", username)
        editor.apply()
    }

    private fun openGameActivity() {
        val gameActivityIntent = Intent(this, GameActivity::class.java)
        startActivity(gameActivityIntent)
    }
}