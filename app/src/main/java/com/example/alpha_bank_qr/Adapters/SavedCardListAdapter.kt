package com.example.alpha_bank_qr.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.alpha_bank_qr.Entities.SavedCard
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.Utils.DataUtils

class SavedCardListAdapter(private val context: Activity, private val savedCards: Array<SavedCard>)
    : ArrayAdapter<SavedCard>(context,
    R.layout.saved_card_list_item, savedCards
) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.saved_card_list_item, null, true)

        val photo = rowView.findViewById(R.id.photo) as ImageView
        val id = rowView.findViewById(R.id.id) as TextView
        val name = rowView.findViewById(R.id.name) as TextView
        val jobTitle = rowView.findViewById(R.id.job_title) as TextView
        val company = rowView.findViewById(R.id.company) as TextView

        photo.setImageDrawable(savedCards[position].photo)
        id.text = savedCards[position].id.toString()
        name.text = savedCards[position].name
        jobTitle.text = savedCards[position].jobTitle
        company.text = savedCards[position].company

        return rowView
    }

    companion object {
        fun setSavedCardsToView(context: Activity) : ArrayList<SavedCard> {
            val dbHelper = QRDatabaseHelper(context)
            val cursor = dbHelper.getScannedUsers()
            val cards = ArrayList<SavedCard>()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val name = cursor.getString(cursor.getColumnIndex("name")) + " " + cursor.getString(cursor.getColumnIndex("surname"))
                    val jobTitle = cursor.getString(cursor.getColumnIndex("job_title"))
                    val company = cursor.getString(cursor.getColumnIndex("company"))

                    cards.add(SavedCard(id, DataUtils.getImageInDrawable(cursor), name, jobTitle, company))
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return cards
        }
    }
}