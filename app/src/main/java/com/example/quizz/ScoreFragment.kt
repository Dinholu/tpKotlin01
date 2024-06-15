package com.example.quizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
            val finalScore = it.getInt("score")
            displayScores(categoryId)
            displayFinalScore(finalScore)
        }

        // Gestion du bouton de retour
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_ScoreFragment_to_CategoryFragment)
        }
    }

    private fun displayFinalScore(finalScore: Int) {
        binding.finalScoreTextView.text = getString(R.string.final_score, finalScore)
        binding.finalScoreTextView.visibility = View.VISIBLE
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
    private fun setColorByRank(textView: TextView, rank: Int) {
        val color = when (rank) {
            1 -> resources.getColor(R.color.gold, null)
            2 -> resources.getColor(R.color.silver, null)
            3 -> resources.getColor(R.color.bronze, null)
            else -> resources.getColor(R.color.white, null)
        }
        textView.setTextColor(color)
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

        val headerRankTextView = TextView(context).apply {
            text = "Rang"
            textSize = 18f
            setTextColor(resources.getColor(R.color.white, null))
            setPadding(8, 8, 18, 8)
            gravity = android.view.Gravity.CENTER
        }

        val headerScoreTextView = TextView(context).apply {
            text = "Score"
            textSize = 18f
            setTextColor(resources.getColor(R.color.white, null))
            setPadding(8, 8, 8, 8)
            gravity = android.view.Gravity.CENTER
        }

        val headerNameTextView = TextView(context).apply {
            text = "Nom"
            textSize = 18f
            setTextColor(resources.getColor(R.color.white, null))
            setPadding(18, 8, 8, 8)
            gravity = android.view.Gravity.CENTER
        }

        headerRow.addView(headerRankTextView)
        headerRow.addView(View(context).apply { layoutParams = TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT) }) // Add a space between columns
        headerRow.addView(headerScoreTextView)
        headerRow.addView(View(context).apply { layoutParams = TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT) }) // Add a space between columns
        headerRow.addView(headerNameTextView)
        tableLayout.addView(headerRow)


        var displayedRank = 1
        var previousScore = 1
        var j = 0

        for (i in scores.indices) {
            if ( j > 10) break

            val score = scores[i]
            if (score.score != previousScore && j != 0) {
                displayedRank++

            }

            val tableRow = TableRow(context).apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER
            }

            val rankTextView = TextView(context).apply {
                text = displayedRank.toString()
                textSize = 18f
                setPadding(8, 8, 18, 8)
                gravity = android.view.Gravity.CENTER
            }
            setColorByRank(rankTextView, displayedRank)

            val scoreTextView = TextView(context).apply {
                text = score.score.toString()
                textSize = 18f
                setPadding(8, 8, 18, 8)
                gravity = android.view.Gravity.CENTER
            }
            setColorByRank(scoreTextView, displayedRank)

            val nameTextView = TextView(context).apply {
                text = score.player
                textSize = 18f
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER
            }
            setColorByRank(nameTextView, displayedRank)

            tableRow.addView(rankTextView)
            tableRow.addView(View(context).apply { layoutParams = TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT) }) // Add a space between columns
            tableRow.addView(scoreTextView)
            tableRow.addView(View(context).apply { layoutParams = TableRow.LayoutParams(16, TableRow.LayoutParams.WRAP_CONTENT) }) // Add a space between columns
            tableRow.addView(nameTextView)
            tableLayout.addView(tableRow)
            previousScore = score.score
            j++
        }
    }


}
