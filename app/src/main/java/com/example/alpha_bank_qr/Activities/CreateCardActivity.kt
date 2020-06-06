package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Database.DBService
import com.example.alpha_bank_qr.Database.QRDatabaseHelper
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.Json
import com.example.alpha_bank_qr.Utils.ListUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.android.synthetic.main.activity_qr.view.*
import kotlinx.android.synthetic.main.data_list_checkbox_item.view.*
import net.glxn.qrgen.android.QRCode
import yuku.ambilwarna.AmbilWarnaDialog

class CreateCardActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val selectedItems = ArrayList<DataItem>()
    private var cardColor : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        back.setOnClickListener {
            finish()
        }

        setDataToListView()

        selectedItems.clear()

        cardColor = ContextCompat.getColor(this, R.color.colorPrimary)
        color_tag.setOnClickListener {
            val dialog = AmbilWarnaDialog(this, cardColor, object:
                AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog:AmbilWarnaDialog, color:Int) {
                    cardColor = color
                    color_tag.setBackgroundColor(cardColor)
                }
                override fun onCancel(dialog:AmbilWarnaDialog) { }
            })
            dialog.show()
        }

        generate.setOnClickListener {
            try {
                if (selectedItems.size == 0) {
                    Toast.makeText(this, "Не выбрано ни одного поля!", Toast.LENGTH_LONG).show()
                } else {
                    val ownerUser = DBService.getOwnerUser(this)
                    val newUser = DataUtils.parseDataToUser(selectedItems, ownerUser.photo)

                    var bitmap = QRCode.from(Json.toJson(newUser)).withCharset("utf-8").withSize(1000, 1000).bitmap()
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)

                    setQRWindow(bitmap)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

        save.setOnClickListener {
            when {
                card_title.text.toString() == "" -> {
                    Toast.makeText(this, "Введите название визитки!", Toast.LENGTH_LONG).show()
                }
                selectedItems.size == 0 -> {
                    Toast.makeText(this, "Не выбрано ни одного поля!", Toast.LENGTH_LONG).show()
                }
                else -> saveCardToDatabase()
            }
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

    private fun setQRWindow(bitmap: Bitmap) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_qr, null)

        val mBuilder = AlertDialog.Builder(this)
            .setTitle("Покажите QR код")
            .setView(mDialogView)

        val d: Drawable = BitmapDrawable(
            resources,
            Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
        )
        mDialogView.qr_img.setImageDrawable(d)
        val  mAlertDialog = mBuilder.show()

        //mDialogView.ok.setOnClickListener { mAlertDialog.dismiss() }

        //mDialogView.share.setOnClickListener {
         //   ProgramUtils.saveImage(this, arrayListOf(bitmap))
        //}
    }

    private fun setDataToListView() {
        val user = DBService.getOwnerUser(this)
        val data = DataUtils.setUserData(user)

        val adapter = DataListAdapter(this, data, R.layout.data_list_checkbox_item)
        data_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        data_list.onItemClickListener = this
        data_list.adapter = adapter

        ListUtils.setDynamicHeight(data_list)
    }

    private fun saveCardToDatabase() {
        val cardsNames = DBService.getAllCardsNames(this)
        if (cardsNames.contains(card_title.text.toString().trimStart().trimEnd()))
            Toast.makeText(this, "Визитка с таким именем уже существует!", Toast.LENGTH_SHORT).show()
        else {
            val ownerUser = DBService.getOwnerUser(this)
            var user = DataUtils.parseDataToUser(selectedItems, ownerUser.photo)
            DBService.addUser(this, user)
            user = DBService.getLastUserFromDB(this)
            val userCard = Card(0, cardColor, card_title.text.toString().trimStart().trimEnd(), user.id)
            DBService.addCard(this, userCard)
            goToActivity(CardsActivity::class.java)
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