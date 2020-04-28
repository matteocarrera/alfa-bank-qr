package com.example.alpha_bank_qr.Utils

import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User
import java.io.ByteArrayOutputStream

class DataUtils {
    companion object {
        private val data = ArrayList<DataItem>()
        private val titles = arrayOf(
            DataItem("фамилия", "surname"),
            DataItem("имя", "name"),
            DataItem("отчество", "patronymic"),
            DataItem("компания", "company"),
            DataItem("должность", "job_title"),
            DataItem("мобильный номер", "mobile"),
            DataItem("мобильный номер (другой)", "mobile_second"),
            DataItem("email", "email"),
            DataItem("email (другой)", "email_second"),
            DataItem("адрес", "address"),
            DataItem("адрес (другой)", "address_second"),
            DataItem("vk", "vk"),
            DataItem("facebook", "facebook"),
            DataItem("twitter", "twitter"),
            DataItem("заметки", "notes")
        )

        fun getImageInByteArray(image: Int, resources: Resources): ByteArray {
            val drawable = resources.getDrawable(image)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        private fun getImageInByteArray(drawable: Drawable): ByteArray {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun getImageInDrawable(cursor: Cursor): Drawable {
            val blob = cursor.getBlob(cursor.getColumnIndex("photo"))
            return BitmapDrawable(BitmapFactory.decodeByteArray(blob, 0, blob.size))
        }

        fun setNameAndSurname(cursor: Cursor): String {
            return cursor.getString(cursor.getColumnIndex("name")) +
                    " " +
                    cursor.getString(cursor.getColumnIndex("surname"))
        }

        fun setUserData(cursor: Cursor): ArrayList<DataItem> {
            data.clear()
            for (i in titles.indices) {
                addItem(
                    titles[i].title,
                    cursor.getString(cursor.getColumnIndex(titles[i].description))
                )
            }
            return data
        }

        fun parseDataToUser(data : ArrayList<DataItem>, drawable: Drawable) : User {
            val user = User(0,
                getImageInByteArray(
                    drawable
                ), 0, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
            data.forEach {
                when (it.title) {
                    "имя" -> user.name = it.description
                    "фамилия" -> user.surname = it.description
                    "отчество" -> user.patronymic = it.description
                    "компания" -> user.company = it.description
                    "должность" -> user.jobTitle = it.description
                    "мобильный номер" -> user.mobile = it.description
                    "мобильный номер (другой)" -> user.mobileSecond = it.description
                    "email" -> user.email = it.description
                    "email (другой)" -> user.emailSecond = it.description
                    "адрес" -> user.address = it.description
                    "адрес (другой)" -> user.addressSecond = it.description
                    "vk" -> user.vk = it.description
                    "facebook" -> user.facebook = it.description
                    "twitter" -> user.twitter = it.description
                    "notes" -> user.notes = it.description
                }
            }
            return user
        }

        private fun addItem(title: String, description: String) {
            if (description.isNotEmpty()) data.add(DataItem(title, description))
        }
    }
}