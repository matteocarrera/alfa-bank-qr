package com.example.alpha_bank_qr.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardsInfo")
data class CardInfo(
    var color: Int,
    var title: String = "",
    var cardId: String
) {
    @PrimaryKey
    var id: String = cardId
}