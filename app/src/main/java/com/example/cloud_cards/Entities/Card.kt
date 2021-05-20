package com.example.cloud_cards.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/*
    Класс визитки пользователя в TemplatesFragment
 */

@Entity(tableName = "cards")
data class Card (
    @PrimaryKey
    var uuid: String,
    var type: CardType,
    var color: String,
    var title: String,
    var cardUuid: String
): Serializable