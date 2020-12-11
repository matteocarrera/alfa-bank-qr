package com.example.alpha_bank_qr.Database

import androidx.room.TypeConverter
import com.example.alpha_bank_qr.Entities.CardInfo
import com.example.alpha_bank_qr.Entities.UserBoolean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


public class DataConverters {

    @TypeConverter
    fun fromCardsList(userBoolean: List<UserBoolean>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<UserBoolean?>?>() {}.getType()
        return gson.toJson(userBoolean, type)
    }

    @TypeConverter
    fun toCardsList(userBooleanListString: String): List<UserBoolean> {
        val gson = Gson()
        val type = object :
            TypeToken<List<UserBoolean?>?>() {}.type
        return gson.fromJson(userBooleanListString, type)
    }

    @TypeConverter
    fun fromCardsInfoList(cardInfo: List<CardInfo>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<CardInfo?>?>() {}.getType()
        return gson.toJson(cardInfo, type)
    }

    @TypeConverter
    fun toCardsInfoList(cardsInfoListString: String): List<CardInfo> {
        val gson = Gson()
        val type = object :
            TypeToken<List<CardInfo?>?>() {}.type
        return gson.fromJson(cardsInfoListString, type)
    }
}