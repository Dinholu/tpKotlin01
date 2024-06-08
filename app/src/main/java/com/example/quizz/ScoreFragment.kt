package com.example.quizz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.SharedPreferences
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quizz.DAO.ScoreDAO
import com.example.quizz.databinding.FragmentScoreBinding
import kotlinx.coroutines.launch


class ScoreFragment : Fragment() {

        private lateinit var binding: FragmentScoreBinding
        private lateinit var scoreDao: ScoreDAO
        private lateinit var categoryDao: CategoryDao

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentScoreBinding.inflate(inflater, container, false)
            scoreDao = (requireActivity().application as QuizApplication).database.scoreDao()
            categoryDao = (requireActivity().application as QuizApplication).database.categoryDao()
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            arguments?.let {
                val score = it.getInt("score")
                val categoryId = it.getInt("categoryId")
                displayScores(categoryId)
            }
        }

        private fun displayScores(categoryId: Int) {
            lifecycleScope.launch {
                val scores = scoreDao.getScoresByCategory(categoryId)
                if (scores.isNotEmpty()) {
                    val scoresText =
                        scores.joinToString(separator = "\n") { "Player: ${it.player}, Score: ${it.score}" }
                    binding.bestScoresTextView.text = scoresText
                } else {
                    binding.bestScoresTextView.text = "No scores available for this category."
                }
            }
        }
    }


