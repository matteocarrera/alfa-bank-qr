package com.example.alpha_bank_qr.Entities

import com.example.alpha_bank_qr.Utils.ImageUtils

/*
    Класс, хранящий в себе пользователя и его фотографию
 */

class Contact (
    var user: User,
    val image: ImageUtils
)