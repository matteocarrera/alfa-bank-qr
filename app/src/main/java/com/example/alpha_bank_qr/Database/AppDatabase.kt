package com.example.alpha_bank_qr.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.alpha_bank_qr.DAO.CardDao
import com.example.alpha_bank_qr.DAO.ScannedUuidDao
import com.example.alpha_bank_qr.DAO.UserDao
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.ScannedUUID
import com.example.alpha_bank_qr.Entities.User

@Database(entities = [User::class, Card::class, ScannedUUID::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun cardDao() : CardDao
    abstract fun scannedUuidDao() : ScannedUuidDao

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