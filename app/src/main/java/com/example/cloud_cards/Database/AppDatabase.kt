package com.example.cloud_cards.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloud_cards.DAO.CardDao
import com.example.cloud_cards.DAO.UserBooleanDao
import com.example.cloud_cards.DAO.UserDao
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Entities.UserBoolean

@Database(entities = [User::class, Card::class, UserBoolean::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun cardDao() : CardDao
    abstract fun userBooleanDao() : UserBooleanDao

    companion object {
        @Volatile private var INSTANCE : AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context,
                            AppDatabase::class.java, "qr-database.db")
                            .allowMainThreadQueries()
                            .build()

                    }
                }
            }
            return INSTANCE as AppDatabase
        }
    }
}