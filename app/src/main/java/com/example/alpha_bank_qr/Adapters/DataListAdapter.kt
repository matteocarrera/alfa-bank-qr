package com.example.alpha_bank_qr.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.R

class DataListAdapter(private val context: Activity, private val dataItems: ArrayList<DataItem>)
    : ArrayAdapter<DataItem>(context,
    R.layout.data_list_item, dataItems) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.data_list_item, null, true)

        val title = rowView.findViewById(R.id.title) as TextView
        val data = rowView.findViewById(R.id.description) as TextView

        title.text = dataItems[position].title
        data.text = dataItems[position].description

        return rowView
    }
}