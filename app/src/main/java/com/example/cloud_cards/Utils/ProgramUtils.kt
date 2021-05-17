package com.example.cloud_cards.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import kotlinx.android.synthetic.main.activity_qr.view.*
import net.glxn.qrgen.android.QRCode
import java.io.File
import java.io.FileOutputStream

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
            alert.setPositiveButton("Готово") { dialog, id ->
                dialog.cancel()
            }
            alert.setNeutralButton("Поделиться") { dialog, id ->
                // TODO()
            }

            alert.show()
        }

        fun goToActivityAnimated(context: Context, cls : Class<*>) {
            val intent = Intent(context, cls)
            context.startActivity(intent)
        }

        fun setError(context: Context, text : String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
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

        @SuppressLint("SetWorldReadable")
        fun saveImage(context: Context, bitmaps: ArrayList<Bitmap>) {
            try {
                val imageUris = ArrayList<Uri>()
                bitmaps.forEach {
                    val file = File(context.externalCacheDir, "$it.png")
                    val fOut = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                    fOut.flush()
                    fOut.close()
                    file.setReadable(true, false)
                    val photoURI = FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName.toString() + ".provider",
                        file
                    )
                    imageUris.add(photoURI)
                }
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    putExtra(Intent.EXTRA_STREAM, imageUris)
                    type = "image/*"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться QR кодом с помощью"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun exportContact(user: User) : Intent {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION)
            intent.type = ContactsContract.RawContacts.CONTENT_TYPE

            // Не считан второй адрес и фото
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

            val fields = arrayOf(
                DataItem("vk.com/", user.vk),
                DataItem("facebook.com/", user.facebook),
                DataItem("instagram.com/", user.instagram),
                DataItem("twitter.com/", user.twitter)
            )

            val data = ArrayList<ContentValues>()

            for (i in fields.indices) {
                if (fields[i].data.isNotEmpty()) {
                    val row = ContentValues()
                    row.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                    )
                    row.put(ContactsContract.CommonDataKinds.Website.URL, fields[i].title + fields[i].data)
                    row.put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_PROFILE)
                    data.add(row)
                }
            }

            intent.putExtra(ContactsContract.Intents.Insert.DATA, data)

            return intent
        }
    }
}