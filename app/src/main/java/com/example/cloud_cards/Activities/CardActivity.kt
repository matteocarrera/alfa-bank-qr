package com.example.cloud_cards.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.PhoneLookup
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cloud_cards.Adapters.DataListAdapter
import com.example.cloud_cards.Database.DBService
import com.example.cloud_cards.Entities.DataItem
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.DataUtils
import com.example.cloud_cards.Utils.ImageUtils
import com.example.cloud_cards.Utils.Json
import com.example.cloud_cards.Utils.ProgramUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.activity_qr.view.*
import net.glxn.qrgen.android.QRCode
import java.util.*

class CardActivity : AppCompatActivity() {

    private var id : Int = 0
    private var user = User()
    private lateinit var mStorageRef: StorageReference
    private var READ_CONTACTS_PERMISSION = 0
    private var WRITE_CONTACTS_PERMISSION = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

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
            user = DBService.getUserById(this, id)

            //val flag = (user.isScanned)

            val popupMenu = PopupMenu(this, more)

            popupMenu.setOnMenuItemClickListener { item ->

                when(item.itemId) {
                    R.id.qr -> { setQRWindow(id) }
                    R.id.delete -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Удаление визитки")
                        builder.setMessage("Вы действительно хотите удалить данную визитку?")
                        builder.setPositiveButton("Да"){ _, _ ->
                            DBService.deleteUser(this, id)
                            if (!flag) DBService.deleteCard(this, cardId)
                            goToActivity(CardsActivity::class.java)
                            Toast.makeText(this,"Визитка успешно удалена!",Toast.LENGTH_SHORT).show()
                        }
                        builder.setNegativeButton("Нет"){ _, _ -> }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                    R.id.export -> {
                        if (!contactExists(user.mobile))
                            startActivity(ProgramUtils.exportContact(user))
                    }
                    R.id.add_photo -> {
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
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
                cur.use {
                    if (it != null) {
                        if (it.moveToFirst()) {
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
            startActivity(ProgramUtils.exportContact(user))
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

                val uuid = UUID.randomUUID().toString()
                mStorageRef = FirebaseStorage.getInstance().getReference(uuid)
                mStorageRef.putFile(Uri.parse(result.uri.toString())).addOnSuccessListener {
                    DBService.updateUserPhoto(this, id, uuid)
                    setDataToListView(id)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToListView(id : Int) {
        val user = DBService.getUserById(this, id)
        val photoUUID = user.photo
        if (photoUUID != "") {
            ImageUtils.getImageFromFirebase(photoUUID, profile_photo)
        } else {
            profile_photo.visibility = View.GONE
            circle.visibility = View.VISIBLE
            letters.text = user.name.take(1) + user.surname.take(1)
        }

        val data = DataUtils.setUserData(user)

        val adapter = DataListAdapter(this, data, R.layout.data_list_item)
        data_list.adapter = adapter
        data_list.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView?.getItemAtPosition(i) as DataItem
            when (item.title) {
                "мобильный номер", "мобильный номер (другой)" -> ProgramUtils.makeCall(this, this, item.data)
                "email", "email (другой)" -> ProgramUtils.makeEmail(this, item.data)
                "адрес", "адрес (другой)" -> ProgramUtils.openMap(this, item.data)
                "vk", "facebook", "instagram", "twitter" -> ProgramUtils.openWebsite(this, item)
                else -> {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("text", item.data)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Данные скопированы", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun setQRWindow(id : Int) {
        val user = DBService.getUserById(this, id)

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_qr, null)

        val mBuilder = AlertDialog.Builder(this)
            .setTitle("Покажите QR код")
            .setView(mDialogView)

        var bitmap = QRCode.from(Json.toJson(user)).withCharset("utf-8").withSize(1000, 1000).bitmap()
        bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
        mDialogView.qr_img.setImageBitmap(bitmap)

        val  mAlertDialog = mBuilder.show()

        //mDialogView.ok.setOnClickListener { mAlertDialog.dismiss() }

        //mDialogView.share.setOnClickListener {
        //    ProgramUtils.saveImage(this, arrayListOf(bitmap))
        //}
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
