package com.example.kotlintp

import com.example.kotlintp.data.Categories
import com.example.kotlintp.data.Questions
import com.example.kotlintp.data.Reponses

object DataProvider {
    fun getQuestions(): List<Questions> {
        return listOf(
            Questions(
                1,
                "Quel acteur incarne le personnage de Jack Dawson dans le film Titanic ?",
                Categories(1, "url/to/image1", "Cinéma"),
                listOf(
                    Reponses(1, "Leonardo DiCaprio", true),
                    Reponses(2, "Tom Cruise", false),
                    Reponses(3, "Brad Pitt", false)
                )
            ),
            Questions(
                2,
                "Quel est le réalisateur du film 'Inception' ?",
                Categories(1, "url/to/image2", "Cinéma"),
                listOf(
                    Reponses(1, "Christopher Nolan", true),
                    Reponses(2, "Steven Spielberg", false),
                    Reponses(3, "Quentin Tarantino", false)
                )
            ),
        )
    }

    fun getCategories(): List<Categories> {
        return getQuestions().map { it.Categorie }.distinct()
    }
}
