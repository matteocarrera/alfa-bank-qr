package com.example.alpha_bank_qr.Utils

import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.Entities.UserBoolean

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
            addItem("Номер карты 1", user.cardNumber)
            addItem("Номер карты 2", user.cardNumberSecond)
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

        fun generatedUsersEqual(firstUser: UserBoolean, secondUser: UserBoolean): Boolean {
            return firstUser.name == secondUser.name &&
                    firstUser.surname == secondUser.surname &&
                    firstUser.patronymic == secondUser.patronymic &&
                    firstUser.company == secondUser.company &&
                    firstUser.jobTitle == secondUser.jobTitle &&
                    firstUser.mobile == secondUser.mobile &&
                    firstUser.mobileSecond == secondUser.mobileSecond &&
                    firstUser.email == secondUser.email &&
                    firstUser.emailSecond == secondUser.emailSecond &&
                    firstUser.address == secondUser.address &&
                    firstUser.addressSecond == secondUser.addressSecond &&
                    firstUser.cardNumber == secondUser.cardNumber &&
                    firstUser.cardNumberSecond == secondUser.cardNumberSecond &&
                    firstUser.website == secondUser.website &&
                    firstUser.vk == secondUser.vk &&
                    firstUser.telegram == secondUser.telegram &&
                    firstUser.facebook == secondUser.facebook &&
                    firstUser.instagram == secondUser.instagram &&
                    firstUser.twitter == secondUser.twitter &&
                    firstUser.notes == secondUser.notes
        }

        fun getUserFromTemplate(user: User, userBoolean: UserBoolean): User {
            val currentUser = User()
            currentUser.parentId = userBoolean.parentId
            currentUser.uuid = userBoolean.uuid
            currentUser.name = checkField(user.name, userBoolean.name)
            currentUser.surname = checkField(user.surname, userBoolean.surname)
            currentUser.patronymic =
                checkField(user.patronymic, userBoolean.patronymic)
            currentUser.company = checkField(user.company, userBoolean.company)
            currentUser.jobTitle =
                checkField(user.jobTitle, userBoolean.jobTitle)
            currentUser.mobile = checkField(user.mobile, userBoolean.mobile)
            currentUser.mobileSecond =
                checkField(user.mobileSecond, userBoolean.mobileSecond)
            currentUser.email = checkField(user.email, userBoolean.email)
            currentUser.emailSecond =
                checkField(user.emailSecond, userBoolean.emailSecond)
            currentUser.address = checkField(user.address, userBoolean.address)
            currentUser.addressSecond =
                checkField(user.addressSecond, userBoolean.addressSecond)
            currentUser.cardNumber =
                checkField(user.cardNumber, userBoolean.cardNumber)
            currentUser.cardNumberSecond =
                checkField(user.cardNumberSecond, userBoolean.cardNumberSecond)
            currentUser.website = checkField(user.website, userBoolean.website)
            currentUser.vk = checkField(user.vk, userBoolean.vk)
            currentUser.telegram =
                checkField(user.telegram, userBoolean.telegram)
            currentUser.facebook =
                checkField(user.facebook, userBoolean.facebook)
            currentUser.instagram =
                checkField(user.instagram, userBoolean.instagram)
            currentUser.twitter = checkField(user.twitter, userBoolean.twitter)
            currentUser.notes = checkField(user.notes, userBoolean.notes)
            return currentUser
        }

        private fun checkField(field: String, isSelected: Boolean): String {
            if (isSelected)
                return field
            return ""
        }

        // Переводим выбранные данные в генераторе в пользователя для дальнейшего использования
        fun parseDataToUserCard(data: ArrayList<DataItem>): User {
            val user = User()
            data.forEach {
                when (it.title) {
                    "имя" -> user.name = it.description
                    "фамилия" -> user.surname = it.description
                    "отчество" -> user.patronymic = it.description
                    "компания" -> user.company = it.description
                    "должность" -> user.jobTitle = it.description
                    "мобильный номер" -> user.mobile = it.description
                    "мобильный номер (другой)" -> user.mobileSecond = it.description
                    "email" -> user.email = it.description
                    "email (другой)" -> user.emailSecond = it.description
                    "адрес" -> user.address = it.description
                    "адрес (другой)" -> user.addressSecond = it.description
                    "Номер карты 1" -> user.cardNumber = it.description
                    "Номер карты 2" -> user.cardNumberSecond = it.description
                    "Сайт" -> user.website = it.description
                    "vk" -> user.vk = it.description
                    "telegram" -> user.telegram = it.description
                    "facebook" -> user.facebook = it.description
                    "instagram" -> user.instagram = it.description
                    "twitter" -> user.twitter = it.description
                    "заметки" -> user.notes = it.description
                }
            }
            return user
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