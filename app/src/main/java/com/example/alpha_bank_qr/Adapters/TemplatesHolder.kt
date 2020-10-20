package com.example.alpha_bank_qr.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.R
import com.google.android.material.card.MaterialCardView

class TemplatesHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.my_card_list_item, parent, false)) {

    private val colorTag: MaterialCardView = itemView.findViewById(R.id.color_tag)
    private val id: TextView = itemView.findViewById(R.id.card_id)
    private val title: TextView = itemView.findViewById(R.id.title)
    private val userId: TextView = itemView.findViewById(R.id.user_id)

    fun bind(card: Card) {
        colorTag.setBackgroundColor(card.color)
        id.text = card.id.toString()
        title.text = card.title
        userId.text = card.userId
    }

}