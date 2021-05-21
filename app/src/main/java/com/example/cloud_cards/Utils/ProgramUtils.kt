package com.example.cloud_cards.Utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import kotlinx.android.synthetic.main.activity_qr.view.*
import net.glxn.qrgen.android.QRCode
import java.io.ByteArrayOutputStream

class ProgramUtils {
    companion object {
        fun setQRWindow(context: Context?, link: String) {
            val alert = AlertDialog.Builder(context)
            val factory = LayoutInflater.from(context)
            val view: View = factory.inflate(R.layout.activity_qr, null)

            var bitmap = QRCode.from(link).withCharset("utf-8").withSize(1000, 1000).bitmap()
            bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
            view.qr_img.setImageBitmap(bitmap)

            alert.setView(view)
            alert.setPositiveButton("Готово") { dialog, _ ->
                dialog.cancel()
            }
            alert.setNeutralButton("Поделиться") { _, _ ->
                showShareIntent(context!!, link)
            }

            alert.show()
        }

        fun showShareIntent(context: Context, link: String) {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TITLE, "Визитка CloudCards")
            i.putExtra(Intent.EXTRA_TEXT, "Пользователь CloudCards отправил Вам визитку: $link")
            context.startActivity(Intent.createChooser(i, "Поделиться контактом"))
        }

        fun makeCall(activity: Activity, context: Context, number : String) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else context.startActivity(intent)
        }

        fun makeEmail(context: Context, email : String) {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            context.startActivity(intent)
        }

        fun openMap(context : Context, location : String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$location"))
            context.startActivity(intent)
        }

        fun openWebsite(context: Context, item: DataItem) {
            var website = ""
            when (item.title) {
                "vk" -> website = "vk.com/"
                "facebook" -> website = "facebook.com/"
                "instagram" -> website = "instagram.com/"
                "twitter" -> website = "twitter.com/"
                else -> {}
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www." + website + item.data))
            context.startActivity(intent)
        }

        fun exportContact(context: Context, user: User, drawable: Drawable) {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION)
            intent.type = ContactsContract.RawContacts.CONTENT_TYPE

            // Устанавливаем все поля для контакта (без дополнительного адреса)
            intent
                .putExtra(ContactsContract.Intents.Insert.NAME, user.name + " " + user.patronymic + " " + user.surname)
                .putExtra(ContactsContract.Intents.Insert.COMPANY, user.company)
                .putExtra(ContactsContract.Intents.Insert.JOB_TITLE, user.jobTitle)
                .putExtra(ContactsContract.Intents.Insert.PHONE, user.mobile)
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE)
                .putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, user.mobileSecond)
                .putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, user.email)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, user.emailSecond)
                .putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                .putExtra(ContactsContract.Intents.Insert.POSTAL, user.address)
                .putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.NOTES, user.notes)

            // Устанавливаем социальные сети, при их наличии
            val fields = arrayOf(
                DataItem("vk.com/", user.vk),
                DataItem("facebook.com/", user.facebook),
                DataItem("instagram.com/", user.instagram),
                DataItem("twitter.com/", user.twitter)
            )

            val data = ArrayList<ContentValues>()
            var row: ContentValues

            for (i in fields.indices) {
                if (fields[i].data.isNotEmpty()) {
                    row = ContentValues()
                    row.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                    )
                    row.put(ContactsContract.CommonDataKinds.Website.URL, fields[i].title + fields[i].data)
                    row.put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_PROFILE)
                    data.add(row)
                }
            }

            // Устанавливаем фотографию контакту при ее наличии, размер меньше 2^4
            if (user.photo.isNotEmpty()) {
                val imageAsBitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                imageAsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val imageData = stream.toByteArray()
                if (imageData.size > 262144) {
                    val alert = AlertDialog.Builder(context)
                    alert.setTitle("Предупреждение")
                    alert.setMessage("Ошибка при экспорте фотографии контакта! Все данные будут сохранены без фотографии.")
                    alert.setPositiveButton("Продолжить") { _, _ ->
                        intent.putExtra(ContactsContract.Intents.Insert.DATA, data)
                        context.startActivity(intent)
                    }
                    alert.setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alert.show()
                    return
                }

                row = ContentValues()
                row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, imageData)
                data.add(row)
            }

            intent.putExtra(ContactsContract.Intents.Insert.DATA, data)
            context.startActivity(intent)
        }
    }
}