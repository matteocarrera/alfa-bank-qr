package com.example.alpha_bank_qr.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.alpha_bank_qr.Entities.Card

@Dao
interface CardDao {

    @Query("SELECT * FROM cards")
    fun getAllCards(): List<Card>

    @Query("SELECT title FROM cards")
    fun getAllCardsNames() : List<String>

    @Insert
    fun insertCard(card: Card)

    @Delete
    fun deleteCard(card: Card)
}