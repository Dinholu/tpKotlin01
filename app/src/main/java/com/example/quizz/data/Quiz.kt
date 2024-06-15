package com.example.quizz.data
import java.io.Serializable


data class Quiz(
    val _id: String,
    val question: String,
    val answer: String,
    val badAnswers: List<String>,
    val category: String,
    val difficulty: String,
): Serializable