package com.example.kotlintp.daointerface

import com.example.kotlintp.data.Score

//@Dao
interface I_Score {
    //@Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getAllScoreForCategory(categoryId: Int) : Array<Score>

    //@Insert
    fun setScore(userId : Int, categoryId : Int, score : Int)
}