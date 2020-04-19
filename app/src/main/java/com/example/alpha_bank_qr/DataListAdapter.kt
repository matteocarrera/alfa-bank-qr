package com.example.alpha_bank_qr

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class DataListAdapter(private val context: Activity, private val title: Array<String>, private val description: Array<String>)
    : ArrayAdapter<String>(context, R.layout.data_list_item, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.data_list_item, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val subtitleText = rowView.findViewById(R.id.description) as TextView

        titleText.text = title[position]
        subtitleText.text = description[position]

        return rowView
    }
}