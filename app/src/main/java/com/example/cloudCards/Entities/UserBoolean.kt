package com.example.cloudCards.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Класс Пользователя, сгенерированного на основе родительского Пользователя
 */

@Entity(tableName = "cards")
data class UserBoolean(
    var parentId: String = "",
    var name: Boolean = false,
    var surname: Boolean = false,
    var patronymic: Boolean = false,
    var company: Boolean = false,
    var jobTitle: Boolean = false,
    var mobile: Boolean = false,
    var mobileSecond: Boolean = false,
    var email: Boolean = false,
    var emailSecond: Boolean = false,
    var address: Boolean = false,
    var addressSecond: Boolean = false,
    var cardNumber: Boolean = false,
    var cardNumberSecond: Boolean = false,
    var website: Boolean = false,
    var vk: Boolean = false,
    var telegram: Boolean = false,
    var facebook: Boolean = false,
    var instagram: Boolean = false,
    var twitter: Boolean = false,
    var notes: Boolean = false
) {
    @PrimaryKey
    var uuid: String = ""
}