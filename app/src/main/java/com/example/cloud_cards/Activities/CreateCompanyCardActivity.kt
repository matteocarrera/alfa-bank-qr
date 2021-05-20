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
import java.util.*

class CreateCompanyCardActivity : AppCompatActivity() {

    private var cardColor = ColorUtils.getColorList()[0]
    private var templateCompany: Company? = null
    private var templateCard: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_company_card)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_create_company)
        toolbar.inflateMenu(R.menu.create_card_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.done -> {
                    saveCompanyBusinessCard()
                }
            }
            true
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        templateCompany = intent.getSerializableExtra("company") as? Company
        templateCard = intent.getSerializableExtra("card") as? Card
        if (templateCompany != null && templateCard != null) {
            cardColor = templateCard!!.color
            card_title.setText(templateCard!!.title)
            setDataToField(templateCompany!!)
        }

        card_color.setCardBackgroundColor(Color.parseColor(cardColor))
        card_color.setOnClickListener {
            cardColor =
                ColorUtils.getColorList()[(0 until ColorUtils.getColorList().count()).random()]
            card_color.setCardBackgroundColor(Color.parseColor(cardColor))
        }
    }

    private fun setDataToField(company: Company) {
        companyNameField.setText(company.name)
        responsibleFullNameField.setText(company.responsibleFullName)
        responsibleJobTitleField.setText(company.responsibleJobTitle)
        companyAddressField.setText(company.address)
        companyPhoneField.setText(company.phone)
        companyEmailField.setText(company.email)
        companySiteField.setText(company.website)
    }

    private fun saveCompanyBusinessCard() {
        val db = AppDatabase.getInstance(this)
        val ownerUser = db.userDao().getOwnerUser()!!
        val company = Company(
            ownerUser.uuid,
            if (templateCompany != null) templateCompany!!.uuid else UUID.randomUUID().toString(),
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
            .collection("users")
            .document(company.parentUuid)
            .collection("cards")
            .document(company.uuid)
            .set(businessCard)

        val card = Card(
            UUID.randomUUID().toString(),
            CardType.company,
            cardColor,
            card_title.text.toString(),
            company.uuid
        )

        if (templateCard != null) {
            card.uuid = templateCard!!.uuid
            db.cardDao().updateCard(card)
            finish()
            Toast.makeText(this, "Визитка компании успешно обновлена!", Toast.LENGTH_SHORT).show()
            return
        }

        db.cardDao().insertCard(card)

        db.idPairDao().insertPair(IdPair(company.uuid, company.parentUuid))

        finish()
        Toast.makeText(this, "Визитка компании успешно создана!", Toast.LENGTH_SHORT).show()
    }
}