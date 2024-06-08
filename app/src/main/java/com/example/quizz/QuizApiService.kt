package com.example.quizz

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

interface QuizApiService {
    @GET("quiz")
    fun getQuizzes(
        @Query("limit") limit: Int,
        @Query("category") category: String
    ): Call<QuizResponse>
}

data class QuizResponse(
    val quizzes: List<Quiz>
)

data class Quiz(
    val _id: String,
    val question: String,
    val answer: String,
    val badAnswers: List<String>,
    val category: String,
    val difficulty: String,
): Serializable
