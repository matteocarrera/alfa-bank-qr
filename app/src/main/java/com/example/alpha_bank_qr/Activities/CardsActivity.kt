package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.MyCardListAdapter
import com.example.alpha_bank_qr.Adapters.SavedCardListAdapter
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.SavedCard
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.Json
import com.example.alpha_bank_qr.Utils.ListUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import kotlinx.android.synthetic.main.activity_cards.*
import kotlinx.android.synthetic.main.selected_saved_card_list_item.view.*
import net.glxn.qrgen.android.QRCode

class CardsActivity : AppCompatActivity(){

    private val selectedItems = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        val bundle : Bundle? = intent.extras
        try {
            val success = bundle!!.getBoolean("success")
            val fail = bundle.getBoolean("fail")

            if (success) Toast.makeText(this, "Визитная карточка успешно добавлена!", Toast.LENGTH_LONG).show()
            if (fail) Toast.makeText(this, "Такая визитная карточка уже существует!", Toast.LENGTH_LONG).show()
        } catch (e : Exception) {}

        bottom_bar.menu.getItem(0).isChecked = true
        bottom_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.cards -> goToActivity(CardsActivity::class.java)
                R.id.scan -> goToActivity(ScanActivity::class.java)
                else -> goToActivity(ProfileActivity::class.java)
            }
            true
        }

        selected_cards_share.setOnClickListener { shareCards() }

        selected_cards_delete.setOnClickListener { deleteCards() }

        settings.setOnClickListener { ProgramUtils.goToActivityAnimated(this, SettingsActivity::class.java) }

        add_card.setOnClickListener { ProgramUtils.goToActivityAnimated(this, CreateCardActivity::class.java) }

        select_cards.setOnClickListener {
            selectedItems.clear()
            setStandardToolbar(View.INVISIBLE)
            setSelectionToolbar(View.VISIBLE)
            setSavedCardsAdapter(true, R.layout.selected_saved_card_list_item)
        }

        cancel_selection.setOnClickListener {
            selectedItems.clear()
            setStandardToolbar(View.VISIBLE)
            setSelectionToolbar(View.INVISIBLE)
            setSavedCardsAdapter(false, R.layout.saved_card_list_item)
        }

        // Получение QR-визитки в виде изображения вне приложения
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true)
            handleSendImage(intent)
        else if (intent.action == Intent.ACTION_SEND_MULTIPLE && intent.type?.startsWith("image/") == true)
            handleSendMultipleImages(intent)

        setMyCardsAdapter()
        setSavedCardsAdapter(false, R.layout.saved_card_list_item)

        ListUtils.setDynamicHeight(my_cards_list)
        ListUtils.setDynamicHeight(saved_cards_list)
    }

    private fun shareCards() {
        val dbHelper = QRDatabaseHelper(this)
        val qrList = ArrayList<Bitmap>()
        if (selectedItems.count() == 0) Toast.makeText(this, "Вы не выбрали ни одной визитки!", Toast.LENGTH_SHORT).show()
        else {
            selectedItems.forEach {
                val cursor = dbHelper.getQRFromUser(it)
                if (cursor!!.count != 0) {
                    cursor.moveToFirst()
                    val dr = DataUtils.getImageInDrawable(cursor, "qr")
                    var bitmap = (dr as BitmapDrawable).bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
                    qrList.add(bitmap)
                }
            }
            dbHelper.close()
            ProgramUtils.saveImage(this, qrList)
            cancel_selection.performClick()
        }
    }

    private fun deleteCards() {
        if (selectedItems.count() == 0) Toast.makeText(this, "Вы не выбрали ни одной визитки!", Toast.LENGTH_SHORT).show()
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Удаление визиток")
            builder.setMessage("Вы действительно хотите удалить выбранные визитки?")
            builder.setPositiveButton("Да"){ _, _ ->
                selectedItems.forEach {
                    val dbHelper = QRDatabaseHelper(this)
                    dbHelper.deleteUser(it)
                }
                cancel_selection.performClick()
                Toast.makeText(this, "Выбранные визитки успешно удалены!", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Нет"){ _, _ -> }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun setMyCardsAdapter() {
        val cards = MyCardListAdapter.setMyCardsToView(this)
        val myCardsAdapter = MyCardListAdapter(this, cards.toTypedArray())
        my_cards_list.adapter = myCardsAdapter
        countCheck(my_cards_list, my_cards_notification)
        my_cards_list.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView?.getItemAtPosition(i) as Card
            val intent = Intent(this, CardActivity::class.java)
            intent.putExtra("user_id", item.userId)
            intent.putExtra("card_id", item.id)
            startActivity(intent)
        }
    }

    // Устанавливаем адаптер относительно того, какое действие происходит
    private fun setSavedCardsAdapter(selection : Boolean, layout: Int) {
        val savedCards = SavedCardListAdapter.setSavedCardsToView(this)
        val savedCardsAdapter = SavedCardListAdapter(this, savedCards.toTypedArray(), layout)
        if (selection) {
            saved_cards_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            saved_cards_list.setOnItemClickListener { adapterView, view, i, _ ->
                if (view != null) {
                    val item = adapterView?.getItemAtPosition(i) as SavedCard
                    if (selectedItems.contains(item.id)) selectedItems.remove(item.id)
                    else selectedItems.add(item.id)
                    view.checkbox.isChecked = !view.checkbox.isChecked
                }
            }
        } else {
            saved_cards_list.setOnItemClickListener { adapterView, _, i, _ ->
                val item = adapterView?.getItemAtPosition(i) as SavedCard
                val intent = Intent(this, CardActivity::class.java)
                intent.putExtra("user_id", item.id)
                startActivity(intent)
            }
        }
        saved_cards_list.adapter = savedCardsAdapter
        countCheck(saved_cards_list, saved_cards_notification)
    }

    // Обработка полученного изображения вне приложения
    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val bitmap =
                MediaStore.Images.Media.getBitmap(this.contentResolver, it)
            decodeQRFromImage(bitmap)
        }
    }

    // Обработка нескольких изображений
    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let { arrayList ->
            arrayList.forEach {
                val uri = it as? Uri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                decodeQRFromImage(bitmap)
            }
        }
    }

    // Получение данных с QR-визитки (фотография)
    private fun decodeQRFromImage(bitmap: Bitmap) {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val bMap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = MultiFormatReader()
        val result = reader.decode(bMap)
        addUserFromQR(result.text)
    }

    // Добавляем пользователя как визитку, считанную с QR изображения вне приложения
    private fun addUserFromQR(result : String) {
        try {
            val user = Json.fromJson(result)
            val bitmap = QRCode.from(result).withCharset("utf-8").withSize(1000, 1000).bitmap()
            user.qr = DataUtils.getImageInByteArray(bitmap)

            // Проверяем по QR коду, есть ли такая визитка с человеком уже в списке или нет
            val cardExists = QRDatabaseHelper.checkCardForExistence(this, user.qr!!)
            if (cardExists) Toast.makeText(this, "Такая визитная карточка уже существует!", Toast.LENGTH_LONG).show()
            else {
                val dbHelper = QRDatabaseHelper(this)
                dbHelper.addUser(user)
                dbHelper.close()
                Toast.makeText(this, "QR успешно считан!", Toast.LENGTH_LONG).show()
            }
        } catch (e : Exception) {
            Toast.makeText(this, "Ошибка считывания QR", Toast.LENGTH_LONG).show()
        }
    }

    private fun setStandardToolbar(visibility : Int) {
        settings.visibility = visibility
        cards_title.visibility = visibility
        select_cards.visibility = visibility
        add_card.visibility = visibility
    }

    private fun setSelectionToolbar(visibility: Int) {
        select_cards_title.visibility = visibility
        cancel_selection.visibility = visibility
        selected_cards_share.visibility = visibility
        selected_cards_delete.visibility = visibility
    }

    // Если список пуст, то устанавливаем соответствеющее уведомление
    private fun countCheck(list : ListView, notification : TextView) {
        if (list.count == 0) {
            list.visibility = View.GONE
            notification.visibility = View.VISIBLE
        }
        else {
            list.visibility = View.VISIBLE
            notification.visibility = View.GONE
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