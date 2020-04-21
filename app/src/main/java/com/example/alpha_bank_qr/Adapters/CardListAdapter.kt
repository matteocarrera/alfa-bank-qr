package com.example.alpha_bank_qr.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.alpha_bank_qr.R


class CardListAdapter(private val context: Activity, private val name: Array<String>, private val jobTitle: Array<String>, private val company: Array<String>)
    : ArrayAdapter<String>(context,
    R.layout.card_list_item, name
) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.card_list_item, null, true)

        val nameText = rowView.findViewById(R.id.name) as TextView
        val jobTitleText = rowView.findViewById(R.id.job_title) as TextView
        val companyText = rowView.findViewById(R.id.company) as TextView

        nameText.text = name[position]
        jobTitleText.text = jobTitle[position]
        companyText.text = company[position]

        return rowView
    }
}