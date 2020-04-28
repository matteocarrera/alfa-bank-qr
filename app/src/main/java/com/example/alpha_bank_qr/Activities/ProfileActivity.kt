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
            goToActivity(nextActivity!!)
            true
        }

        edit_profile.setOnClickListener {
            addUserToDatabase(setTestUser())
        }
      
        info.setOnClickListener {
            goToActivity(AboutAppActivity::class.java)
        }

        edit_profile.setOnClickListener { goToActivity(EditProfileActivity::class.java) }

        setDataToListview()
    }

    private fun setTestUser() : User {
        return User(
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
    }

    private fun addUserToDatabase(user : User) {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()
        if (cursor!!.count == 0) {
            dbHelper.addUser(user)

            goToActivity(ProfileActivity::class.java)
        }
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    private fun setDataToListview() {
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
