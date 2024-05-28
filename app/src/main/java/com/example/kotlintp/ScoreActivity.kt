package com.example.kotlintp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE)
        val scores = sharedPreferences.getString("ScoreMusic", "")
        val textScore: TextView = findViewById(R.id.TextScore)
        textScore.text = scores;
    }

    fun getDictFromSharedPref(sharedprefvalue : String): MutableMap<String, Int> {
        val separatedvalues = sharedprefvalue.split(';')
        val playerScore = mutableMapOf<String, Int>()

        var counter = 0;
        var usename = "";
        for(value in separatedvalues) {
            if(counter % 2 == 0) {
                usename = value;
            } else {
                playerScore[usename] = value.toInt()
            }
        }

        return playerScore;
    }

    fun saveDictToSharedPref(scores : MutableMap<String, Int>) {
        val sortedPlayerScore = scores.toList().sortedByDescending { it.second }.toMap()

        var dictSerialization = "";

        for(key in sortedPlayerScore.keys) {
            dictSerialization += key;
            dictSerialization += scores[key];
            dictSerialization += ";";
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("ScoreMusic", dictSerialization)
        editor.apply()
    }

    fun saveScore(username : String, score : Int, categorie : String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE)
        val scores = sharedPreferences.getString(categorie, "")

        val playerScore = getDictFromSharedPref(scores.toString())

        playerScore[username] = score

        saveDictToSharedPref(playerScore)
    }
}