package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ListUtils
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.android.synthetic.main.data_list_checkbox_item.view.*
import yuku.ambilwarna.AmbilWarnaDialog

class CreateCardActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val selectedItems = ArrayList<DataItem>()
    private var mDefaultColor : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)

        back.setOnClickListener { goToActivity(CardsActivity::class.java) }

        setDataToListView()

        selectedItems.clear()

        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary)
        color_tag.setOnClickListener {
            val dialog = AmbilWarnaDialog(this, mDefaultColor, object:
                AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog:AmbilWarnaDialog, color:Int) {
                    mDefaultColor = color
                    color_tag.setBackgroundColor(mDefaultColor)
                }
                override fun onCancel(dialog:AmbilWarnaDialog) {
                    // cancel was selected by the user
                }
            })
            dialog.show()
        }

        save.setOnClickListener {
            if (card_title.text.toString() != "") {
                saveCardToDatabase()
                goToActivity(CardsActivity::class.java)
            }
            else
                Toast.makeText(this, "Введите название визитки!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val item = p0?.getItemAtPosition(p2) as DataItem
        if (selectedItems.contains(item)) selectedItems.remove(item)
        else selectedItems.add(item)
        if (p1 != null) {
            p1.checkbox.isChecked = !p1.checkbox.isChecked
        }
    }

    private fun setDataToListView() {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()
        if (cursor!!.count != 0) {
            cursor.moveToFirst()

            val data = DataUtils.setUserData(cursor)

            val adapter = DataListAdapter(this, data, R.layout.data_list_checkbox_item)
            data_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            data_list.onItemClickListener = this
            data_list.adapter = adapter

            ListUtils.setDynamicHeight(data_list);
        }
        dbHelper.close()
    }

    private fun saveCardToDatabase() {
        val dbHelper = QRDatabaseHelper(this)
        var cursor = dbHelper.getOwnerUser()
        cursor!!.moveToFirst()
        val drawable = DataUtils.getImageInDrawable(cursor)
        val user = DataUtils.parseDataToUser(selectedItems, drawable)
        dbHelper.addUser(user)
        cursor = dbHelper.getLastUserFromDb()
        if (cursor!!.count != 0){
            cursor.moveToFirst()
            val userId = cursor.getInt(cursor.getColumnIndex("id"))
            dbHelper.addCard(Card(0, mDefaultColor, card_title.text.toString(), userId))
        }
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
