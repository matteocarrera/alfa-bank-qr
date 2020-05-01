package com.example.alpha_bank_qr

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.BitmapDrawable
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.Utils.DataUtils

class QRDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USERS_TABLE)
        db.execSQL(SQL_CREATE_CARDS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_USERS_TABLE)
        db.execSQL(SQL_DELETE_CARDS_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun updateUser(user: User) {
        val values = ContentValues()
        values.put("photo", DataUtils.getImageInByteArray(user.photo))
        values.put("is_owner", user.isOwner)
        values.put("is_scanned", user.isScanned)
        values.put("name", user.name)
        values.put("surname", user.surname)
        values.put("patronymic", user.patronymic)
        values.put("company", user.company)
        values.put("job_title", user.jobTitle)
        values.put("mobile", user.mobile)
        values.put("mobile_second", user.mobileSecond)
        values.put("email", user.email)
        values.put("email_second", user.emailSecond)
        values.put("address", user.address)
        values.put("address_second", user.addressSecond)
        values.put("vk", user.vk)
        values.put("facebook", user.facebook)
        values.put("twitter", user.twitter)
        values.put("notes", user.notes)
        val db = this.writableDatabase
        db.update("users", values, "id = ${user.id}", arrayOf())
        db.close()
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("photo", DataUtils.getImageInByteArray(user.photo))
        values.put("is_owner", user.isOwner)
        values.put("is_scanned", user.isScanned)
        values.put("name", user.name)
        values.put("surname", user.surname)
        values.put("patronymic", user.patronymic)
        values.put("company", user.company)
        values.put("job_title", user.jobTitle)
        values.put("mobile", user.mobile)
        values.put("mobile_second", user.mobileSecond)
        values.put("email", user.email)
        values.put("email_second", user.emailSecond)
        values.put("address", user.address)
        values.put("address_second", user.addressSecond)
        values.put("vk", user.vk)
        values.put("facebook", user.facebook)
        values.put("twitter", user.twitter)
        values.put("notes", user.notes)
        val db = this.writableDatabase
        db.insert("users", null, values)
        db.close()
    }

    fun addCard(card: Card) {
        val values = ContentValues()
        values.put("color", card.color)
        values.put("title", card.title)
        values.put("user_id", card.userId)
        val db = this.writableDatabase
        db.insert("cards", null, values)
        db.close()
    }

    fun deleteUser(user: User) {
        val db = this.writableDatabase
        db.delete("users", "id = ${user.id}", null)
        db.close()
    }

    fun getOwnerUser(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE is_owner = 1", null)
    }

    fun getScannedUsers(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE is_scanned = 1", null)
    }

    fun getUser(id : Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE id = $id", null)
    }

    fun getLastUserFromDb() : Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE id = (SELECT MAX(id) FROM users)", null)
    }

    fun getAllCards(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM cards", null)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "QRDatabase.db"

        private const val SQL_CREATE_USERS_TABLE =
            "CREATE TABLE users(" +
                    "id INTEGER PRIMARY KEY, " +
                    "photo BLOB," +
                    "is_owner INTEGER," +
                    "is_scanned INTEGER," +
                    "name TEXT," +
                    "surname TEXT," +
                    "patronymic TEXT," +
                    "company TEXT," +
                    "job_title TEXT," +
                    "mobile TEXT," +
                    "mobile_second TEXT," +
                    "email TEXT," +
                    "email_second TEXT," +
                    "address TEXT," +
                    "address_second TEXT," +
                    "vk TEXT," +
                    "facebook TEXT," +
                    "twitter TEXT," +
                    "notes TEXT" +
                    ")"

        private const val SQL_CREATE_CARDS_TABLE =
            "CREATE TABLE cards(" +
                    "id INTEGER PRIMARY KEY," +
                    "color INTEGER," +
                    "title TEXT," +
                    "user_id INTEGER" +
                    ")"

        private const val SQL_DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS users"

        private const val SQL_DELETE_CARDS_TABLE =
            "DROP TABLE IF EXISTS cards"
    }
}