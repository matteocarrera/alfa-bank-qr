package com.example.cloud_cards.Utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.*
import com.google.firebase.firestore.FirebaseFirestore
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
            val linkBody = idsString.split("&")
            val parentId = linkBody[0]
            val uuid = linkBody[1]
            val type = if (linkBody.size == 3) linkBody[2] else CardType.personal.rawValue

            val ownerUser = db.userDao().getOwnerUser()
            val idPairList = db.idPairDao().getAllPairs()

            // Запрет на добавление своей же визитки
            if (ownerUser != null && parentId == ownerUser.parentId) {
                Toast.makeText(context, "Вы не можете сканировать свою же визитку!", Toast.LENGTH_SHORT).show()
                return
            }

            // Если у пользователя уже есть визитки данного контакта, то мы берем все на проверку
            val currentParentIdPairs = idPairList.filter { it.parentUuid == parentId }

            /*
                Если тип импортируемой визитки Персональная и количество визиток не 0, то проверяем
                все существующие визитки от данного контакта на наличие персональной визитки.
                Если такая уже существует, то выдаем ошибку. Допускается возможность хранения визиток
                компаний от одного и того же контакта, но их дублирование запрещено
             */
            if (type == CardType.personal.rawValue && currentParentIdPairs.isNotEmpty()) {
                var counter = 0
                currentParentIdPairs.forEach { idPair ->

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(idPair.parentUuid)
                        .collection("cards")
                        .document(idPair.uuid)
                        .get()
                        .addOnSuccessListener { document ->

                            val cardTypeRaw = document.data?.get("type") as? String
                            val cardType = if (cardTypeRaw != null) CardType.valueOf(cardTypeRaw) else null
                            if (cardType == CardType.personal || cardType == null) {
                                Toast.makeText(context, "Визитка данного пользователя уже существует!", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                            counter++
                            if (counter == currentParentIdPairs.size) {
                                importContact(parentId, uuid, idPairList, context, db)
                                return@addOnSuccessListener
                            }
                        }
                }
            } else {
                importContact(parentId, uuid, idPairList, context, db)
            }
        }

        /*
            Метод, проверящий наличие такой пары ID в БД телефона
         */

        private fun importContact(parentId: String, uuid: String, idPairList: List<IdPair>, context: Context, db: AppDatabase) {
            val idPair = IdPair(uuid, parentId)
            if (!idPairList.contains(idPair)) {
                Toast.makeText(context, "Визитка успешно считана!", Toast.LENGTH_SHORT).show()
                db.idPairDao().insertPair(idPair)
                return
            }

            Toast.makeText(context, "Такая визитка уже существует!", Toast.LENGTH_SHORT).show()
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

        /*
            Генерация ссылки на визитку
         */
        fun generateSiteLink(parentUuid: String, uuid: String, isPersonal: Boolean): String {
            val cardType = if (isPersonal) CardType.personal.rawValue else CardType.company.rawValue
            return "http://cloudcards.h1n.ru/#${parentUuid}&${uuid}&${cardType}"
        }
    }

}