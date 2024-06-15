package com.example.quizz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        leaderboardRecyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(context)
        database = (requireActivity().application as QuizApplication).database

        loadLeaderboard(view)

        return view
    }

    private fun loadLeaderboard(view: View) {
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
            createCategoryButtons(leaderboardItems, view)
        }
    }

    private fun createCategoryButtons(leaderboardItems: List<LeaderboardItem>, view: View) {
        leaderboardAdapter = LeaderboardAdapter(leaderboardItems)
        leaderboardRecyclerView.adapter = leaderboardAdapter
        Log.d("CreateCategoryButtons", "Leaderboard items: $leaderboardItems")
    }
}
