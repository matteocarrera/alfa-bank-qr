package com.example.alpha_bank_qr.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha_bank_qr.Entities.User

class ContactsAdapter(private val list: List<User>)
    : RecyclerView.Adapter<ContactsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactsHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        val user : User = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = list.size

}