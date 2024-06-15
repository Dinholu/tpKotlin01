package com.example.quizz

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.quizz.databinding.ActivityMainBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = (application as QuizApplication).database

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_game)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_changer_pseudo -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_voir_scores -> {
                    navController.navigate(R.id.LeaderboardFragment)
                    true
                }
                R.id.navigation_voir_categories -> {
                    navController.navigate(R.id.CategoryFragment)
                    true
                }
                else -> false
            }
        }

        appBarConfiguration = AppBarConfiguration(setOf(R.id.CategoryFragment, R.id.LeaderboardFragment))

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.LeaderboardFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    bottomNavigationView.menu.findItem(R.id.navigation_voir_scores).isChecked = true
                }
                R.id.CategoryFragment -> {
                    bottomNavigationView.menu.findItem(R.id.navigation_voir_categories).isChecked = true
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }


        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories()
            categories.forEach { category ->
                println("Category: ${category.name}")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_game)
        return when (navController.currentDestination?.id) {
            R.id.ScoreFragment -> {
                navController.navigate(R.id.action_ScoreFragment_to_CategoryFragment)
                true
            }
            else -> navController.navigateUp() || super.onSupportNavigateUp()
        }
    }
}
