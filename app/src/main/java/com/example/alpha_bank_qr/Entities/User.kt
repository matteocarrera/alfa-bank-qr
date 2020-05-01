package com.example.alpha_bank_qr.Entities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

/*
    Пользователь будет иметь возможность добавить дополнительный мобильный номер, дополнительный
    email и дополнительный адрес

    Для каждой отдельной визитки будет создаваться новый пользователь с выборкой данных из
    основного пользователя, уже существующего в базе данных
 */

class User(
    val id: Int,
    var photo : Bitmap,
    var isOwner : Int,
    var isScanned : Int,
    var name: String,
    var surname: String,
    var patronymic: String,
    var company: String,
    var jobTitle: String,
    var mobile: String,
    var mobileSecond: String,
    var email: String,
    var emailSecond: String,
    var address: String,
    var addressSecond: String,
    var vk: String,
    var facebook: String,
    var twitter: String,
    var notes : String
) {
    override fun toString(): String {
        return "User[id: ${this.id}, photo: ${this.photo}, isOwner: ${this.isOwner}, isScanned: ${this.isScanned}, name: ${this.name}, surname: ${this.surname}, patronymic: ${this.patronymic}, company: ${this.company}, jobTitle: ${this.jobTitle}, mobile: ${this.mobile}, mobileSecond: ${this.mobileSecond}, email: ${this.email}, emailSecond: ${this.emailSecond}, address: ${this.address}, addressSecond: ${this.addressSecond}, vk: ${this.vk}, facebook: ${this.facebook}, twitter: ${this.twitter}, notes: ${this.notes}]"
    }
}