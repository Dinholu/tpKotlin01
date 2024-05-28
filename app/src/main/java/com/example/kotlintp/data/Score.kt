package com.example.kotlintp.data

//@Entity(tablename = "scores")
data class Score (
    val IdUtilisateur : Int,
    val IdCategorie : Int,
    val Score : Int,
)