package com.example.alpha_bank_qr.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Пользователь будет иметь возможность добавить дополнительный мобильный номер, дополнительный
    email и дополнительный адрес

    Для каждой отдельной визитки будет создаваться новый пользователь с выборкой данных из
    основного пользователя, уже существующего в базе данных
 */

@Entity(tableName = "users")
data class User(
    var photo : String = "",
    var isOwner : Boolean = false,
    var isScanned : Boolean = false,
    var name: String = "",
    var surname: String = "",
    var patronymic: String = "",
    var company: String = "",
    var jobTitle: String = "",
    var mobile: String = "",
    var mobileSecond: String = "",
    var email: String = "",
    var emailSecond: String = "",
    var address: String = "",
    var addressSecond: String = "",
    var cardNumber: String = "",
    var cardNumberSecond: String = "",
    var website : String = "",
    var vk: String = "",
    var telegram: String = "",
    var facebook: String = "",
    var instagram: String = "",
    var twitter: String = "",
    var notes : String = ""
) {
    @PrimaryKey var id: String = ""

    override fun toString(): String {
        return "$photo|$name|$surname|$patronymic|$company|$jobTitle|$mobile|$mobileSecond|$email|$emailSecond|$address|$addressSecond|$cardNumber|$cardNumberSecond|$website|$vk|$telegram|$facebook|$instagram|$twitter|$notes"
    }
}