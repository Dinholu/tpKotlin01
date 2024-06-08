package com.example.quizz

import android.app.Application
import androidx.room.Room

class QuizApplication : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "quiz-database"
        )
            .fallbackToDestructiveMigration() // Permettre les migrations destructrices
            .build()
    }}