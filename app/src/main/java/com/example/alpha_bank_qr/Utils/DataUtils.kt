package com.example.alpha_bank_qr.Utils

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class DataUtils {
    companion object {
        private lateinit var mStorageRef: StorageReference
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
            DataItem("Сбербанк (расчетный счет)", "sberbank"),
            DataItem("ВТБ (расчетный счет)", "vtb"),
            DataItem("Альфа-Банк (расчетный счет)", "alfabank"),
            DataItem("vk", "vk"),
            DataItem("facebook", "facebook"),
            DataItem("instagram", "instagram"),
            DataItem("twitter", "twitter"),
            DataItem("заметки", "notes")
        )

        fun getImageFromFirebase(child: String, imageView: ImageView) {
            try {
                mStorageRef = FirebaseStorage.getInstance().reference
                mStorageRef.child(child).downloadUrl
                    .addOnSuccessListener {
                        val uri = it.toString().substring(0, it.toString().indexOf("&token"))
                        Picasso.get().load(uri).into(imageView)
                    }.addOnFailureListener { }
            } catch (e : Exception) {
                imageView.setImageDrawable(null)
            }
        }

        fun getImageInByteArray(drawable: Drawable?): ByteArray? {
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                return stream.toByteArray()
            }
            return null
        }

        fun getImageInByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun getImageInDrawable(cursor: Cursor, column : String): Drawable? {
            val blob = cursor.getBlob(cursor.getColumnIndex(column))
            if (blob != null)
                return BitmapDrawable(BitmapFactory.decodeByteArray(blob, 0, blob.size))
            return null
        }

        // Для отображения в профиле
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

        // Переводим выбранные данные в генераторе в пользователя для дальнейшего использования
        fun parseDataToUser(data : ArrayList<DataItem>, photoUUID : String) : User {
            val user = User()
            user.photo = photoUUID
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
                    "Сбербанк (расчетный счет)" -> user.sberbank = it.description
                    "ВТБ (расчетный счет)" -> user.vtb = it.description
                    "Альфа-Банк (расчетный счет)" -> user.alfabank = it.description
                    "vk" -> user.vk = it.description
                    "facebook" -> user.facebook = it.description
                    "instagram" -> user.instagram = it.description
                    "twitter" -> user.twitter = it.description
                    "заметки" -> user.notes = it.description
                }
            }
            return user
        }

        // Если какие-то данные пустые (отсутствуют), то мы не добавляем, иначе добавляем
        private fun addItem(title: String, description: String) {
            if (description.isNotEmpty()) data.add(DataItem(title, description))
        }
    }
}