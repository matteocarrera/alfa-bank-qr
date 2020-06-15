package com.example.alpha_bank_qr.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.alpha_bank_qr.Database.QRDatabaseHelper
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.R

class MyCardListAdapter(private val context: Activity, private val cards: Array<Card>)
    : ArrayAdapter<Card>(context, R.layout.my_card_list_item, cards) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.my_card_list_item, null, true)

        val id = rowView.findViewById(R.id.card_id) as TextView
        val color = rowView.findViewById(R.id.color_tag) as TextView
        val userId = rowView.findViewById(R.id.user_id) as TextView
        val title = rowView.findViewById(R.id.title) as TextView

        id.text = cards[position].id.toString()
        color.setBackgroundColor(cards[position].color)
        userId.text = cards[position].userId.toString()
        title.text = cards[position].title

        return rowView
    }

    companion object {
        fun setMyCardsToView(context: Activity) : ArrayList<Card> {
            val dbHelper =
                QRDatabaseHelper(context)
            val cursor = dbHelper.getAllCards()
            val cards = ArrayList<Card>()
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val color = cursor.getInt(cursor.getColumnIndex("color"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val userId = cursor.getInt(cursor.getColumnIndex("user_id"))
                    TODO("Переделать вызов Card")
                    //cards.add(Card(color, title, userId))
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
            return cards
        }
    }
}