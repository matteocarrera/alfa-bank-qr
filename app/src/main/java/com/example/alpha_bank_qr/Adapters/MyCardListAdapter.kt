package com.example.alpha_bank_qr.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils

class MyCardListAdapter(private val context: Activity, private val cards: Array<Card>)
    : ArrayAdapter<Card>(context, R.layout.my_card_list_item, cards) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.my_card_list_item, null, true)

        val id = rowView.findViewById(R.id.id) as TextView
        val userId = rowView.findViewById(R.id.user_id) as TextView
        val photo = rowView.findViewById(R.id.photo) as ImageView
        val title = rowView.findViewById(R.id.title) as TextView

        id.text = cards[position].id.toString()
        userId.text = cards[position].userId.toString()
        photo.setImageDrawable(DataUtils.getImageInDrawable(cards[position].photo))
        title.text = cards[position].title

        return rowView
    }
}