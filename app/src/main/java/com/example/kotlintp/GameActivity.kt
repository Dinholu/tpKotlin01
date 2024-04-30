package com.example.kotlintp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CategoriesFragment.newInstance(1))
            .commit()
    }
}