package com.example.cloudCards.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cloudCards.DAO.CardInfoDao
import com.example.cloudCards.DAO.UserBooleanDao
import com.example.cloudCards.DAO.UsersDao
import com.example.cloudCards.Entities.CardInfo
import com.example.cloudCards.Entities.User
import com.example.cloudCards.Entities.UserBoolean

@Database(entities = [User::class, UserBoolean::class, CardInfo::class], version = 1)
@TypeConverters(DataConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UsersDao
    abstract fun userBooleanDao(): UserBooleanDao
    abstract fun cardInfoDao(): CardInfoDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, "qr-database.db"
                        )
                            .allowMainThreadQueries()
                            .build()

                    }
                }
            }
            return INSTANCE as AppDatabase
        }
    }
}