package com.example.cloud_cards.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Класс пары id, необходимых для получения данных по контакту
 */

@Entity(tableName = "idPairs")
class IdPair (
    @PrimaryKey
    var uuid: String = "",
    var parentUuid: String = ""
)
