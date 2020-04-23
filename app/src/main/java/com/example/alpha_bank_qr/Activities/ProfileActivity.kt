package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bottom_bar.menu.getItem(2).isChecked = true
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

        profile_photo.setImageResource(R.drawable.photo3)
        profile_name.text = "Иван Иванов"

        val data = ArrayList<DataItem>()
        data.add(DataItem("email", "testmail@mail.ru"))
        data.add(DataItem("мобильный номер", "+79129096785"))
        data.add(DataItem("vk", "ivanivan"))

        val adapter = DataListAdapter(this, data)
        data_list.adapter = adapter
    }

}
