package com.example.alpha_bank_qr.Entities

/*
    Пользователь будет иметь возможность добавить дополнительный мобильный номер, дополнительный
    email и дополнительный адрес

    Для каждой отдельной визитки будет создаваться новый пользователь с выборкой данных из
    основного пользователя, уже существующего в базе данных
 */

class User(
    var id: Int = 0,
    var photo : String = "",
    var isOwner : Int = 0,
    var isScanned : Int = 0,
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
    var sberbank: String = "",
    var vtb: String = "",
    var alfabank: String = "",
    var vk: String = "",
    var facebook: String = "",
    var instagram: String = "",
    var twitter: String = "",
    var notes : String = ""
) {
    override fun toString(): String {
        return "$photo|$name|$surname|$patronymic|$company|$jobTitle|$mobile|$mobileSecond|$email|$emailSecond|$address|$addressSecond|$sberbank|$vtb|$alfabank|$vk|$facebook|$instagram|$twitter|$notes"
    }
}