package com.example.alpha_bank_qr.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.alpha_bank_qr.Database.QRDatabaseHelper
import com.example.alpha_bank_qr.Entities.SavedCard
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.ImageUtils

class SavedCardListAdapter(private val context: Activity, private val savedCards: Array<SavedCard>, private val layout: Int)
    : ArrayAdapter<SavedCard>(context,
    R.layout.saved_card_list_item, savedCards
) {

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(layout, null, true)

        val photo = rowView.findViewById(R.id.photo) as ImageView
        val letters = rowView.findViewById(R.id.letters) as TextView
        val circle = rowView.findViewById(R.id.circle) as LinearLayout
        val id = rowView.findViewById(R.id.contact_id) as TextView
        val name = rowView.findViewById(R.id.name) as TextView
        val jobTitle = rowView.findViewById(R.id.job_title) as TextView
        val company = rowView.findViewById(R.id.company) as TextView

        if (savedCards[position].photo == "") {
            photo.visibility = View.GONE
            letters.visibility = View.VISIBLE
            circle.visibility = View.VISIBLE
            letters.text = savedCards[position].name.take(1) + savedCards[position].surname.take(1)
        } else {
            photo.visibility = View.VISIBLE
            letters.visibility = View.GONE
            circle.visibility = View.GONE
            ImageUtils.getImageFromFirebase(savedCards[position].photo, photo)
        }

        id.text = savedCards[position].id.toString()
        name.text = savedCards[position].name + " " + savedCards[position].surname
        jobTitle.text = savedCards[position].jobTitle
        if (jobTitle.text == "") jobTitle.text = "Должность не указана"
        company.text = savedCards[position].company
        if (company.text == "") company.text = "Компания не указана"

        return rowView
    }

    companion object {
        fun setSavedCardsToView(context: Activity) : ArrayList<SavedCard> {
            val dbHelper =
                QRDatabaseHelper(context)
            val cursor = dbHelper.getScannedUsers()
            val cards = ArrayList<SavedCard>()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val photoUUID = cursor.getString(cursor.getColumnIndex("photo"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val surname = cursor.getString(cursor.getColumnIndex("surname"))
                    val jobTitle = cursor.getString(cursor.getColumnIndex("job_title"))
                    val company = cursor.getString(cursor.getColumnIndex("company"))

                    cards.add(SavedCard(id, photoUUID, name, surname, jobTitle, company))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return cards
        }
    }
}