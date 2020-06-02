package com.example.alpha_bank_qr.Utils

import android.content.Context
import com.example.alpha_bank_qr.Database.DBService
import com.example.alpha_bank_qr.Database.QRDatabaseHelper
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User

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
            addItem("Сбербанк (расчетный счет)", user.sberbank)
            addItem("ВТБ (расчетный счет)", user.vtb)
            addItem("Альфа-Банк (расчетный счет)", user.alfabank)
            addItem("vk", user.vk)
            addItem("facebook", user.facebook)
            addItem("instagram", user.instagram)
            addItem("twitter", user.twitter)
            addItem("заметки", user.notes)
            return data
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
            user.photo = photoUUID
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
                    "Сбербанк (расчетный счет)" -> user.sberbank = it.description
                    "ВТБ (расчетный счет)" -> user.vtb = it.description
                    "Альфа-Банк (расчетный счет)" -> user.alfabank = it.description
                    "vk" -> user.vk = it.description
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
                it.sberbank = checkForDifference(it.sberbank, user.sberbank)
                it.vtb = checkForDifference(it.vtb, user.vtb)
                it.alfabank = checkForDifference(it.alfabank, user.alfabank)
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