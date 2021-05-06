package com.example.cloud_cards.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Entities.Card

class TemplatesAdapter(private val list: List<Card>)
    : RecyclerView.Adapter<TemplatesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplatesHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TemplatesHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: TemplatesHolder, position: Int) {
        val card : Card = list[position]
        holder.bind(card)
    }

    override fun getItemCount(): Int = list.size

}