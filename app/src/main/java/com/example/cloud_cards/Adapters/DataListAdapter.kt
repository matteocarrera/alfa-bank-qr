package com.example.cloud_cards.Adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.R

class DataListAdapter(private val context: FragmentActivity, private val dataItems: ArrayList<DataItem>, private val layout: Int)
    : ArrayAdapter<DataItem>(context, layout, dataItems) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(layout, null, true)

        val title = rowView.findViewById(R.id.title) as TextView
        val data = rowView.findViewById(R.id.description) as TextView

        title.text = dataItems[position].title
        data.text = dataItems[position].data

        return rowView
    }
}