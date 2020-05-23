package com.example.alpha_bank_qr.Entities

/*
    Класс для отображения сохраненных визиток в окне просмотра всех визиток, к БД отношения
    не имеет
 */

class SavedCard (
    val id : Int,
    val photo : String,
    val name : String,
    val surname : String,
    val jobTitle : String,
    val company : String
)