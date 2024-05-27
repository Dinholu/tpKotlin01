package com.example.kotlintp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CategoriesFragment())
            .commit()

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val textUsername: TextView = findViewById(R.id.TextUsername)
        textUsername.text = "Bonjour, $username !"
    }
}
