package com.example.cloud_cards.Activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.*
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ColorUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_company_card.*
import kotlinx.android.synthetic.main.activity_create_company_card.card_title
import java.util.*

class CreateCompanyCardActivity : AppCompatActivity() {

    private var cardColor = ColorUtils.getColorList()[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_company_card)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_create_company)
        toolbar.inflateMenu(R.menu.create_card_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.done -> {
                    saveCompanyBusinessCard()
                    Toast.makeText(this, "Визитка компании успешно создана!", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        card_color.setCardBackgroundColor(Color.parseColor(cardColor))
        card_color.setOnClickListener {
            cardColor =
                ColorUtils.getColorList()[(0 until ColorUtils.getColorList().count()).random()]
            card_color.setCardBackgroundColor(Color.parseColor(cardColor))
        }
    }

    private fun saveCompanyBusinessCard() {
        val db = AppDatabase.getInstance(this)
        val ownerUser = db.userDao().getOwnerUser()!!
        val company = Company(
            ownerUser.uuid,
            UUID.randomUUID().toString(),
            companyNameField.text.toString(),
            responsibleFullNameField.text.toString(),
            responsibleJobTitleField.text.toString(),
            companyAddressField.text.toString(),
            companyPhoneField.text.toString(),
            companyEmailField.text.toString(),
            companySiteField.text.toString()
        )
        val businessCard = BusinessCard(CardType.company, company)

        FirebaseFirestore.getInstance()
            .collection("users").document(company.parentUuid)
            .collection("cards").document(company.uuid)
            .set(businessCard)

        db.idPairDao().insertPair(IdPair(company.uuid, company.parentUuid))

        val card = Card(
            UUID.randomUUID().toString(),
            CardType.company,
            cardColor,
            card_title.text.toString(),
            company.uuid
        )
        db.cardDao().insertCard(card)

        finish()
    }
}