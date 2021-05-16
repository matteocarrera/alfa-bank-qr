package com.example.cloud_cards.Utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.IdPair
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

class QRUtils {
    companion object {
        /*
            Получение данных из QR кода, анализ и добавление в БД
         */
        fun getUserFromQR(result : String, context: Context) {
            val db = AppDatabase.getInstance(context)
            if (!result.contains("cloudcards.h1n.ru") && !result.contains("&")) {
                Toast.makeText(context, "QR невозможно считать!", Toast.LENGTH_SHORT).show()
                return
            }
            val idsString = result.split("#")[1]
            val parentId = idsString.split("&")[0]
            val uuid = idsString.split("&")[1]
            val idPairList = db.idPairDao().getAllPairs()
            val idPair = IdPair(uuid, parentId)
            if (idPairList.contains(idPair)) {
                Toast.makeText(context, "Такая визитка уже существует!", Toast.LENGTH_SHORT).show()
                return
            }
            db.idPairDao().insertPair(idPair)
            Toast.makeText(context, "Визитка успешно считана!", Toast.LENGTH_SHORT).show()
        }

        /*
            Получение данных из фотографии, на которой есть QR код
         */
        fun decodeQRFromImage(bitmap: Bitmap, context: Context) {
            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
            val intArray = IntArray(compressedBitmap.width * compressedBitmap.height)
            compressedBitmap.getPixels(intArray, 0, compressedBitmap.width, 0, 0, compressedBitmap.width, compressedBitmap.height)

            val source: LuminanceSource = RGBLuminanceSource(compressedBitmap.width, compressedBitmap.height, intArray)
            val bMap = BinaryBitmap(HybridBinarizer(source))

            val reader: Reader = MultiFormatReader()
            val result = reader.decode(bMap)
            getUserFromQR(result.toString(), context)
        }
    }

}