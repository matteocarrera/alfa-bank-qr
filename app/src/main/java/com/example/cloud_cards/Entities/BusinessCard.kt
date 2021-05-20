package com.example.cloud_cards.Entities

/*
    Класс визитки, содержащий в себе тип и сами данные по визитке
 */

class BusinessCard<T>(val type: CardType, val data: T)