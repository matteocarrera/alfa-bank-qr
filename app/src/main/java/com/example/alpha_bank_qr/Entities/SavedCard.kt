package com.example.alpha_bank_qr.Entities

import android.graphics.drawable.Drawable

/*
    Класс для отображения сохраненных визиток в окне просмотра всех визиток, к БД отношения
    не имеет
 */

class SavedCard (
    val id : Int,
    val photo : Drawable,
    val name : String,
    val jobTitle : String,
    val company : String
)