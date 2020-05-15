package com.example.alpha_bank_qr

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.Drawable
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

    fun updateUserPhoto(id: Int, drawable: Drawable) {
        val cv = ContentValues()
        cv.put("photo", DataUtils.getImageInByteArray(drawable))
        val db = this.writableDatabase
        db.update("users", cv, "id = $id", null)
        db.close()
    }

    fun updateUser(user: User) {
        val values = ContentValues()
        values.put("photo", user.photo)
        values.put("qr", user.qr)
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
        values.put("sberbank", user.sberbank)
        values.put("vtb", user.vtb)
        values.put("alfabank", user.alfabank)
        values.put("vk", user.vk)
        values.put("facebook", user.facebook)
        values.put("instagram", user.instagram)
        values.put("twitter", user.twitter)
        values.put("notes", user.notes)
        val db = this.writableDatabase
        db.update("users", values, "id = ${user.id}", arrayOf())
        db.close()
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("photo", user.photo)
        values.put("qr", user.qr)
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
        values.put("sberbank", user.sberbank)
        values.put("vtb", user.vtb)
        values.put("alfabank", user.alfabank)
        values.put("vk", user.vk)
        values.put("facebook", user.facebook)
        values.put("instagram", user.instagram)
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

    fun deleteUser(id : Int) {
        val db = this.writableDatabase
        db.delete("users", "id = $id", null)
        db.close()
    }

    fun deleteCard(id : Int) {
        val db = this.writableDatabase
        db.delete("cards", "id = $id", null)
        db.close()
    }

    fun getOwnerUser(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE is_owner = 1", null)
    }

    fun getQRFromUser(id : Int) : Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT qr FROM users WHERE id = $id", null)
    }

    fun getScannedUsers(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE is_scanned = 1", null)
    }

    fun getAllUsersQR(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT qr FROM users", null)
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

    fun getAllCardsNames(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT title FROM cards", null)
    }

    companion object {
        // Функция для проверки по QR коду, существует ли такая визитка уже или еще нет
        fun checkCardForExistence(context: Context, qr : ByteArray) : Boolean {
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getAllUsersQR()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val qrFromDB = cursor.getBlob(cursor.getColumnIndex("qr"))
                    if (qrFromDB != null && qr.contentEquals(qrFromDB)) {
                        dbHelper.close()
                        return true
                    }
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return false
        }

        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "QRDatabase.db"

        private const val SQL_CREATE_USERS_TABLE =
            "CREATE TABLE users(" +
                    "id INTEGER PRIMARY KEY, " +
                    "photo BLOB," +
                    "qr BLOB," +
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
                    "sberbank TEXT," +
                    "vtb TEXT," +
                    "alfabank TEXT," +
                    "vk TEXT," +
                    "facebook TEXT," +
                    "instagram TEXT," +
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