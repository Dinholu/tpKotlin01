package com.example.quizz

import com.example.quizz.data.Quizzes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApiService {
    @GET("quiz")
    fun getQuizzes(
        @Query("limit") limit: Int,
        @Query("category") category: String
    ): Call<Quizzes>
}


