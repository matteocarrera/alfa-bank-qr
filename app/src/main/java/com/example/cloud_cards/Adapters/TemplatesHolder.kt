package com.example.cloud_cards.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.R
import com.google.android.material.card.MaterialCardView

class TemplatesHolder (inflater : LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.my_card_list_item, parent, false)) {

    private var colorTag : MaterialCardView
    private var id : TextView
    private var title : TextView
    private var userId : TextView

    init {
        colorTag = itemView.findViewById(R.id.color_tag)
        id = itemView.findViewById(R.id.card_id)
        title = itemView.findViewById(R.id.title)
        userId = itemView.findViewById(R.id.user_id)
    }

    fun bind(card: Card) {
        colorTag.setBackgroundColor(card.color)
        id.text = card.id.toString()
        title.text = card.title
        userId.text = card.userId
    }

}