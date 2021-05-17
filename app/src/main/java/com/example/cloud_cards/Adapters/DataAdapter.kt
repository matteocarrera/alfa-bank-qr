package com.example.cloud_cards.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.R

class DataAdapter(private val dataSet: ArrayList<*>, val checkboxVisibility: Int, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.data_list_item, dataSet) {

    private class ViewHolder {
        lateinit var titleView: TextView
        lateinit var dataView: TextView
        lateinit var checkBox: CheckBox
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getItem(position: Int): DataItem {
        return dataSet[position] as DataItem
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(parent.context).inflate(R.layout.data_list_item, parent, false)
            viewHolder.titleView = convertView.findViewById(R.id.title)
            viewHolder.dataView = convertView.findViewById(R.id.data)
            viewHolder.checkBox = convertView.findViewById(R.id.checkbox)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataItem = getItem(position)
        viewHolder.titleView.text = item.title
        viewHolder.dataView.text = item.data
        viewHolder.checkBox.isChecked = item.checked
        viewHolder.checkBox.visibility = checkboxVisibility
        return result
    }
}