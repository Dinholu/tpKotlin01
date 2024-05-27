package com.example.kotlintp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kotlintp.data.Questions

class GameFragment : Fragment() {

    private lateinit var textViewQuestion: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var textViewScore: TextView

    private var score = 0
    private var questionIndex = 0
    private lateinit var questions: List<Questions>

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewQuestion = view.findViewById(R.id.textViewQuestion)
        textViewTimer = view.findViewById(R.id.textViewTimer)
        textViewScore = view.findViewById(R.id.textViewScore)

        sharedViewModel.categoryName.observe(viewLifecycleOwner) { categoryName ->
            val allQuestions = DataProvider.getQuestions()
            questions = allQuestions.filter { it.Categorie.name == categoryName }
            loadNextQuestion()
        }
    }

    private fun loadNextQuestion() {
        if (questionIndex < questions.size) {
            val question = questions[questionIndex]
            textViewQuestion.text = question.Nom
            startTimer()
        } else {
            showScore()
        }
    }

    private fun startTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textViewTimer.text = "Time left: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                questionIndex++
                loadNextQuestion()
            }
        }.start()
    }

    private fun showScore() {
        // Show score logic
    }
}
