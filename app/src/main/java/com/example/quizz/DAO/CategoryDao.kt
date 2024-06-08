package com.example.quizz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import android.util.Log
import com.example.quizz.data.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Query("SELECT COUNT(*) FROM categories WHERE EXISTS ( SELECT 1 FROM categories LIMIT 1)")
    suspend fun getNumberCategory(): Int

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getCategoryByName(name: String): Category?

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?
    @Query("DELETE FROM categories")
    suspend fun nukeTable()
}

suspend fun insertCategory(categoryDao: CategoryDao, category: Category) {
    val categoryExists = categoryDao.getCategoryById(category.id) != null
    if (categoryExists) {
        categoryDao.insert(category)
    } else {
        Log.e("Database", "Category with id ${category.id} does not exist.")
    }
}