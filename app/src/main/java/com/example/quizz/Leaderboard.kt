package com.example.quizz

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Leaderboard : AppCompatActivity() {

  private lateinit var leaderboardRecyclerView: RecyclerView
  private lateinit var leaderboardAdapter: LeaderboardAdapter
  private lateinit var database: AppDatabase

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_leaderboard)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.leaderboardLayout)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
    leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
    database = AppDatabase.getDatabase(applicationContext)

    loadLeaderboard()
  }

  private fun loadLeaderboard() {
    lifecycleScope.launch {
      val categories = withContext(Dispatchers.IO) {
        database.categoryDao().getAllCategories().also {
          if (it.isEmpty()) {
            Log.d("LoadLeaderboard", "No categories found")
          } else {
            Log.d("LoadLeaderboard", "Categories: $it")
          }
        }
      }
      val leaderboardItems = categories.map { category ->
        val scores = withContext(Dispatchers.IO) {
          database.scoreDao().getTopScoresByCategory(category.id).also {
            if (it.isEmpty()) {
              Log.d("LoadLeaderboard", "No scores for category ${category.name}")
            } else {
              Log.d("LoadLeaderboard", "Scores for category ${category.name}: $it")
            }
          }
        }
        LeaderboardItem(category.name, scores)
      }
      createCategoryButtons(leaderboardItems)
    }
  }

  private fun createCategoryButtons(leaderboardItems: List<LeaderboardItem>) {
    leaderboardAdapter = LeaderboardAdapter(leaderboardItems)
    leaderboardRecyclerView.adapter = leaderboardAdapter
    Log.d("CreateCategoryButtons", "Leaderboard items: $leaderboardItems")
  }
}
