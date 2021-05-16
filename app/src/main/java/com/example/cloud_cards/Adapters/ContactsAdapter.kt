package com.example.cloud_cards.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Fragments.ContactsFragment

class ContactsAdapter(private val list: List<User>, private val fragment: ContactsFragment)
    : RecyclerView.Adapter<ContactsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactsHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user, fragment)
    }

    override fun getItemCount(): Int = list.size
}