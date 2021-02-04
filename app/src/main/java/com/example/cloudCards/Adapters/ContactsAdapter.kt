package com.example.cloudCards.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudCards.Entities.User

class ContactsAdapter(private val lists: List<User>) : RecyclerView.Adapter<ContactsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactsHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        val data: User = lists[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = lists.size

}