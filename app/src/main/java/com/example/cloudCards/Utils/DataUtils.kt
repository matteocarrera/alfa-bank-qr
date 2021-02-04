package com.example.cloudCards.Utils

import com.example.cloudCards.Entities.CardInfo
import com.example.cloudCards.Entities.DataItem
import com.example.cloudCards.Entities.User
import com.example.cloudCards.Entities.UserBoolean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class DataUtils {
    companion object {
        private val data = ArrayList<DataItem>()

        // Для отображения в профиле
        fun setUserData(data: User): ArrayList<DataItem> {
            this.data.clear()
            addItem("фамилия", data.surname)
            addItem("имя", data.name)
            addItem("отчество", data.patronymic)
            addItem("компания", data.company)
            addItem("должность", data.jobTitle)
            addItem("мобильный номер", data.mobile)
            addItem("мобильный номер (другой)", data.mobileSecond)
            addItem("email", data.email)
            addItem("email (другой)", data.emailSecond)
            addItem("адрес", data.address)
            addItem("адрес (другой)", data.addressSecond)
            addItem("Номер карты 1", data.cardNumber)
            addItem("Номер карты 2", data.cardNumberSecond)
            addItem("Сайт", data.website)
            addItem("vk", data.vk)
            addItem("telegram", data.telegram)
            addItem("facebook", data.facebook)
            addItem("instagram", data.instagram)
            addItem("twitter", data.twitter)
            addItem("заметки", data.notes)
            return this.data
        }

        fun parseDataToUserCard(data: ArrayList<DataItem>): UserBoolean {
            val user = UserBoolean()
            data.forEach {
                when (it.title) {
                    "фамилия" -> user.surname = true
                    "имя" -> user.name = true
                    "отчество" -> user.patronymic = true
                    "компания" -> user.company = true
                    "должность" -> user.jobTitle = true
                    "мобильный номер" -> user.mobile = true
                    "мобильный номер (другой)" -> user.mobileSecond = true
                    "email" -> user.email = true
                    "email (другой)" -> user.emailSecond = true
                    "адрес" -> user.address = true
                    "адрес (другой)" -> user.addressSecond = true
                    "Номер карты 1" -> user.cardNumber = true
                    "Номер карты 2" -> user.cardNumberSecond = true
                    "Сайт" -> user.website = true
                    "vk" -> user.vk = true
                    "telegram" -> user.telegram = true
                    "facebook" -> user.facebook = true
                    "instagram" -> user.instagram = true
                    "twitter" -> user.twitter = true
                    "заметки" -> user.notes = true
                }
            }
            return user
        }

        fun mapToCardInfo(map: HashMap<String, String>): CardInfo {
            return CardInfo(
                id = map.getValue("id"),
                color = map.getValue("color").toInt(),
                title = map.getValue("title"),
                cardId = map.getValue("cardId")
            )
        }

        fun cardInfoToMap(cardInfo: CardInfo): HashMap<String, String> {
            val updateMap = HashMap<String, String>()
            updateMap["id"] = cardInfo.id
            updateMap["color"] = cardInfo.color.toString()
            updateMap["title"] = cardInfo.title
            updateMap["cardId"] = cardInfo.cardId
            return updateMap
        }

        fun mapToUser(map: HashMap<String, String>): User {
            val gson = Gson()
            val jsonString = gson.toJson(map)
            return gson.fromJson(jsonString, User::class.java)
        }

        fun userToMap(user: User): HashMap<String, String> {
            val gson = Gson()
            val jsonString = gson.toJson(user)
            return gson.fromJson(
                jsonString,
                object : TypeToken<HashMap<String?, String?>?>() {}.getType()
            )
        }

        fun getUserFromTemplate(data: User, userBoolean: UserBoolean): User {
            val currentUser = User()
            currentUser.parentId = userBoolean.parentId
            currentUser.uuid = userBoolean.uuid
            currentUser.name = checkField(data.name, userBoolean.name)
            currentUser.surname = checkField(data.surname, userBoolean.surname)
            currentUser.patronymic =
                checkField(data.patronymic, userBoolean.patronymic)
            currentUser.company = checkField(data.company, userBoolean.company)
            currentUser.jobTitle =
                checkField(data.jobTitle, userBoolean.jobTitle)
            currentUser.mobile = checkField(data.mobile, userBoolean.mobile)
            currentUser.mobileSecond =
                checkField(data.mobileSecond, userBoolean.mobileSecond)
            currentUser.email = checkField(data.email, userBoolean.email)
            currentUser.emailSecond =
                checkField(data.emailSecond, userBoolean.emailSecond)
            currentUser.address = checkField(data.address, userBoolean.address)
            currentUser.addressSecond =
                checkField(data.addressSecond, userBoolean.addressSecond)
            currentUser.cardNumber =
                checkField(data.cardNumber, userBoolean.cardNumber)
            currentUser.cardNumberSecond =
                checkField(data.cardNumberSecond, userBoolean.cardNumberSecond)
            currentUser.website = checkField(data.website, userBoolean.website)
            currentUser.vk = checkField(data.vk, userBoolean.vk)
            currentUser.telegram =
                checkField(data.telegram, userBoolean.telegram)
            currentUser.facebook =
                checkField(data.facebook, userBoolean.facebook)
            currentUser.instagram =
                checkField(data.instagram, userBoolean.instagram)
            currentUser.twitter = checkField(data.twitter, userBoolean.twitter)
            currentUser.notes = checkField(data.notes, userBoolean.notes)
            return currentUser
        }

        private fun checkField(field: String, isSelected: Boolean): String {
            if (isSelected)
                return field
            return ""
        }

        // Если какие-то данные пустые (отсутствуют), то мы не добавляем, иначе добавляем
        private fun addItem(title: String, description: String) {
            if (description.isNotEmpty()) data.add(DataItem(title, description))
        }

        private fun checkForDifference(oldUserData: String, newUserData: String): String {
            if (oldUserData != "" && oldUserData != newUserData) return newUserData
            return oldUserData
        }
    }
}