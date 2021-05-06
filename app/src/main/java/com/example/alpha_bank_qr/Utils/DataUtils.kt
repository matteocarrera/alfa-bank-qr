package com.example.alpha_bank_qr.Utils

import android.content.Context
import com.example.alpha_bank_qr.Database.DBService
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

        fun checkCardForExistence(context: Context, user : User) : Boolean {
            DBService.getScannedUsers(context)?.forEach {
                val userData = Json.toJson(user)
                val scannedUserData = Json.toJson(it)
                if (userData == scannedUserData) return true
            }
            return false
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

        // Если какие-то данные пустые (отсутствуют), то мы не добавляем, иначе добавляем
        private fun addItem(title: String, description: String) {
            if (description.isNotEmpty()) data.add(DataItem(title, description))
        }

        // Методы для получения существующих пользователей в визитках и обновления их данных
        fun updateMyCardsData(context: Context, user: User) {
            DBService.getUsersFromMyCards(context).forEach {
                it.name = checkForDifference(it.name, user.name)
                it.surname = checkForDifference(it.surname, user.surname)
                it.patronymic = checkForDifference(it.patronymic, user.patronymic)
                it.company = checkForDifference(it.company, user.company)
                it.jobTitle = checkForDifference(it.jobTitle, user.jobTitle)
                it.mobile = checkForDifference(it.mobile, user.mobile)
                it.mobileSecond = checkForDifference(it.mobileSecond, user.mobileSecond)
                it.email = checkForDifference(it.email, user.email)
                it.emailSecond = checkForDifference(it.emailSecond, user.emailSecond)
                it.address = checkForDifference(it.address, user.address)
                it.addressSecond = checkForDifference(it.addressSecond, user.addressSecond)
                //it.sberbank = checkForDifference(it.sberbank, user.sberbank)
                //it.vtb = checkForDifference(it.vtb, user.vtb)
                //it.alfabank = checkForDifference(it.alfabank, user.alfabank)
                it.vk = checkForDifference(it.vk, user.vk)
                it.facebook = checkForDifference(it.facebook, user.facebook)
                it.instagram = checkForDifference(it.instagram, user.instagram)
                it.twitter = checkForDifference(it.twitter, user.twitter)
                it.notes = checkForDifference(it.notes, user.notes)
                DBService.updateUser(context, it)
            }
        }

        private fun checkForDifference(oldUserData : String, newUserData : String) : String {
            if (oldUserData != "" && oldUserData != newUserData) return newUserData
            return oldUserData
        }
    }
}