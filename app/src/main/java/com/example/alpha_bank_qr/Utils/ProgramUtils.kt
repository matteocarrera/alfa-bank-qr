package com.example.alpha_bank_qr.Utils

import android.content.ContentValues
import android.content.Intent
import android.provider.ContactsContract
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User

class ProgramUtils {
    companion object {
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
                DataItem("twitter.com/", user.twitter)
            )

            val data = ArrayList<ContentValues>()

            for (i in fields.indices) {
                if (fields[i].description.isNotEmpty()) {
                    val row = ContentValues()
                    row.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                    )
                    row.put(ContactsContract.CommonDataKinds.Website.URL, fields[i].title + fields[i].description)
                    row.put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_PROFILE)
                    data.add(row)
                }
            }

            intent.putExtra(ContactsContract.Intents.Insert.DATA, data)

            return intent
        }
    }
}