package com.example.quizz.DAO

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.quizz.CategoryDao
import com.example.quizz.data.Score

@Dao
interface ScoreDAO {
    @Insert
    suspend fun insert(score: Score): Long

    @Query("SELECT COUNT(*) FROM scores WHERE EXISTS ( SELECT 1 FROM scores LIMIT 1)")
    suspend fun getNumberScores(): Int

    @Query("SELECT * FROM scores")
    suspend fun getAllScores(): List<Score>

    @Query("SELECT * FROM scores WHERE id = :id")
    suspend fun getScoreById(id: Int): Score?

    @Query("SELECT * FROM scores WHERE categoryId = :categoryId ORDER BY score ASC")
    suspend fun getScoresByCategory(categoryId: Int): List<Score>

    @Query("DELETE FROM scores")
    suspend fun nukeTable()

    @Query("SELECT * FROM scores WHERE categoryId = :categoryId ORDER BY score DESC LIMIT 3")
    suspend fun getTopScoresByCategory(categoryId: Int): List<Score>
}

suspend fun insertScore(scoreDao: ScoreDAO, categoryDao: CategoryDao, score: Score) {
    val categoryExists = categoryDao.getCategoryById(score.categoryId) != null
    if (categoryExists) {
        scoreDao.insert(score)
    } else {
        Log.e("Database", "Category with id ${score.categoryId} does not exist.")
    }
}