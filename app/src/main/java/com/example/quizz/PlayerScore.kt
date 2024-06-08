package com.example.quizz

import android.content.SharedPreferences
import com.example.quizz.data.Score
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class PlayerScore(val username: String, val score: Int) {
    override fun toString(): String {
        return "PlayerScore(username='$username', score=$score)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other?.javaClass) return false

        other as PlayerScore

        if (username != other.username) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + score
        return result
    }
}

fun savePlayerScores(sharedPreferences: SharedPreferences, playerScores: List<Score>) {
   // Sauvegarder les scores des joueurs en base de données
    return;
}

fun getTopPlayerScores(sharedPreferences: SharedPreferences, number: Int): List<Score> {
    //
    return emptyList()
}