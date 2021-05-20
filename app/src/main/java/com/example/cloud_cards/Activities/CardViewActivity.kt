package com.example.cloud_cards.Activities

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.PhoneLookup
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cloud_cards.Adapters.DataAdapter
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.DataUtils
import com.example.cloud_cards.Utils.ImageUtils
import com.example.cloud_cards.Utils.ProgramUtils
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
                    if (permissionsAreGranted() && !contactExists(user.mobile)) {
                        ProgramUtils.exportContact(this, user, profile_photo.drawable)
                    }
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
        if (user.photo.isEmpty()) {
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
            val item = data_list.adapter.getItem(position) as DataItem
            when (item.title) {
                "мобильный номер", "мобильный номер (другой)" -> ProgramUtils.makeCall(this, this, item.data)
                "email", "email (другой)" -> ProgramUtils.makeEmail(this, item.data)
                "адрес", "адрес (другой)" -> ProgramUtils.openMap(this, item.data)
                "vk", "facebook", "instagram", "twitter" -> ProgramUtils.openWebsite(this, item)
                else -> {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("text", item.data)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Данные поля \"${item.title}\" скопированы", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*
        Метод, подволяющий проверить наличие контакта в телефонной книге
     */

    private fun contactExists(number: String): Boolean {
        if (number.isNotEmpty()) {
            val lookupUri: Uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
            val mPhoneNumberProjection = arrayOf(
                PhoneLookup._ID,
                PhoneLookup.NUMBER,
                PhoneLookup.DISPLAY_NAME)
            val cur: Cursor? = contentResolver
                .query(lookupUri, mPhoneNumberProjection, null, null, null)
            cur.use {
                if (it != null && it.moveToFirst()) {
                    Toast.makeText(this, "Контакт с таким мобильным номером уже существует!", Toast.LENGTH_LONG).show()
                    return true
                }
            }
            return false
        } else
            return false
    }

    /*
        Метод, позволяющий проверить наличие разрешений на взаимодействие со списком контактов
     */

    private fun permissionsAreGranted(): Boolean {
        val readContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        val writeContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
        if (readContactsPermission != PackageManager.PERMISSION_GRANTED &&
            writeContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                1)
            return false
        }
        return true
    }

    /*
        Метод, позволяющий обработать результат запроса на разрешение доступа к контактам телефона
     */

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ProgramUtils.exportContact(this, user, profile_photo.drawable)
        }
    }
}