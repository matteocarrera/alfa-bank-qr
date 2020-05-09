package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.data_list
import kotlinx.android.synthetic.main.activity_profile.profile_photo

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

        info.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, AboutAppActivity::class.java)
            finish()
        }

        edit_profile.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, EditProfileActivity::class.java)
            finish()
        }

        add_profile.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, EditProfileActivity::class.java)
            finish()
        }

        setDataToListView()
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    private fun setDataToListView() {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()
        if (cursor!!.count != 0) {
            cursor.moveToFirst()

            val drawable = DataUtils.getImageInDrawable(cursor, "photo")
            if (drawable != null) {
                profile_photo.setImageDrawable(drawable)
            } else {
                profile_photo.visibility = View.GONE
                circle.visibility = View.VISIBLE
                letters.text = cursor.getString(cursor.getColumnIndex("name")).take(1) + cursor.getString(cursor.getColumnIndex("surname")).take(1)
            }

            val data = DataUtils.setUserData(cursor)

            val adapter = DataListAdapter(this, data, R.layout.data_list_item)
            data_list.adapter = adapter
        } else {
            add_profile.visibility = View.VISIBLE
        }
    }
}
