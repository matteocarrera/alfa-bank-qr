package com.example.cloud_cards.Activities

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
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.IdPair
import com.example.cloud_cards.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

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
        compressedBitmap.getPixels(intArray, 0, compressedBitmap.width, 0, 0, compressedBitmap.width, compressedBitmap.height)

        val source: LuminanceSource = RGBLuminanceSource(compressedBitmap.width, compressedBitmap.height, intArray)
        val bMap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = MultiFormatReader()
        val result = reader.decode(bMap)
        getUserFromQR(result)
    }

    private fun getUserFromQR(rawResult : Result) {
        val result = rawResult.toString()
        if (!result.contains("cloudcards.h1n.ru") && !result.contains("&")) {
            Toast.makeText(applicationContext, "QR невозможно считать!", Toast.LENGTH_SHORT).show()
            return
        }
        val idsString = result.split("#")[1]
        val parentId = idsString.split("&")[0]
        val uuid = idsString.split("&")[1]
        var idPairList = db.idPairDao().getAllPairs()
        var idPair = IdPair(uuid, parentId)
        if (idPairList.contains(idPair)) {
            Toast.makeText(applicationContext, "Ошибка считывания визитки!", Toast.LENGTH_SHORT).show()
            return
        }
        db.idPairDao().insertPair(idPair)
        Toast.makeText(applicationContext, "Визитка успешно считана!", Toast.LENGTH_SHORT).show()

    }
}