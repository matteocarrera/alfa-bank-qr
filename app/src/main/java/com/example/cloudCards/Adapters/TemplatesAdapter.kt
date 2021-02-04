package com.example.cloudCards.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudCards.Entities.CardInfo

class TemplatesAdapter(private val list: List<CardInfo>) : RecyclerView.Adapter<TemplatesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplatesHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TemplatesHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: TemplatesHolder, position: Int) {
        val cardInfo: CardInfo = list[position]
        holder.bind(cardInfo)
    }

    override fun getItemCount(): Int = list.size

}