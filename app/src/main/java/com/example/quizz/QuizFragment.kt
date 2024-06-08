package com.example.quizz

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.quizz.DAO.ScoreDAO
import com.example.quizz.data.Score
import com.example.quizz.databinding.FragmentQuizBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var currentPageIndex = 0
    private var score = 0
    private var quizzes: List<Quiz> = listOf()
    private var timer: CountDownTimer? = null
    private var categoryName: String? = null
    private lateinit var database: AppDatabase
    private lateinit var scoreDao: ScoreDAO
    private lateinit var categoryDao: CategoryDao

    companion object {
        private const val TIMER_DELAY = 10000 // 10 seconds in milliseconds
        private const val TAG = "QuizFragment"
        private const val STATE_CURRENT_PAGE_INDEX = "currentPageIndex"
        private const val STATE_SCORE = "score"
        private const val STATE_QUIZZES = "quizzes"
        private const val STATE_CATEGORY_NAME = "categoryName"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        database = (requireActivity().application as QuizApplication).database
        scoreDao = database.scoreDao()
        categoryDao = database.categoryDao()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            currentPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX)
            score = savedInstanceState.getInt(STATE_SCORE)
            categoryName = savedInstanceState.getString(STATE_CATEGORY_NAME)
            val jsonQuizzes = savedInstanceState.getString(STATE_QUIZZES)
            if (jsonQuizzes != null) {
                quizzes = Gson().fromJson(jsonQuizzes, object : TypeToken<List<Quiz>>() {}.type)
            }
        } else {
            val jsonQuizzes = arguments?.getString("quizzes")
            categoryName = arguments?.getString("categoryName")
            if (jsonQuizzes == null) {
                Log.e(TAG, "No quizzes found in arguments")
                return
            }
            quizzes = Gson().fromJson(jsonQuizzes, object : TypeToken<List<Quiz>>() {}.type)
        }

        Log.d(TAG, "Quizzes loaded: ${quizzes.size}")

        (activity as? AppCompatActivity)?.supportActionBar?.title = categoryName  // Met à jour le titre de la Toolbar

        if (quizzes.isNotEmpty()) {
            binding.questionProgressBar.max = quizzes.size
            updateScoreTextView()
            displayCurrentQuestion()
            startTimer()
        } else {
            Log.e(TAG, "No quizzes available to display")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_PAGE_INDEX, currentPageIndex)
        outState.putInt(STATE_SCORE, score)
        outState.putString(STATE_CATEGORY_NAME, categoryName)
        outState.putString(STATE_QUIZZES, Gson().toJson(quizzes))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(TIMER_DELAY.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = "Temps restant : ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                currentPageIndex++
                navigateToNextQuestion()
            }
        }.start()
    }

    private fun navigateToNextQuestion() {
        if (currentPageIndex < quizzes.size) {
            displayCurrentQuestion()
            startTimer()
        } else {
            timer?.cancel()
            // Afficher le score final
            saveScoreToDatabase()  // Enregistrer le score en base de données
        }
    }

    private fun displayCurrentQuestion() {
        if (currentPageIndex >= quizzes.size) return

        binding.questionContainer.removeAllViews()
        binding.questionProgressBar.progress = currentPageIndex + 1

        val quiz = quizzes[currentPageIndex]
        Log.d(TAG, "Displaying question: ${quiz.question}")

        val questionTextView = TextView(requireContext()).apply {
            text = quiz.question
            typeface = resources.getFont(R.font.robotomedium)
            textSize = 22f
        }
        binding.questionContainer.addView(questionTextView)

        val answers = mutableListOf<String>()
        answers.add(quiz.answer)
        answers.addAll(quiz.badAnswers)
        answers.shuffle() // Mélanger les réponses

        val marginTopInPixels = resources.getDimensionPixelSize(R.dimen.fab_margin)

        for (answer in answers) {
            val answerButton = Button(requireContext()).apply {
                text = answer
                typeface = resources.getFont(R.font.robotomedium)
                setTextColor(resources.getColor(R.color.white))
                setBackgroundResource(R.drawable.rounded_button)
                textSize = 15f

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, marginTopInPixels, 0, 0)
                }

                setOnClickListener {
                    timer?.cancel()
                    disableButtons()
                    checkAnswer(this, answer == quiz.answer, quiz.answer)
                }
            }
            binding.questionContainer.addView(answerButton)
        }
    }

    private fun disableButtons() {
        for (i in 0 until binding.questionContainer.childCount) {
            val view = binding.questionContainer.getChildAt(i)
            if (view is Button) {
                view.isEnabled = false
            }
        }
    }

    private fun checkAnswer(selectedButton: Button, isCorrect: Boolean, correctAnswer: String) {
        if (isCorrect) {
            score++
            selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else {
            selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            showCorrectAnswer(correctAnswer)
        }
        updateScoreTextView()
        binding.questionContainer.postDelayed({
            currentPageIndex++
            navigateToNextQuestion()
        }, 2000) // Delay to show the result before moving to the next question
    }

    private fun showCorrectAnswer(correctAnswer: String) {
        for (i in 0 until binding.questionContainer.childCount) {
            val view = binding.questionContainer.getChildAt(i)
            if (view is Button && view.text == correctAnswer) {
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }
    }

    private fun updateScoreTextView() {
        binding.scoreTextView.text = "Score: $score/${quizzes.size}"
    }

    private fun saveScoreToDatabase() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val playerName = sharedPreferences.getString("username", "Unknown") ?: "Unknown"

        lifecycleScope.launch {
            val categoryId = quizzes.firstOrNull()?.category?.let { categoryDao.getCategoryByName(it)?.id } ?: return@launch
            val score = Score(categoryId = 1, score = this@QuizFragment.score, player = playerName)
            val scoreId = scoreDao.insert(score)
            Log.d(TAG, "Score inserted with ID: $scoreId")
            navigateToScoreFragment(categoryId) // Afficher les scores de la catégorie
        }
    }


    private fun navigateToScoreFragment(categoryId: Int) {
        val bundle = Bundle().apply {
            putInt("score", score)
            putInt("categoryId", categoryId)
        }
        findNavController().navigate(R.id.action_QuizFragment_to_ScoreFragment, bundle)
    }
}