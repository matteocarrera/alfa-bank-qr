package com.example.alpha_bank_qr.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.ImageUtils
import kotlinx.android.synthetic.main.saved_card_list_item.view.*

class SelectedContactsHolder (inflater : LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.selected_saved_card_list_item, parent, false)) {

    private var photo : ImageView
    private var id : TextView
    private var name : TextView
    private var jobTitle : TextView
    private var company : TextView

    init {
        photo = itemView.findViewById(R.id.photo)
        id = itemView.findViewById(R.id.contact_id)
        name = itemView.findViewById(R.id.name)
        jobTitle = itemView.findViewById(R.id.job_title)
        company = itemView.findViewById(R.id.company)
    }

    fun bind(user: User) {
        if (user.photo == "") {
            photo.visibility = View.GONE
            itemView.letters.visibility = View.VISIBLE
            itemView.circle.visibility = View.VISIBLE
            itemView.letters.text = user.name.take(1) + user.surname.take(1)
        } else {
            photo.visibility = View.VISIBLE
            itemView.letters.visibility = View.GONE
            itemView.circle.visibility = View.GONE
            ImageUtils.getImageFromFirebase(user.photo, photo)
        }
        id.text = user.id
        name.text = user.name + " " + user.surname
        if (user.jobTitle.isNotEmpty()) jobTitle.text = user.jobTitle
        else jobTitle.text = "Должность не указана"
        if (user.company.isNotEmpty()) company.text = user.company
        else company.text = "Компания не указана"
    }

}