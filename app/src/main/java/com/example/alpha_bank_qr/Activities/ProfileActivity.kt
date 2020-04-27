package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
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

        val user = User(
            0,
            DataUtils.getImageInByteArray(R.drawable.photo3, resources),
            1,
            0,
            "Николай",
            "Алексеев",
            "Сергеевич",
            "Альфа-Банк",
            "Руководящий отделом",
            "+79129995678",
            "",
            "nikolai@alfa-bank.ru",
            "",
            "Екатеринбург, ул. Пушкина 17",
            "",
            "",
            "",
            "nikolaialfa",
            ""
        )

        edit_profile.setOnClickListener {
            val dbHelper = QRDatabaseHelper(this)
            val cursor = dbHelper.getOwnerUser()
            if (cursor!!.count == 0) {
                dbHelper.addUser(user)

                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }

        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()
        if (cursor!!.count != 0) {
            cursor.moveToFirst()
            profile_name.text = DataUtils.setNameAndSurname(cursor)

            profile_photo.setImageDrawable(DataUtils.getImageInDrawable(cursor))

            val data = DataUtils.setUserData(cursor)

            val adapter = DataListAdapter(this, data, R.layout.data_list_item)
            data_list.adapter = adapter
        }

    }

}
