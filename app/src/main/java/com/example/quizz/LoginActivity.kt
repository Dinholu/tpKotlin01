package com.example.quizz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quizz.data.Category
import com.example.quizz.data.Score
import kotlinx.coroutines.launch

private lateinit var m_buttonSubmit: Button
private lateinit var sharedPreferences: SharedPreferences

class LoginActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = (application as QuizApplication).database

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        editTextUsername.setText(username)

        m_buttonSubmit = findViewById(R.id.buttonSubmit)

        m_buttonSubmit.setOnClickListener {
            val usernameInput = editTextUsername.text.toString().trim()

            if (usernameInput.isEmpty() || usernameInput.length > 5) {
                Toast.makeText(this, "Le nom d'utilisateur doit contenir entre 1 et 5 caractères.", Toast.LENGTH_SHORT).show()
            } else {
                val editor = sharedPreferences.edit()
                editor.putString("username", usernameInput)
                editor.apply()

                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra("username", usernameInput)
                }
                startActivity(intent)

                finish()
            }
        }
        lifecycleScope.launch {
            if (database.categoryDao().getNumberCategory() == 0) {
                val categories = listOf(
                    Category(1, "Cinéma", "tv_cinema"),
                    Category(2, "Littérature", "art_litterature"),
                    Category(3, "Jeux Vidéo", "jeux_videos"),
                    Category(4, "Culture G", "culture_generale"),
                    Category(5, "Musique", "musique"),
                    Category(6, "Sport", "sport"),
                )
                categories.forEach { category ->
                    try {
                        database.categoryDao().insert(category)
                        Log.d("InsertDebug", "Inserted category: ${category.name}")
                    } catch (e: Exception) {
                        Log.e("InsertError", "Failed to insert category", e)
                    }
                }
            }
            if (database.scoreDao().getNumberScores() == 0) {
                val scores = listOf(
                    Score(1, 1, 2, "Aliz"),
                    Score(2, 2, 9, "Wrkt"),
                    Score(3, 2, 10, "Ian"),
                    Score(4, 4, 9, "Tang"),
                    Score(5, 2, 0, "Leo")
                )
                scores.forEach { score ->
                    database.scoreDao().insert(score)
                }
            }
        }
    }
}
