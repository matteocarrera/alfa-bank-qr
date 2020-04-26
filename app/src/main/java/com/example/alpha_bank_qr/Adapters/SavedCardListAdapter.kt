package com.example.alpha_bank_qr.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Entities.User

class SavedCardListAdapter(private val context: Activity, private val users: Array<User>)
    : ArrayAdapter<User>(context,
    R.layout.saved_card_list_item, users
) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.saved_card_list_item, null, true)

        //val photo = rowView.findViewById(R.id.photo) as ImageView
        val name = rowView.findViewById(R.id.name) as TextView
        val jobTitle = rowView.findViewById(R.id.job_title) as TextView
        val company = rowView.findViewById(R.id.company) as TextView

        //photo.setImageResource(users[position].photo)
        name.text = users[position].name + " " + users[position].surname
        jobTitle.text = users[position].jobTitle
        company.text = users[position].company

        return rowView
    }
}