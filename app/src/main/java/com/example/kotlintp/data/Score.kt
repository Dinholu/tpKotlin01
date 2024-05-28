package com.example.kotlintp.data

/*@Entity(tablename = "scores",
    foreignKeys = [
        ForeignKey(
            entity = Utilisateur::class,
            parentColumns = ["Id"],
            childColumns = ["IdUtilisateur"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categorie::class,
            parentColumns = ["Id"],
            childColumns = ["IdCategorie"],
            onDelete = ForeignKey.CASCADE
        )]
)
*/
data class Score (
    //@PrimaryKey
    val IdUtilisateur : Int,
    //@PrimaryKey
    val IdCategorie : Int,
    val Score : Int,
)