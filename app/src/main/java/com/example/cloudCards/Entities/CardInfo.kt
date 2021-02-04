package com.example.cloudCards.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardsInfo")
data class CardInfo(
    @PrimaryKey
    var id: String,
    var color: Int,
    var title: String = "",
    var cardId: String
)