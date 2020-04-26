package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.MyCardListAdapter
import com.example.alpha_bank_qr.Adapters.SavedCardListAdapter
import com.example.alpha_bank_qr.Entities.Card
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.Utils.ListUtils
import kotlinx.android.synthetic.main.activity_cards.*

class CardsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)
        bottom_bar.menu.getItem(0).isChecked = true
        bottom_bar.setOnNavigationItemSelectedListener {
            val nextActivity =
                when (it.itemId) {
                    R.id.cards -> CardsActivity::class.java
                    R.id.scan -> ScanActivity::class.java
                    R.id.profile -> ProfileActivity::class.java
                    else -> {
                        Log.e("Error", "Activity set error")
                        null
                    }
                }
            val intent = Intent(this, nextActivity)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            true
        }

        saved_cards_list.visibility = View.GONE
        saved_cards_image.setImageResource(R.drawable.ic_expand)
        setVisibility(my_cards_title, my_cards_list, my_cards_image)
        setVisibility(saved_cards_title, saved_cards_list, saved_cards_image)

        /*val users = setUsers()
        val cards = setCards()

        val myCardsAdapter = MyCardListAdapter(this, cards)
        my_cards_list.adapter = myCardsAdapter
        val savedCardsAdapter = SavedCardListAdapter(this, users)
        saved_cards_list.adapter = savedCardsAdapter*/

        ListUtils.setDynamicHeight(my_cards_list);
        ListUtils.setDynamicHeight(saved_cards_list);
    }

    private fun setVisibility(layout: LinearLayout, list: ListView, image: ImageView) {
        layout.setOnClickListener {
            if (list.visibility == View.VISIBLE) {
                list.visibility = View.GONE
                image.setImageResource(R.drawable.ic_expand)
            } else {
                list.visibility = View.VISIBLE
                image.setImageResource(R.drawable.ic_hide)
            }
        }
    }
}


