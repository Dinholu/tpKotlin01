package com.example.kotlintp.daointerface

import com.example.kotlintp.data.Score

//@Dao
interface I_Score {
    //@Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getAllScoreForCategory(categoryId: Int) : Array<Score>

    //@Insert
    fun insert(vararg score: Score)

    //@Update
    fun update(vararg score: Score)

    //@Delete
    fun delete(vararg score: Score)
}