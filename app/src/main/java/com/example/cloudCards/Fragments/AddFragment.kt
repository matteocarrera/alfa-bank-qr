package com.example.cloudCards.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cloudCards.Adapters.DataListAdapter
import com.example.cloudCards.Database.AppDatabase
import com.example.cloudCards.Database.FirestoreInstance
import com.example.cloudCards.Entities.CardInfo
import com.example.cloudCards.Entities.DataItem
import com.example.cloudCards.Entities.UserBoolean
import com.example.cloudCards.R
import com.example.cloudCards.Utils.DataUtils
import com.example.cloudCards.Utils.DataUtils.Companion.getUserFromTemplate
import com.example.cloudCards.Utils.DataUtils.Companion.userToMap
import com.example.cloudCards.Utils.ListUtils
import com.example.cloudCards.Utils.ProgramUtils
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

        val cardColor = ContextCompat.getColor(view.context, R.color.colorPrimary)
        view.card_color.setOnClickListener {
            setColorPicker(this, view)
        }

        setToolbar(this, view, cardColor)

        selectedItems.clear()

        setDataToListView(this)
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

    companion object {
        private fun setToolbar(addFragment: AddFragment, view: View, cardColor: Int) {
            addFragment.toolbar_add.setOnMenuItemClickListener {
                if (it.itemId == R.id.done) {
                    val title = view.card_title.text.toString()
                    val cardTitles = addFragment.db.cardInfoDao().getAllCardsNames()
                    when {
                        title.length > addFragment.MAX_CARD_TITLE_LENGTH -> {
                            Toast.makeText(
                                view.context,
                                "Название слишком длинное!",
                                Toast.LENGTH_SHORT
                            )
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
                            val user = createTemplate(addFragment)
                            val card = CardInfo(
                                id = user.uuid,
                                color = cardColor,
                                title = title.trimStart().trimEnd(),
                                cardId = user.uuid
                            )
                            addFragment.db.cardInfoDao().insertCard(card)
                            Toast.makeText(
                                view.context,
                                "Шаблон успешно создан!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            addFragment.parentFragmentManager.popBackStack()
                        }
                    }
                }
                true
            }
        }

        // https://androidexample365.com/a-simple-color-picker-library-for-android/
        private fun setColorPicker(addFragment: AddFragment, view: View) {
            val colorPicker = ColorPicker(addFragment.activity)
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

        private fun setDataToListView(addFragment: AddFragment) {
            val user = addFragment.db.userDao().getOwnerUser()
            val data = DataUtils.setUserData(user)

            val adapter = DataListAdapter(
                addFragment.requireActivity(),
                data,
                R.layout.data_list_checkbox_item
            )
            addFragment.data_list.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            addFragment.data_list.onItemClickListener = addFragment
            addFragment.data_list.adapter = adapter

            ListUtils.setDynamicHeight(addFragment.data_list)
        }

        private fun createTemplate(addFragment: AddFragment): UserBoolean {
            val newUser = DataUtils.parseDataToUserCard(addFragment.selectedItems)
            val userOwner = addFragment.db.userDao().getOwnerUser()
            val parentId = userOwner.uuid
            var uuid = UUID.randomUUID().toString()

            newUser.parentId = parentId
            newUser.uuid = uuid

            val myUserCards = addFragment.db.userBooleanDao().getTemplateUsers(parentId)
            var userExists = false
            if (myUserCards.isNotEmpty()) {
                myUserCards.forEach {
                    if (it.toString() == newUser.toString()) {
                        userExists = true
                        newUser.uuid = it.uuid
                        uuid = it.uuid
                    }
                }
            }
            if (!userExists) {
                val databaseRef = FirestoreInstance.getInstance().collection("users")
                databaseRef.document(parentId).collection("cards").document(newUser.uuid).set(
                    userToMap(getUserFromTemplate(userOwner, newUser))
                )

                addFragment.db.userBooleanDao().insertUserBoolean(newUser)
            }
            return newUser
        }

        private fun setShowQRDialog(addFragment: AddFragment, view: View, bitmap: Bitmap) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            val inflater = addFragment.requireActivity().layoutInflater
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

        private fun generateQR(addFragment: AddFragment, view: View) {
            try {
                if (addFragment.selectedItems.size == 0) {
                    Toast.makeText(view.context, "Не выбрано ни одного поля!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    val user = createTemplate(addFragment)

                    var bitmap =
                        QRCode.from(user.parentId + "|" + user.uuid).withCharset("utf-8")
                            .withSize(1000, 1000).bitmap()
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)

                    setShowQRDialog(addFragment, view, bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}