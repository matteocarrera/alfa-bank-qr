package com.example.cloud_cards.Activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud_cards.Adapters.DataAdapter
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.DataUtils
import com.example.cloud_cards.Utils.ImageUtils
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.activity_card_view.*

class CardViewActivity : AppCompatActivity() {

    private lateinit var user: User
    private var isProfile = false
    private var data = ArrayList<DataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_view)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_card_view)
        toolbar.inflateMenu(R.menu.card_view_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share -> {
                    Toast.makeText(this, "SHARE", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Получаем данные, отправленные как Extra для загрузки данных
        user = intent.getSerializableExtra("user") as User
        isProfile = intent.getBooleanExtra("isProfile", false)

        // Если вызвано окно профиля, то меняем заголовок и убираем меню
        if (isProfile) {
            toolbar.title = getString(R.string.profile)
            val item = toolbar.menu.getItem(0)
            item.isVisible = false
        }

        // Загружаем данные пользователя
        if (user.photo == "") {
            letters.visibility = View.VISIBLE
            letters.text = user.name.take(1).plus(user.surname.take(1))
        } else {
            profile_photo.visibility = View.VISIBLE
            letters.visibility = View.GONE
            ImageUtils.getImageFromFirebase(user.photo, profile_photo)
        }

        data = DataUtils.setUserData(user)

        val adapter = DataAdapter(data, View.GONE, this)
        data_list.adapter = adapter
        data_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, data[position].data, Toast.LENGTH_SHORT).show()
        }
    }
}