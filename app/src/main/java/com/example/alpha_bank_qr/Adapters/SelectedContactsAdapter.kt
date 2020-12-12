package com.example.alpha_bank_qr.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha_bank_qr.Entities.User

class SelectedContactsAdapter(private val lists: List<User>) :
    RecyclerView.Adapter<SelectedContactsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedContactsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SelectedContactsHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: SelectedContactsHolder, position: Int) {
        val data: User = lists[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = lists.size

}