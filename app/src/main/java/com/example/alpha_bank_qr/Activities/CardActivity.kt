package com.example.alpha_bank_qr.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.PhoneLookup
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Entities.DataItem
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.activity_qr.view.*

class CardActivity : AppCompatActivity() {

    private var id : Int = 0
    private var cursor : Cursor? = null
    private var READ_CONTACTS_PERMISSION = 0
    private var WRITE_CONTACTS_PERMISSION = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        READ_CONTACTS_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        WRITE_CONTACTS_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)

        val bundle : Bundle? = intent.extras
        id = bundle!!.getInt("user_id")
        val cardId = bundle.getInt("card_id")

        back.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, CardsActivity::class.java)
            finish()
        }

        more.setOnClickListener {
            val dbHelper = QRDatabaseHelper(this)
            cursor = dbHelper.getUser(id)
            if (cursor!!.count != 0) { cursor!!.moveToFirst() }

            val flag = (cursor!!.getInt(cursor!!.getColumnIndex("is_scanned")) == 1)

            val popupMenu = PopupMenu(this, more)

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.qr -> { setQRWindow(id) }
                    R.id.delete -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Удаление визитки")
                        builder.setMessage("Вы действительно хотите удалить данную визитку?")
                        builder.setPositiveButton("Да"){ _, _ ->
                            dbHelper.deleteUser(id)
                            if (!flag) dbHelper.deleteCard(cardId)
                            dbHelper.close()
                            goToActivity(CardsActivity::class.java)
                            Toast.makeText(this,"Визитка успешно удалена!",Toast.LENGTH_SHORT).show()
                        }
                        builder.setNegativeButton("Нет"){ _, _ -> }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                    R.id.export -> {
                        if (!contactExists(cursor!!.getString(cursor!!.getColumnIndex("mobile"))))
                            startActivity(ProgramUtils.exportContact(DataUtils.parseDataToUser(DataUtils.setUserData(cursor!!), null)))
                        dbHelper.close()
                    }
                    R.id.add_photo -> {
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this);
                    }
                }
                true
            }

            if (flag) popupMenu.menuInflater.inflate(R.menu.saved_card_menu, popupMenu.menu)
            else popupMenu.menuInflater.inflate(R.menu.my_card_menu, popupMenu.menu)

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {

            } finally {
                popupMenu.show()
            }

            popupMenu.show()
        }

        setDataToListView(id)
    }

    // Проверяем, существует ли такой контакт в списке контактов самого телефона, а не приложения
    private fun contactExists(number: String?): Boolean {
        if (READ_CONTACTS_PERMISSION != PackageManager.PERMISSION_GRANTED &&
            WRITE_CONTACTS_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                1)
            return true
        } else {
            if (number != null) {
                val lookupUri: Uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
                val mPhoneNumberProjection = arrayOf(
                    PhoneLookup._ID,
                    PhoneLookup.NUMBER,
                    PhoneLookup.DISPLAY_NAME)
                val cur: Cursor? = contentResolver
                    .query(lookupUri, mPhoneNumberProjection, null, null, null)
                cur.use { cur ->
                    if (cur != null) {
                        if (cur.moveToFirst()) {
                            Toast.makeText(this, "Контакт с таким мобильным номером уже существует!", Toast.LENGTH_LONG).show()
                            return true
                        }
                    }
                }
                return false
            } else return false
        }
    }

    // Обрабатываем результат запроса на разрешение доступа к контактам телефона
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(ProgramUtils.exportContact(DataUtils.parseDataToUser(DataUtils.setUserData(cursor!!), null)))
        }
    }

    // Обработка добавления фотографии для визитки пользователя
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                profile_photo.setImageURI(result.uri)
                profile_photo.visibility = View.GONE
                circle.visibility = View.VISIBLE
                val dbHelper = QRDatabaseHelper(this)
                val drawable = profile_photo.drawable
                dbHelper.updateUserPhoto(id, drawable)
                dbHelper.close()
                setDataToListView(id)
                finish();
                startActivity(intent);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToListView(id : Int) {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getUser(id)
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
            data_list.setOnItemClickListener { adapterView, _, i, _ ->
                val item = adapterView?.getItemAtPosition(i) as DataItem
                when (item.title) {
                    "мобильный номер", "мобильный номер (другой)" -> ProgramUtils.makeCall(this, this, item.description)
                    "email", "email (другой)" -> ProgramUtils.makeEmail(this, item.description)
                    "адрес", "адрес (другой)" -> ProgramUtils.openMap(this, item.description)
                    "vk", "facebook", "instagram", "twitter" -> ProgramUtils.openWebsite(this, item)
                    else -> {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("text", item.description)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(this, "Данные скопированы", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dbHelper.close()
    }

    @SuppressLint("InflateParams")
    private fun setQRWindow(id : Int) {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getQRFromUser(id)
        if (cursor!!.count != 0) {
            cursor.moveToFirst()
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_qr, null)

            val mBuilder = AlertDialog.Builder(this)
                .setTitle("Покажите QR код")
                .setView(mDialogView)

            val dr = DataUtils.getImageInDrawable(cursor, "qr")
            val bitmap = (dr as BitmapDrawable).bitmap

            val d: Drawable = BitmapDrawable(
                resources,
                Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
            )
            mDialogView.qr_img.setImageDrawable(d)
            val  mAlertDialog = mBuilder.show()

            mDialogView.ok.setOnClickListener { mAlertDialog.dismiss() }

            mDialogView.share.setOnClickListener {
                ProgramUtils.saveImage(this, bitmap)
            }
        }
        dbHelper.close()
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
