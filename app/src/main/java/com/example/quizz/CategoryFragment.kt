package com.example.quizz

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentCategoryBinding
import androidx.lifecycle.lifecycleScope
import com.example.quizz.data.Category
import com.example.quizz.data.Quiz
import com.example.quizz.data.Quizzes
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var apiService: QuizApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        database = (requireActivity().application as QuizApplication).database
        val retrofit = Retrofit.Builder()
            .baseUrl("https://quizzapi.jomoreschi.fr/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(QuizApiService::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playerName = view.findViewById<TextView>(R.id.playerName)
        playerName.text = ("Bienvenue " + activity?.intent?.getStringExtra("username") + "!")

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories()
            createCategoryButtons(categories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCategoryImageResource(slug: String): Int {
        val resourceId = resources.getIdentifier(slug+"_round", "mipmap", requireContext().packageName)
        return if (resourceId != 0) resourceId else R.mipmap.ic_launcher_round
    }

    private fun createCategoryButtons(categories: List<Category>) {
        binding.categoryButtons.removeAllViews()
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.fab_margin)
        val gridLayout = binding.categoryButtons

        for (category in categories) {
            val buttonLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = ContextCompat.getDrawable(context, R.drawable.selector) // Apply button background
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
                }
                setPadding(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
                elevation = 8f // Adding shadow for elevation
            }

            val imageView = ImageView(requireContext()).apply {
                setImageResource(getCategoryImageResource(category.slug)) // Utilisez le slug pour obtenir l'image
                background = ContextCompat.getDrawable(context, R.drawable.selector) // Apply rounded image background
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, marginInPixels / 2)
                }
                scaleType = ImageView.ScaleType.FIT_XY // Adjust the scale type
                adjustViewBounds = true
            }

            val textView = TextView(requireContext()).apply {
                text = category.name
                typeface = resources.getFont(R.font.robotomedium)
                setTextColor(resources.getColor(R.color.white))
                textSize = 15f
                gravity = Gravity.CENTER
            }

            buttonLayout.addView(imageView)
            buttonLayout.addView(textView)

            buttonLayout.setOnClickListener {
                fetchQuizzes(category.slug, category.name, category.id)
            }

            gridLayout.addView(buttonLayout)
        }
    }

    private fun fetchQuizzes(categorySlug: String, categoryName: String, categoryId: Int) {
        apiService.getQuizzes(limit = 10, category = categorySlug).enqueue(object : Callback<Quizzes> {
            override fun onResponse(call: Call<Quizzes>, response: Response<Quizzes>) {
                if (response.isSuccessful) {
                    val quizzes = response.body()?.quizzes ?: emptyList()
                    navigateToQuizFragment(quizzes, categoryName, categoryId)
                }
            }

            override fun onFailure(call: Call<Quizzes>, t: Throwable) {
                // Gérer les erreurs ici
            }
        })
    }

    private fun navigateToQuizFragment(quizzes: List<Quiz>, categoryName: String, categoryId: Int) {
        val gson = Gson()
        val jsonQuizzes = gson.toJson(quizzes)
        val bundle = Bundle().apply {
            putString("quizzes", jsonQuizzes)
            putString("categoryName", categoryName)
            putInt("categoryId", categoryId)
        }
        findNavController().navigate(R.id.action_CategoryFragment_to_QuizFragment, bundle)
    }

    private fun createBundle(id: Int): Bundle {
        val bundle = Bundle().apply {
            putInt("categoryId", id)
        }
        return bundle
    }
}
