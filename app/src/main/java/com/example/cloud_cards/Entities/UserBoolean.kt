package com.example.cloud_cards.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Класс Пользователя, сгенерированного на основе родительского Пользователя
 */

@Entity(tableName = "usersBoolean")
data class UserBoolean(
    var parentId : String = "",
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
    var website : Boolean = false,
    var vk: Boolean = false,
    var telegram: Boolean = false,
    var facebook: Boolean = false,
    var instagram: Boolean = false,
    var twitter: Boolean = false,
    var notes : Boolean = false
) {
    @PrimaryKey var uuid: String = ""

    override fun equals(other: Any?): Boolean {
        val secondUser = other as UserBoolean
        return this.name == secondUser.name &&
                this.surname == secondUser.surname &&
                this.patronymic == secondUser.patronymic &&
                this.company == secondUser.company &&
                this.jobTitle == secondUser.jobTitle &&
                this.mobile == secondUser.mobile &&
                this.mobileSecond == secondUser.mobileSecond &&
                this.email == secondUser.email &&
                this.emailSecond == secondUser.emailSecond &&
                this.address == secondUser.address &&
                this.addressSecond == secondUser.addressSecond &&
                this.cardNumber == secondUser.cardNumber &&
                this.cardNumberSecond == secondUser.cardNumberSecond &&
                this.website == secondUser.website &&
                this.vk == secondUser.vk &&
                this.telegram == secondUser.telegram &&
                this.facebook == secondUser.facebook &&
                this.instagram == secondUser.instagram &&
                this.twitter == secondUser.twitter &&
                this.notes == secondUser.notes
    }
}