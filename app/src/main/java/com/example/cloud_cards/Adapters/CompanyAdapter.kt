package com.example.cloud_cards.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Entities.Company
import com.example.cloud_cards.Fragments.ContactsFragment

class CompanyAdapter(private val list: List<Company>, private val fragment: ContactsFragment)
    : RecyclerView.Adapter<CompanyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CompanyHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CompanyHolder, position: Int) {
        val company: Company = list[position]
        holder.bind(company, fragment)
    }

    override fun getItemCount(): Int = list.size
}