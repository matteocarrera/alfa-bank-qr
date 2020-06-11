package com.example.alpha_bank_qr.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Переменная userId используется как связка с таблицей users в базе данных для получения
    данных определенного пользователя, созданного для конкретной визитной карточки

    Иконка может выбираться пользователем из списка предложенных
 */

@Entity(tableName = "cards")
data class Card (
    var color : Int,
    var title : String,
    var userId: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}