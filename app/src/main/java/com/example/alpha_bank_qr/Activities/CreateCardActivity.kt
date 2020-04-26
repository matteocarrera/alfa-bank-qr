package com.example.alpha_bank_qr.Activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.UserParser
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.android.synthetic.main.data_list_checkbox_item.view.*

class CreateCardActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)

        back.setOnClickListener {
            finish()
        }

        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()
        if (cursor!!.count != 0) {
            cursor.moveToFirst()

            val data = UserParser.setUserData(cursor)

            val adapter = DataListAdapter(this, data, R.layout.data_list_checkbox_item)
            data_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            data_list.onItemClickListener = this
            data_list.adapter = adapter
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val item : DataItem = p0?.getItemAtPosition(p2) as DataItem
        if (p1 != null) {
            p1.checkbox.isChecked = !p1.checkbox.isChecked
        }
    }
}
