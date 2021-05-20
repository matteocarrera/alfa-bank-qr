package com.example.cloud_cards.Entities

/*
    Класс компании, используемый в визитках
 */

class Company(
    // Родительский UUID, по которому можно обратиться к визитке
    val parentUuid: String,

    // UUID компании
    val uuid: String,

    // Наименование компании
    val name: String,

    // ФИО ответственного от компании
    val responsibleFullName: String,

    // Должность ответственного от компании
    val responsibleJobTitle: String,

    // Адрес
    val address: String,

    // Телефон
    val phone: String,

    // Электронная почта
    val email: String,

    // Сайт
    val website: String
)