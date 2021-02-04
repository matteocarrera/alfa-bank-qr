package com.example.cloudCards.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cloudCards.Entities.CardInfo

@Dao
interface CardInfoDao {

    @Query("SELECT * FROM cardsInfo")
    fun getAllCards(): List<CardInfo>

    @Query("SELECT title FROM cardsInfo")
    fun getAllCardsNames(): List<String>

    @Query("SELECT * FROM cardsInfo WHERE id = :id")
    fun getCardById(id: Int): CardInfo

    @Insert
    fun insertCard(cardInfo: CardInfo)

    @Delete
    fun deleteCard(cardInfo: CardInfo)
}