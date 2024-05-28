package com.example.kotlintp.daointerface

import com.example.kotlintp.data.Utilisateur

//@Dao
interface I_Utilisateur {
    //@Query("SELECT * FROM utilisateurs WHERE pseudo = :username)
    fun getUserByUsername(username: String) : Utilisateur

    //@Insert
    fun insert(vararg utilisateur: Utilisateur)

    //@Update
    fun update(vararg utilisateur: Utilisateur)

    //@Delete
    fun delete(vararg utilisateur: Utilisateur)
}