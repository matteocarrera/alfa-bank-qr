package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(navView, navController)
        navView.setupWithNavController(navController)

        db = AppDatabase.getInstance(applicationContext)

        // Получение QR-визитки в виде изображения вне приложения
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true)
            handleSendImage(intent)
        else if (intent.action == Intent.ACTION_SEND_MULTIPLE && intent.type?.startsWith("image/") == true)
            handleSendMultipleImages(intent)
    }

    // Обработка полученного изображения вне приложения
    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val bitmap =
                MediaStore.Images.Media.getBitmap(this.contentResolver, it)
            decodeQRFromImage(bitmap)
        }
    }

    // Обработка нескольких изображений
    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let { arrayList ->
            arrayList.forEach {
                val uri = it as? Uri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                decodeQRFromImage(bitmap)
            }
        }
    }

    // Получение данных с QR-визитки (фотография)
    private fun decodeQRFromImage(bitmap: Bitmap) {
        val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        val intArray = IntArray(compressedBitmap.width * compressedBitmap.height)
        compressedBitmap.getPixels(
            intArray,
            0,
            compressedBitmap.width,
            0,
            0,
            compressedBitmap.width,
            compressedBitmap.height
        )

        val source: LuminanceSource =
            RGBLuminanceSource(compressedBitmap.width, compressedBitmap.height, intArray)
        val bMap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = MultiFormatReader()
        val result = reader.decode(bMap)
        getUserFromQR(result)
    }

    private fun getUserFromQR(rawResult: Result) {
        val databaseRef = FirebaseDatabase.getInstance().getReference(rawResult.toString())
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val jsonUser = dataSnapshot.value.toString()
                val user = Gson().fromJson(jsonUser, User::class.java)
                val scannedUsers = db.userDao().getAllUsers()
                var userExists = false
                scannedUsers.forEach {
                    if (it.uuid == user.uuid) userExists = true
                }
                if (userExists) {
                    Toast.makeText(
                        applicationContext,
                        "Ошибка считывания визитки!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    db.userDao().insertUser(user)
                    Toast.makeText(
                        applicationContext,
                        "Визитка успешно считана!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Ошибка считывания: " + databaseError.code)
            }
        })
    }
}