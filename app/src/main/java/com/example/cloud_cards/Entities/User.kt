package com.example.cloud_cards.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Основной класс Пользователя
 */

@Entity(tableName = "users")
data class User(
    // UUID родительского пользователя
    var parentId : String = "",
    var photo : String = "",
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
    // UUID, присвоенный конкретному пользователю
    @PrimaryKey var uuid: String = ""

    override fun toString(): String {
        return "$photo|$name|$surname|$patronymic|$company|$jobTitle|$mobile|$mobileSecond|$email|$emailSecond|$address|$addressSecond|$cardNumber|$cardNumberSecond|$website|$vk|$telegram|$facebook|$instagram|$twitter|$notes"
    }
}