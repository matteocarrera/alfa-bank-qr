package com.example.cloud_cards.Utils

import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Entities.UserBoolean

class DataUtils {
    companion object {
        private val data = ArrayList<DataItem>()

        // Для отображения в профиле
        fun setUserData(user: User): ArrayList<DataItem> {
            data.clear()
            addItem("фамилия", user.surname)
            addItem("имя", user.name)
            addItem("отчество", user.patronymic)
            addItem("компания", user.company)
            addItem("должность", user.jobTitle)
            addItem("мобильный номер", user.mobile)
            addItem("мобильный номер (другой)", user.mobileSecond)
            addItem("email", user.email)
            addItem("email (другой)", user.emailSecond)
            addItem("адрес", user.address)
            addItem("адрес (другой)", user.addressSecond)
            addItem("Сайт", user.website)
            addItem("vk", user.vk)
            addItem("telegram", user.telegram)
            addItem("facebook", user.facebook)
            addItem("instagram", user.instagram)
            addItem("twitter", user.twitter)
            addItem("заметки", user.notes)
            return data
        }

        fun parseDataToUser(data: ArrayList<DataItem>): UserBoolean {
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

        // Переводим выбранные данные в генераторе в пользователя для дальнейшего использования
        fun parseDataToUser(data : ArrayList<DataItem>, photoUUID : String) : User {
            val user = User()
            data.forEach {
                when (it.title) {
                    "имя" -> user.name = it.data
                    "фамилия" -> user.surname = it.data
                    "отчество" -> user.patronymic = it.data
                    "компания" -> user.company = it.data
                    "должность" -> user.jobTitle = it.data
                    "мобильный номер" -> user.mobile = it.data
                    "мобильный номер (другой)" -> user.mobileSecond = it.data
                    "email" -> user.email = it.data
                    "email (другой)" -> user.emailSecond = it.data
                    "адрес" -> user.address = it.data
                    "адрес (другой)" -> user.addressSecond = it.data
                    "Номер карты 1" -> user.cardNumber = it.data
                    "Номер карты 2" -> user.cardNumberSecond = it.data
                    "Сайт" -> user.website = it.data
                    "vk" -> user.vk = it.data
                    "telegram" -> user.telegram = it.data
                    "facebook" -> user.facebook = it.data
                    "instagram" -> user.instagram = it.data
                    "twitter" -> user.twitter = it.data
                    "заметки" -> user.notes = it.data
                }
            }
            return user
        }

        fun getUserFromTemplate(user: User, userBoolean: UserBoolean): User {
            val currentUser = User()
            currentUser.parentId = userBoolean.parentId
            currentUser.uuid = userBoolean.uuid
            currentUser.photo = if (user.photo != "") user.photo else ""
            currentUser.name = checkField(user.name, userBoolean.name)
            currentUser.surname = checkField(user.surname, userBoolean.surname)
            currentUser.patronymic = checkField(user.patronymic, userBoolean.patronymic)
            currentUser.company = checkField(user.company, userBoolean.company)
            currentUser.jobTitle = checkField(user.jobTitle, userBoolean.jobTitle)
            currentUser.mobile = checkField(user.mobile, userBoolean.mobile)
            currentUser.mobileSecond = checkField(user.mobileSecond, userBoolean.mobileSecond)
            currentUser.email = checkField(user.email, userBoolean.email)
            currentUser.emailSecond = checkField(user.emailSecond, userBoolean.emailSecond)
            currentUser.address = checkField(user.address, userBoolean.address)
            currentUser.addressSecond = checkField(user.addressSecond, userBoolean.addressSecond)
            currentUser.cardNumber = checkField(user.cardNumber, userBoolean.cardNumber)
            currentUser.cardNumberSecond = checkField(user.cardNumberSecond, userBoolean.cardNumberSecond)
            currentUser.website = checkField(user.website, userBoolean.website)
            currentUser.vk = checkField(user.vk, userBoolean.vk)
            currentUser.telegram = checkField(user.telegram, userBoolean.telegram)
            currentUser.facebook = checkField(user.facebook, userBoolean.facebook)
            currentUser.instagram = checkField(user.instagram, userBoolean.instagram)
            currentUser.twitter = checkField(user.twitter, userBoolean.twitter)
            currentUser.notes = checkField(user.notes, userBoolean.notes)
            return currentUser
        }

        private fun checkField(field: String, isSelected: Boolean): String {
            return if (isSelected) field else String()
        }

        // Если какие-то данные пустые (отсутствуют), то мы не добавляем, иначе добавляем
        private fun addItem(title: String, description: String) {
            if (description.isNotEmpty()) data.add(DataItem(title, description))
        }
    }
}