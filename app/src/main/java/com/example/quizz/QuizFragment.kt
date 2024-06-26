package com.example.quizz
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.fragment.findNavController
import com.example.quizz.AppDatabase
import com.example.quizz.CategoryDao
import com.example.quizz.DAO.ScoreDAO
import com.example.quizz.QuizApplication
import com.example.quizz.R
import com.example.quizz.data.Quiz
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
    private var categoryId: Int? = 0
    private lateinit var database: AppDatabase
    private lateinit var scoreDao: ScoreDAO
    private lateinit var categoryDao: CategoryDao
    private val blinkHandler = Handler(Looper.getMainLooper())
    private var blinkRunnable: Runnable? = null

    companion object {
        private const val TIMER_DELAY = 10000
        private const val TAG = "QuizFragment"
        private const val STATE_CURRENT_PAGE_INDEX = "currentPageIndex"
        private const val STATE_SCORE = "score"
        private const val STATE_QUIZZES = "quizzes"
        private const val STATE_CATEGORY_NAME = "categoryName"
        private const val STATE_CATEGORY_ID = "categoryId"
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
            categoryId = savedInstanceState.getInt(STATE_CATEGORY_ID)
            val jsonQuizzes = savedInstanceState.getString(STATE_QUIZZES)
            if (jsonQuizzes != null) {
                quizzes = Gson().fromJson(jsonQuizzes, object : TypeToken<List<Quiz>>() {}.type)
            }
        } else {
            val jsonQuizzes = arguments?.getString("quizzes")
            categoryName = arguments?.getString("categoryName")
            categoryId = arguments?.getInt("categoryId")
            if (jsonQuizzes == null) {
                Log.e(TAG, "No quizzes found in arguments")
                return
            }
            quizzes = Gson().fromJson(jsonQuizzes, object : TypeToken<List<Quiz>>() {}.type)
        }

        Log.d(TAG, "Quizzes loaded: ${quizzes.size}")

        (activity as? AppCompatActivity)?.supportActionBar?.title = categoryId.toString() + " - " + categoryName  // Met à jour le titre de la Toolbar

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
        categoryId?.let { outState.putInt(STATE_CATEGORY_ID, it) }
        outState.putString(STATE_QUIZZES, Gson().toJson(quizzes))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        blinkRunnable?.let { blinkHandler.removeCallbacks(it) }
        _binding = null
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(TIMER_DELAY.toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                _binding?.let {
                    it.timerText.text = "Temps restant : ${millisUntilFinished / 1000}"
                }
            }

            override fun onFinish() {
                _binding?.let { binding ->
                    disableButtons()
                    showCorrectAnswer(quizzes[currentPageIndex].answer)
                    binding.questionContainer.postDelayed({
                        currentPageIndex++
                        navigateToNextQuestion()
                    }, 2000) // Delay to show the correct answer before moving to the next question
                }
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
            Log.d(TAG,"Questionnaire terminé")
            saveScoreToDatabase()  // Enregistrer le score en base de données
        }
    }

    private fun displayCurrentQuestion() {
        _binding?.let { binding ->
            if (currentPageIndex >= quizzes.size) return

            binding.questionContainer.removeAllViews()
            binding.questionProgressBar.progress = currentPageIndex + 1

            val quiz = quizzes[currentPageIndex]
            Log.d(TAG, "Displaying question: ${quiz.question}")

            val questionTextView = TextView(requireContext()).apply {
                text = quiz.question
                typeface = resources.getFont(R.font.robotomedium)
                textSize = 22f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
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
    }

    private fun disableButtons() {
        _binding?.let { binding ->
            val childCount = binding.questionContainer.childCount
            for (i in 0 until childCount) {
                val view = binding.questionContainer.getChildAt(i)
                if (view is Button) {
                    view.isEnabled = false
                }
            }
        }
    }

    private fun checkAnswer(selectedButton: Button, isCorrect: Boolean, correctAnswer: String) {
        _binding?.let { binding ->
            if (isCorrect) {
                score++
                selectedButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_green)
            } else {
                selectedButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_red)
                showCorrectAnswer(correctAnswer)
            }
            updateScoreTextView()
            binding.questionContainer.postDelayed({
                currentPageIndex++
                navigateToNextQuestion()
            }, 2000)
        }
    }

    private fun showCorrectAnswer(correctAnswer: String) {
        _binding?.let { binding ->
            val childCount = binding.questionContainer.childCount
            for (i in 0 until childCount) {
                val view = binding.questionContainer.getChildAt(i)
                if (view is Button && view.text == correctAnswer) {
                    // Faire clignoter le bouton de la bonne réponse
                    blinkCorrectAnswer(view)
                    // Ajouter un Listener pour passer à la question suivante
                    view.setOnClickListener {
                        currentPageIndex++
                        navigateToNextQuestion()
                    }
                }
            }
        }
    }


    private fun blinkCorrectAnswer(button: Button) {
        val drawableGreen = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_green)
        val drawableDefault = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)

        blinkRunnable = object : Runnable {
            private var useGreen = true
            override fun run() {
                button.background = if (useGreen) drawableGreen else drawableDefault
                useGreen = !useGreen
                blinkHandler.postDelayed(this, 250)
            }
        }
        blinkHandler.post(blinkRunnable!!)
    }

    private fun updateScoreTextView() {
        _binding?.let {
            it.scoreTextView.text = "Score: $score/${quizzes.size}"
        }
    }

    private fun saveScoreToDatabase() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val playerName = sharedPreferences.getString("username", "Unknown") ?: "Unknown"

        lifecycleScope.launch {
            Log.d(TAG, "Inside saveScoreToDatabase coroutine")
            val categoryId = this@QuizFragment.categoryId
            Log.d(TAG, "Category ID: $categoryId")
            val score =
                categoryId?.let { Score(categoryId = it, score = this@QuizFragment.score, player = playerName) }
            val scoreId = score?.let { scoreDao.insert(it) }
            Log.d(TAG, "Score inserted with ID: $scoreId")
            if (categoryId != null) {
                navigateToScoreFragment(categoryId)
            }
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
