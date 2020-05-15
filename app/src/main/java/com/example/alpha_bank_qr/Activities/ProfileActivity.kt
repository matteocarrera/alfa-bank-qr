package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bottom_bar.menu.getItem(2).isChecked = true
        bottom_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.cards -> goToActivity(CardsActivity::class.java)
                R.id.scan -> goToActivity(ScanActivity::class.java)
                else -> goToActivity(ProfileActivity::class.java)
            }
            true
        }

        info.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, AboutAppActivity::class.java)
        }

        edit_profile.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, EditProfileActivity::class.java)
        }

        add_profile.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, EditProfileActivity::class.java)
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
