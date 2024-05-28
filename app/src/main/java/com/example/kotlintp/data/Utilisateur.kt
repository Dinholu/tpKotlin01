package com.example.kotlintp.data

//@Entity(tablename = "utilisateurs")
data class Utilisateur (
    //@PrimaryKey(autogenerate=true)
    val Id : Int,
    val Pseudo : String,
)