package com.example.alpha_bank_qr.Utils

import android.content.Context
import android.database.Cursor
import com.example.alpha_bank_qr.Database.DBService
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.Database.QRDatabaseHelper

class DataUtils {
    companion object {
        private val data = ArrayList<DataItem>()
        private val titles = arrayOf(
            DataItem("фамилия", "surname"),
            DataItem("имя", "name"),
            DataItem("отчество", "patronymic"),
            DataItem("компания", "company"),
            DataItem("должность", "job_title"),
            DataItem("мобильный номер", "mobile"),
            DataItem("мобильный номер (другой)", "mobile_second"),
            DataItem("email", "email"),
            DataItem("email (другой)", "email_second"),
            DataItem("адрес", "address"),
            DataItem("адрес (другой)", "address_second"),
            DataItem("Сбербанк (расчетный счет)", "sberbank"),
            DataItem("ВТБ (расчетный счет)", "vtb"),
            DataItem("Альфа-Банк (расчетный счет)", "alfabank"),
            DataItem("vk", "vk"),
            DataItem("facebook", "facebook"),
            DataItem("instagram", "instagram"),
            DataItem("twitter", "twitter"),
            DataItem("заметки", "notes")
        )

        // Для отображения в профиле
        fun setUserData(cursor: Cursor): ArrayList<DataItem> {
            data.clear()
            for (i in titles.indices) {
                addItem(
                    titles[i].title,
                    cursor.getString(cursor.getColumnIndex(titles[i].description))
                )
            }
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
            getUsersFromMyCards(context).forEach {
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
                val dbHelper =
                    QRDatabaseHelper(context)
                dbHelper.updateUser(it)
                dbHelper.close()
            }
        }

        private fun getUsersFromMyCards(context: Context) : ArrayList<User> {
            val dbHelper =
                QRDatabaseHelper(context)
            val cursor = dbHelper.getUsersFromMyCards()
            val users = ArrayList<User>()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val user = parseDataToUser(setUserData(cursor), cursor.getString(cursor.getColumnIndex("photo")))
                    user.id = cursor.getInt(cursor.getColumnIndex("id"))
                    user.photo = cursor.getString(cursor.getColumnIndex("photo"))

                    users.add(user)

                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return users
        }

        private fun checkForDifference(oldUserData : String, newUserData : String) : String {
            if (oldUserData != "" && oldUserData != newUserData) return newUserData
            return oldUserData
        }
    }
}