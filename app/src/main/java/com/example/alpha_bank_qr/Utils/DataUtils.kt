package com.example.alpha_bank_qr.Utils

import com.example.alpha_bank_qr.Entities.CardInfo
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.Entities.UserBoolean
import java.util.HashMap

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

        fun cardInfoToMap(cardInfo: CardInfo): HashMap<String, String> {
            val updateMap = HashMap<String, String>()
            updateMap["id"] = cardInfo.id
            updateMap["color"] = cardInfo.color.toString()
            updateMap["title"] = cardInfo.title
            updateMap["cardId"] = cardInfo.cardId
            return updateMap
        }

        fun userToMap(user: User): HashMap<String, String> {
            val updateMap = HashMap<String, String>()
            updateMap["uuid"] = user.uuid
            updateMap["parentId"] = user.parentId
            updateMap["photo"] = user.photo
            updateMap["name"] = user.name
            updateMap["surname"] = user.surname
            updateMap["patronymic"] = user.patronymic
            updateMap["company"] = user.company
            updateMap["jobTitle"] = user.jobTitle
            updateMap["mobile"] = user.mobile
            updateMap["mobileSecond"] = user.mobileSecond
            updateMap["email"] = user.email
            updateMap["emailSecond"] = user.emailSecond
            updateMap["address"] = user.address
            updateMap["addressSecond"] = user.addressSecond
            updateMap["cardNumber"] = user.cardNumber
            updateMap["cardNumberSecond"] = user.cardNumberSecond
            updateMap["website"] = user.website
            updateMap["vk"] = user.vk
            updateMap["telegram"] = user.telegram
            updateMap["facebook"] = user.facebook
            updateMap["instagram"] = user.instagram
            updateMap["twitter"] = user.twitter
            updateMap["notes"] = user.notes
            return updateMap
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