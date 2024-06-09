package com.example.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quizz.DAO.ScoreDAO
import com.example.quizz.data.Score
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
            val categoryId = it.getInt("categoryId")
            displayScores(categoryId)
        }
    }

    private fun displayScores(categoryId: Int) {
        lifecycleScope.launch {
            val scores = scoreDao.getScoresByCategory(categoryId)
            if (scores.isNotEmpty()) {
                binding.noScoresTextView.visibility = View.GONE
                val sortedScores = scores.sortedByDescending { it.score } // Trier les scores du plus élevé au plus bas
                populateScoreTable(sortedScores)
            } else {
                binding.noScoresTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun populateScoreTable(scores: List<Score>) {
        val tableLayout = binding.scoreTable
        tableLayout.removeAllViews() // Clear existing rows

        // Ajouter l'en-tête du tableau
        val headerRow = TableRow(context).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            gravity = android.view.Gravity.CENTER
        }

        val headerScoreTextView = TextView(context).apply {
            text = "Score"
            textSize = 18f
            setTextColor(resources.getColor(R.color.white, null))
            setPadding(8, 8, 8, 8)
            gravity = android.view.Gravity.CENTER
        }

        val spacer = View(context).apply {
            layoutParams = TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT) // Spacer with fixed width
        }

        val headerNameTextView = TextView(context).apply {
            text = "Nom"
            textSize = 18f
            setTextColor(resources.getColor(R.color.white, null))
            setPadding(8, 8, 8, 8)
            gravity = android.view.Gravity.CENTER
        }

        headerRow.addView(headerScoreTextView)
        headerRow.addView(spacer)
        headerRow.addView(headerNameTextView)
        tableLayout.addView(headerRow)

        // Ajouter les lignes de score
        for (score in scores) {
            val tableRow = TableRow(context).apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER
            }

            val scoreTextView = TextView(context).apply {
                text = score.score.toString()
                textSize = 18f
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER
            }

            val spacer = View(context).apply {
                layoutParams = TableRow.LayoutParams(70, TableRow.LayoutParams.WRAP_CONTENT) // Spacer with fixed width
            }

            val nameTextView = TextView(context).apply {
                text = score.player
                textSize = 18f
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER
            }

            tableRow.addView(scoreTextView)
            tableRow.addView(spacer)
            tableRow.addView(nameTextView)
            tableLayout.addView(tableRow)
        }
    }
}
