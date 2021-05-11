package com.example.cloud_cards.Database

import android.content.Context
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.Entities.User

class DBService {
    companion object {
        fun addUser(context: Context, user: User) {
            val dbHelper = QRDatabaseHelper(context)
            dbHelper.addUser(user)
            dbHelper.close()
        }

        fun addCard(context: Context, card : Card) {
            val dbHelper = QRDatabaseHelper(context)
            dbHelper.addCard(card)
            dbHelper.close()
        }

        fun updateUser(context: Context, user: User) {
            val dbHelper = QRDatabaseHelper(context)
            dbHelper.updateUser(user)
            dbHelper.close()
        }

        fun updateUserPhoto(context: Context, id : Int, photoUUID : String) {
            val dbHelper = QRDatabaseHelper(context)
            dbHelper.updateUserPhoto(id, photoUUID)
            dbHelper.close()
        }

        fun getOwnerUser(context: Context) : User {
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getOwnerUserID()
            if (cursor!!.count != 0) { cursor.moveToFirst() }
            dbHelper.close()
            return getUserById(context, cursor.getInt(cursor.getColumnIndex("id")))
        }

        fun getUserById(context: Context, id : Int) : User {
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getUser(id)
            val user = User()
            if (cursor!!.count != 0) {
                cursor.moveToFirst()
                //user.id = cursor.getInt(cursor.getColumnIndex("id"))
                user.photo = cursor.getString(cursor.getColumnIndex("photo"))
                user.name = cursor.getString(cursor.getColumnIndex("name"))
                user.surname = cursor.getString(cursor.getColumnIndex("surname"))
                user.patronymic = cursor.getString(cursor.getColumnIndex("patronymic"))
                user.company = cursor.getString(cursor.getColumnIndex("company"))
                user.jobTitle = cursor.getString(cursor.getColumnIndex("job_title"))
                user.mobile = cursor.getString(cursor.getColumnIndex("mobile"))
                user.mobileSecond = cursor.getString(cursor.getColumnIndex("mobile_second"))
                user.email = cursor.getString(cursor.getColumnIndex("email"))
                user.emailSecond = cursor.getString(cursor.getColumnIndex("email_second"))
                user.address = cursor.getString(cursor.getColumnIndex("address"))
                user.addressSecond = cursor.getString(cursor.getColumnIndex("address_second"))
                //user.sberbank = cursor.getString(cursor.getColumnIndex("sberbank"))
                //user.vtb = cursor.getString(cursor.getColumnIndex("vtb"))
                //user.alfabank = cursor.getString(cursor.getColumnIndex("alfabank"))
                user.vk = cursor.getString(cursor.getColumnIndex("vk"))
                user.facebook = cursor.getString(cursor.getColumnIndex("facebook"))
                user.instagram = cursor.getString(cursor.getColumnIndex("instagram"))
                user.twitter = cursor.getString(cursor.getColumnIndex("twitter"))
                user.notes = cursor.getString(cursor.getColumnIndex("notes"))
            }
            dbHelper.close()
            return user
        }

        fun getLastUserFromDB(context: Context) : User {
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getLastUserID()
            if (cursor!!.count != 0) { cursor.moveToFirst() }
            dbHelper.close()
            return getUserById(context, cursor.getInt(cursor.getColumnIndex("id")))
        }

        fun getAllCardsNames(context: Context) : ArrayList<String> {
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getAllCardsNames()
            val cardsNames = ArrayList<String>()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    cardsNames.add(cursor.getString(cursor.getColumnIndex("title")))
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return cardsNames
        }

        fun getScannedUsers(context: Context) : ArrayList<User>? {
            val qrList = ArrayList<User>()
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getScannedUsers()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val user = getUserById(context, cursor.getInt(cursor.getColumnIndex("id")))
                    qrList.add(user)
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return qrList
        }

        fun getUsersFromMyCards(context: Context) : ArrayList<User> {
            val dbHelper =
                QRDatabaseHelper(context)
            val cursor = dbHelper.getUsersIDsFromMyCards()
            val users = ArrayList<User>()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val user = getUserById(context, cursor.getInt(cursor.getColumnIndex("id")))
                    users.add(user)
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return users
        }

        fun deleteUser(context: Context, id : Int) {
            val dbHelper = QRDatabaseHelper(context)
            dbHelper.deleteUser(id)
            dbHelper.close()
        }

        fun deleteCard(context: Context, id : Int) {
            val dbHelper = QRDatabaseHelper(context)
            dbHelper.deleteCard(id)
            dbHelper.close()
        }
    }
}