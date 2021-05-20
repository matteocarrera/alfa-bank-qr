package com.example.cloud_cards.Activities

import android.app.AlertDialog
import android.content.Intent
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
import com.example.cloud_cards.Entities.*
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ColorUtils
import com.example.cloud_cards.Utils.DataUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_template_card.*

class TemplateCardActivity: AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var uuid: String
    private var businessCardCompany: Company? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_card)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_template_card)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Получаем данные, отправленные как Extra для загрузки данных
        uuid = intent.getStringExtra("uuid")!!
    }

    override fun onResume() {
        super.onResume()

        db = AppDatabase.getInstance(this)
        val card = db.cardDao().getCardById(uuid)

        // Устанавливаем меню для работы с шаблонной визитки
        val popupMenu = getTemplateCardMenu(card)
        card_title_view.setOnClickListener {
            popupMenu.show()
        }

        // Устанавливаем параметры в View
        card_color.setCardBackgroundColor(Color.parseColor(card.color))
        card_title.text = card.title

        // Получаем данные самой визитки
        val idPair = db.idPairDao().getIdPairById(card.cardUuid)
        val ownerUser = db.userDao().getOwnerUser()!!

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(idPair.parentUuid)
            .collection("cards")
            .document(idPair.uuid)
            .get()
            .addOnSuccessListener { document ->
                val cardTypeRaw = document.data?.get("type") as? String
                val cardType = if (cardTypeRaw != null) CardType.valueOf(cardTypeRaw) else null
                val currentUser: User
                val businessCard: BusinessCard<*>
                val data: ArrayList<DataItem>
                when (cardType) {
                    CardType.personal -> {
                        businessCard = Gson().fromJson(Gson().toJson(document.data).toString(), BusinessCard::class.java)
                        val businessCardUser = Gson().fromJson(Gson().toJson(businessCard.data).toString(), UserBoolean::class.java)
                        currentUser = DataUtils.getUserFromTemplate(ownerUser, businessCardUser)
                        data = DataUtils.setUserData(currentUser)
                    }
                    CardType.company -> {
                        businessCard = Gson().fromJson(Gson().toJson(document.data).toString(), BusinessCard::class.java)
                        businessCardCompany = Gson().fromJson(Gson().toJson(businessCard.data).toString(), Company::class.java)
                        data = DataUtils.setCompanyData(businessCardCompany!!)
                    }
                    else -> {
                        val businessCardData = Gson().fromJson(Gson().toJson(document.data).toString(), UserBoolean::class.java)
                        currentUser = DataUtils.getUserFromTemplate(ownerUser, businessCardData)
                        data = DataUtils.setUserData(currentUser)
                    }
                }

                val adapter = DataAdapter(data, View.GONE, this)
                data_list.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
    }

    private fun getTemplateCardMenu(card: Card): PopupMenu {
        val popupMenu = PopupMenu(this, card_title_view, Gravity.END)
        popupMenu.inflate(R.menu.template_card_menu)

        // Устанавливаем красный цвет текста для последней кнопки меню "Удалить"
        val s = SpannableString(getString(R.string.delete))
        s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
        popupMenu.menu.getItem(3).title = s

        // В зависимости от типа визитки скрываем те или иные пункты меню
        if (card.type == CardType.personal) {
            popupMenu.menu.getItem(1).isVisible = false
        } else {
            popupMenu.menu.getItem(0).isVisible = false
            popupMenu.menu.getItem(2).isVisible = false
        }

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
                R.id.edit_card -> {
                    val intent = Intent(this, CreateCompanyCardActivity::class.java)
                    intent.putExtra("card", card)
                    intent.putExtra("company", businessCardCompany)
                    startActivity(intent)
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