package com.example.alpha_bank_qr.Utils

import com.example.alpha_bank_qr.Entities.User

class Json {
    companion object {
        fun toJson(user : User) : String {
            return user.toString()
        }

        fun fromJson(json : String) : User {
            val userData = json.split(',')
            val user = User()
            user.isScanned = 1
            user.name = userData[0]
            user.surname = userData[1]
            user.patronymic = userData[2]
            user.company = userData[3]
            user.jobTitle = userData[4]
            user.mobile = userData[5]
            user.mobileSecond = userData[6]
            user.email = userData[7]
            user.emailSecond = userData[8]
            user.address = userData[9]
            user.addressSecond = userData[10]
            user.vk = userData[11]
            user.facebook = userData[12]
            user.twitter = userData[13]
            user.notes = userData[14]
            return user
        }
    }
}