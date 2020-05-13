package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.example.alpha_bank_qr.Adapters.MyCardListAdapter
import com.example.alpha_bank_qr.Adapters.SavedCardListAdapter
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.Entities.SavedCard
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.ListUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import kotlinx.android.synthetic.main.activity_cards.*

class CardsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        val bundle : Bundle? = intent.extras
        try {
            val flag = bundle!!.getBoolean("scan")

            if (flag) Toast.makeText(this, "Визитная карточка успешно добавлена!", Toast.LENGTH_SHORT).show()
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

        add_card.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, CreateCardActivity::class.java)
            finish()
        }

        /*saved_cards_list.visibility = View.GONE
        saved_cards_image.setImageResource(R.drawable.ic_down)
        ListUtils.setVisibility(my_cards_title, my_cards_list, my_cards_image)
        ListUtils.setVisibility(saved_cards_title, saved_cards_list, saved_cards_image)*/

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

        val savedCards = SavedCardListAdapter.setSavedCardsToView(this)
        val savedCardsAdapter = SavedCardListAdapter(this, savedCards.toTypedArray())
        saved_cards_list.adapter = savedCardsAdapter
        countCheck(saved_cards_list, saved_cards_notification)
        saved_cards_list.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView?.getItemAtPosition(i) as SavedCard
            val intent = Intent(this, CardActivity::class.java)
            intent.putExtra("user_id", item.id)
            startActivity(intent)
        }

        ListUtils.setDynamicHeight(my_cards_list);
        ListUtils.setDynamicHeight(saved_cards_list);
    }

    private fun countCheck(list : ListView, notification : TextView) {
        if (list.count == 0) notification.visibility = View.VISIBLE
        else notification.visibility = View.GONE
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}