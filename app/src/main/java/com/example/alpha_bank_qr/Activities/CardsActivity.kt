package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.CardListAdapter
import com.example.alpha_bank_qr.R
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

        setVisibility(my_cards_title, my_cards_list, my_cards_image)
        setVisibility(saved_cards_title, saved_cards_list, saved_cards_image)

        val name = arrayOf("Николай", "Александр", "Анна", "Николай", "Александр", "Анна")
        val company = arrayOf("ПАО \"Газпром\"", "ОАО \"Альфа-Банк\"", "ОАО \"Лучший дизайн\"", "ПАО \"Газпром\"", "ОАО \"Альфа-Банк\"", "ОАО \"Лучший дизайн\"")
        val jobTitle = arrayOf("Инженер", "Битмейкер", "Генеральный директор", "Инженер", "Битмейкер", "Генеральный директор")

        val adapter = CardListAdapter(this, name, company, jobTitle)
        my_cards_list.adapter = adapter
        saved_cards_list.adapter = adapter

        ListUtils.setDynamicHeight(my_cards_list);
        ListUtils.setDynamicHeight(saved_cards_list);
    }

    private fun setVisibility(layout : LinearLayout, list : ListView, image : ImageView) {
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


