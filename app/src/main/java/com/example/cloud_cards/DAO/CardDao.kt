package com.example.cloud_cards.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cloud_cards.Entities.Card

@Dao
interface CardDao {

    @Query("SELECT * FROM cards")
    fun getAllCards(): List<Card>

    @Query("SELECT title FROM cards")
    fun getAllCardsNames(): List<String>

    @Query("SELECT * FROM cards WHERE uuid = :uuid")
    fun getCardById(uuid: String): Card

    @Insert
    fun insertCard(card: Card)

    @Delete
    fun deleteCard(card: Card)
}