package com.example.alpha_bank_qr.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ListUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_card.data_list
import kotlinx.android.synthetic.main.activity_qr.view.*
import kotlinx.android.synthetic.main.data_list_checkbox_item.view.*
import kotlinx.android.synthetic.main.dialog_save_card.view.*
import kotlinx.android.synthetic.main.fragment_add.*
import net.glxn.qrgen.android.QRCode
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.util.*
import kotlin.collections.ArrayList

class AddFragment : Fragment(), AdapterView.OnItemClickListener {

    private val MAX_CARD_TITLE_LENGTH = 30
    private val selectedItems = ArrayList<DataItem>()
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        setToolbar(view)

        selectedItems.clear()

        setDataToListView()
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        val item = adapterView?.getItemAtPosition(position) as DataItem
        if (selectedItems.contains(item)) selectedItems.remove(item)
        else selectedItems.add(item)
        if (view != null) {
            view.checkbox.isChecked = !view.checkbox.isChecked
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_card_menu, menu)
    }

    private fun setToolbar(view: View) {
        toolbar_add.setNavigationOnClickListener {
            setSaveDialog(view)
        }
        toolbar_add.setOnMenuItemClickListener {
            if (it.itemId == R.id.done) generateQR(view)
            true
        }
    }

    private fun setSaveDialog(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_save_card, null)
        val cardColor = ContextCompat.getColor(layout.context, R.color.colorPrimary)
        layout.card_color.setOnClickListener {
            setColorPicker(layout)
        }

        val dialog = builder.setView(layout)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            .show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val title = layout.card_title.text.toString()
            val cardTitles = db.cardDao().getAllCardsNames()
            when {
                title.length > MAX_CARD_TITLE_LENGTH -> {
                    Toast.makeText(view.context, "Название слишком длинное!", Toast.LENGTH_SHORT)
                        .show()
                }
                cardTitles.contains(title.trimStart().trimEnd()) -> {
                    Toast.makeText(
                        view.context,
                        "Шаблон с таким названием уже существует!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val user = createUser()
                    val card = Card(cardColor, title.trimStart().trimEnd(), user.uuid)
                    db.cardDao().insertCard(card)
                    Toast.makeText(view.context, "Шаблон успешно создан!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.cancel()
                }
            }
        }
    }

    private fun setShowQRDialog(view: View, bitmap: Bitmap) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.activity_qr, null)

        layout.qr_img.setImageBitmap(bitmap)
        val dialog = builder.setView(layout)
            .setNeutralButton("Закрыть") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Поделиться", null)
            .show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            ProgramUtils.saveImage(view.context, arrayListOf(bitmap))
        }
    }

    private fun generateQR(view: View) {
        try {
            if (selectedItems.size == 0) {
                Toast.makeText(view.context, "Не выбрано ни одного поля!", Toast.LENGTH_LONG).show()
            } else {
                val user = createUser()

                var bitmap =
                    QRCode.from(user.uuid).withCharset("utf-8").withSize(1000, 1000).bitmap()
                bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)

                setShowQRDialog(view, bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createUser(): User {
        db.userDao().getOwnerUser()
        val newUser = DataUtils.parseDataToUserCard(selectedItems)
        var uuid = UUID.randomUUID().toString()
        newUser.uuid = uuid

        val myCardsUsers = db.userBooleanDao().getTemplateUsers(uuid)
        var userExists = false
        if (myCardsUsers.isNotEmpty()) {
            myCardsUsers.forEach {
                if (it.toString() == newUser.toString()) {
                    userExists = true
                    newUser.uuid = it.uuid
                    uuid = it.uuid
                }
            }
        }
        if (!userExists) {
            val databaseRef = FirebaseDatabase.getInstance().getReference(uuid)
            databaseRef.setValue(Gson().toJson(newUser))

            db.userDao().insertUser(newUser)
        }
        return newUser
    }

    // https://androidexample365.com/a-simple-color-picker-library-for-android/
    private fun setColorPicker(view: View) {
        val colorPicker = ColorPicker(activity)
        colorPicker.setOnChooseColorListener(object : OnChooseColorListener {
            override fun onChooseColor(position: Int, color: Int) {}
            override fun onCancel() {}
        })
            .addListenerButton("ОТМЕНА") { _: View, _: Int, _: Int ->
                colorPicker.dismissDialog()
            }
            .addListenerButton("СОХРАНИТЬ") { _: View, _: Int, color: Int ->
                view.card_color.setBackgroundColor(color)
                colorPicker.dismissDialog()
            }
            .disableDefaultButtons(true)
            .setRoundColorButton(true)
            .setTitle("Выберите цвет")
            .show()
    }

    private fun setDataToListView() {
        val user = db.userDao().getOwnerUser()
        val data = DataUtils.setUserData(user)

        val adapter = DataListAdapter(requireActivity(), data, R.layout.data_list_checkbox_item)
        data_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        data_list.onItemClickListener = this
        data_list.adapter = adapter

        ListUtils.setDynamicHeight(data_list)
    }
}