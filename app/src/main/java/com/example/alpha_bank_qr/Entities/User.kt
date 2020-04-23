package com.example.alpha_bank_qr.Entities

/*
    Пользователь будет иметь возможность добавить дополнительный мобильный номер, дополнительный
    email и дополнительный адрес

    Для каждой отдельной визитки будет создаваться новый пользователь с выборкой данных из
    основного пользователя, уже существующего в базе данных
 */

class User(
    val id: Int,
    var photo : Int,
    var name: String,
    var surname: String,
    var patronymic: String,
    var company: String,
    var jobTitle: String,
    var mobile: String,
    var mobileSecond: String?,
    var email: String,
    var emailSecond: String,
    var address: String,
    var addressSecond: String,
    var vk: String,
    var facebook: String,
    var twitter: String
)