package com.example.alpha_bank_qr.Entities

/*
    Переменная userId используется как связка с таблицей users в базе данных для получения
    данных определенного пользователя, созданного для конкретной визитной карточки

    Иконка может выбираться пользователем из списка предложенных
 */

class Card (val id: Int,
            var color : Int,
            var title : String,
            var userId: Int)