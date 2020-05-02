package com.example.alpha_bank_qr.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.activity_qr.view.*

class CardActivity : AppCompatActivity() {

    var id : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        val bundle : Bundle? = intent.extras
        id = bundle!!.getInt("user_id")
        val cardId = bundle.getInt("card_id")

        back.setOnClickListener {
            ProgramUtils.goToActivityAnimated(this, CardsActivity::class.java)
            finish()
        }

        more.setOnClickListener {
            val dbHelper = QRDatabaseHelper(this)
            val cursor = dbHelper.getUser(id)
            if (cursor!!.count != 0) { cursor.moveToFirst() }

            val flag = (cursor.getInt(cursor.getColumnIndex("is_scanned")) == 1)

            val popupMenu = PopupMenu(this, more)
            if (flag) popupMenu.menuInflater.inflate(R.menu.saved_card_menu, popupMenu.menu)
            else popupMenu.menuInflater.inflate(R.menu.my_card_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.qr -> { setQRWindow(cardId) }
                    R.id.delete -> {
                        dbHelper.deleteUser(id)
                        if (!flag) dbHelper.deleteCard(cardId)
                        dbHelper.close()
                        goToActivity(CardsActivity::class.java)
                    }
                    R.id.export -> {
                        startActivity(ProgramUtils.exportContact(DataUtils.parseDataToUser(DataUtils.setUserData(cursor), null)))
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
            popupMenu.show()
        }

        setDataToListView(id)
    }

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
        }
        dbHelper.close()
    }

    @SuppressLint("InflateParams")
    private fun setQRWindow(id : Int) {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getQRFromCard(id)
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
