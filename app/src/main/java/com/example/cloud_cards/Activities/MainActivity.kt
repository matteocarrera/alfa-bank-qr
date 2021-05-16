package com.example.cloud_cards.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.QRUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var db : AppDatabase

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
            QRUtils.decodeQRFromImage(bitmap, applicationContext)
        }
    }

    // Обработка нескольких изображений
    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let { arrayList ->
            arrayList.forEach {
                val uri = it as? Uri
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                QRUtils.decodeQRFromImage(bitmap, applicationContext)
            }
        }
    }
}