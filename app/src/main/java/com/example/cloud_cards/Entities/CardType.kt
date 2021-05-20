package com.example.cloud_cards.Entities

/*
    Enum для определения типа визитки

    Используется Lowercase для совместимости с iOS
 */

enum class CardType(val rawValue: String) {
    personal("personal"),
    company("company")
}