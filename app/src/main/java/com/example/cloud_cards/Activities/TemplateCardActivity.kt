package com.example.cloud_cards.Activities

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud_cards.Adapters.DataAdapter
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Entities.UserBoolean
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ColorUtils
import com.example.cloud_cards.Utils.DataUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_template_card.*

class TemplateCardActivity: AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var card: Card
    private lateinit var popupMenu: PopupMenu
    private var data = ArrayList<DataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_card)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_template_card)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Получаем данные, отправленные как Extra для загрузки данных
        card = intent.getSerializableExtra("card") as Card

        // Устанавливаем меню для работы с шаблонной визитки
        popupMenu = getTemplateCardMenu()
        card_title_view.setOnClickListener {
            popupMenu.show()
        }

        // Устанавливаем параметры в View
        card_color.setCardBackgroundColor(Color.parseColor(card.color))
        card_title.text = card.title

        // Получаем данные о шаблонной визитке
        db = AppDatabase.getInstance(this)
        val idPair = db.idPairDao().getIdPairById(card.cardUuid)

        FirebaseFirestore.getInstance()
            .collection("users").document(idPair.parentUuid)
            .collection("cards").document(idPair.uuid)
            .get().addOnSuccessListener { document ->
                val businessCardUser = Gson().fromJson(Gson().toJson(document.data).toString(), UserBoolean::class.java)
                FirebaseFirestore.getInstance()
                    .collection("users").document(idPair.parentUuid)
                    .collection("data").document(idPair.parentUuid)
                    .get().addOnSuccessListener { secondDocument ->
                        val mainUser = Gson().fromJson(Gson().toJson(secondDocument.data).toString(), User::class.java)
                        val currentUser = DataUtils.getUserFromTemplate(mainUser, businessCardUser)

                        data = DataUtils.setUserData(currentUser)

                        val adapter = DataAdapter(data, View.GONE, this)
                        data_list.adapter = adapter
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Error", "get failed with ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
    }

    private fun getTemplateCardMenu(): PopupMenu {
        val popupMenu = PopupMenu(this, card_title_view, Gravity.END)
        popupMenu.inflate(R.menu.template_card_menu)

        // Устанавливаем красный цвет текста для последней кнопки меню "Удалить"
        val s = SpannableString(getString(R.string.delete))
        s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
        popupMenu.menu.getItem(2).title = s

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.rename -> {
                    // Устанавливаем AlertDialog с дополнительным EditText внутри для изменения заголовка
                    val alert = AlertDialog.Builder(this)
                    alert.setMessage("Введите название визитки:")
                    alert.setTitle("Название визитки")

                    val container = LinearLayout(this)
                    container.orientation = LinearLayout.VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(55, 0, 55, 0)
                    val input = EditText(this)
                    input.layoutParams = lp
                    input.gravity = Gravity.TOP or Gravity.LEFT
                    input.inputType =
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    input.setLines(1)
                    input.maxLines = 1
                    input.setText(card.title)
                    container.addView(input, lp)

                    alert.setView(container)

                    alert.setPositiveButton("Сохранить") { _, _ ->
                        // Обновляем данные визитки
                        val newTitle = input.text.toString()
                        if (newTitle.isEmpty()) {
                            Toast.makeText(this, "Название не может быть пустым!", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        card.title = newTitle
                        AppDatabase.getInstance(this).cardDao().updateCard(card)
                        card_title.text = card.title
                    }

                    alert.setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                    }

                    alert.show()
                }
                R.id.change_color -> {
                    val cardColor = ColorUtils.getColorList()[(0 until ColorUtils.getColorList().count()).random()]
                    card_color.setCardBackgroundColor(Color.parseColor(cardColor))
                    card.color = cardColor
                    db.cardDao().updateCard(card)
                }
                R.id.delete -> {
                    db.cardDao().deleteCard(card)
                    finish()
                }
            }
            true
        }
        return popupMenu
    }
}