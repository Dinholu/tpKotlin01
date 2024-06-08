package com.example.quizz

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentCategoryBinding
import androidx.lifecycle.lifecycleScope
import com.example.quizz.data.Category
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
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
        // Initialiser Retrofit
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
        playerName.text = activity?.intent?.getStringExtra("username") ?: ""

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories()
            createCategoryButtons(categories)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createCategoryButtons(categories: List<Category>) {
        binding.categoryButtons.removeAllViews() // Clear any existing buttons
        val marginTopInPixels = resources.getDimensionPixelSize(R.dimen.fab_margin)

        for (category in categories) {
            val button = Button(requireContext()).apply {
                text = category.name
                typeface = resources.getFont(R.font.robotomedium)
                setTextColor(resources.getColor(R.color.white))
                setBackgroundResource(R.drawable.rounded_button)
                textSize = 15f

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Width
                    ViewGroup.LayoutParams.WRAP_CONTENT // Height
                ).apply {
                    setMargins(0, marginTopInPixels, 0, 0)
                }

                setOnClickListener {
                    fetchQuizzes(category.slug, category.name)
                }
            }
            binding.categoryButtons.addView(button)
        }
    }
    private fun fetchQuizzes(categorySlug: String, categoryName: String) {
        apiService.getQuizzes(limit = 10, category = categorySlug).enqueue(object : Callback<QuizResponse> {
            override fun onResponse(call: Call<QuizResponse>, response: Response<QuizResponse>) {
                if (response.isSuccessful) {
                    val quizzes = response.body()?.quizzes ?: emptyList()
                    navigateToQuizFragment(quizzes, categoryName)
                }
            }

            override fun onFailure(call: Call<QuizResponse>, t: Throwable) {
                // Gérer les erreurs ici
            }
        })
    }

    private fun navigateToQuizFragment(quizzes: List<Quiz>, categoryName: String) {
        val gson = Gson()
        val jsonQuizzes = gson.toJson(quizzes)
        val bundle = Bundle().apply {
            putString("quizzes", jsonQuizzes)
            putString("categoryName", categoryName)  // Passez le nom de la catégorie
        }
        findNavController().navigate(R.id.action_CategoryFragment_to_QuizFragment, bundle)
    }

    private fun createBundle(id: Int) : Bundle
    {
        val bundle = Bundle().apply {
            putInt("categoryId", id)
        }
        return bundle;
    }
}
