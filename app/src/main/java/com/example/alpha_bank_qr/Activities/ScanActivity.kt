package com.example.alpha_bank_qr.Activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Database.QRDatabaseHelper
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.Json
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity() {

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_scan)

        bottom_bar.menu.getItem(1).isChecked = true
        bottom_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.cards -> goToActivity(CardsActivity::class.java)
                R.id.scan -> goToActivity(ScanActivity::class.java)
                else -> goToActivity(ProfileActivity::class.java)
            }
            true
        }

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object: PermissionListener, ZXingScannerView.ResultHandler {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    scanner.setResultHandler(this)
                    scanner.startCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@ScanActivity, "Необходим доступ к камере!", Toast.LENGTH_SHORT).show()
                    goToActivity(CardsActivity::class.java)
                }

                // Обрабатываем результат сканирования QR
                override fun handleResult(rawResult: Result?) {
                    if (rawResult != null) {
                        val user = Json.fromJson(rawResult.text)

                        // Проверяем по QR, существует ли уже такая визитка или нет
                        val flag = DataUtils.checkCardForExistence(this@ScanActivity, user)
                        val dbHelper =
                            QRDatabaseHelper(
                                this@ScanActivity
                            )
                        val intent = Intent(this@ScanActivity, CardsActivity::class.java)
                        if (flag) {
                            intent.putExtra("fail", true)
                        } else {
                            dbHelper.addUser(user)
                            intent.putExtra("success", true)
                        }
                        onBackPressed()
                        dbHelper.close()
                        startActivity(intent)
                    }
                }
            }).check()
    }

    override fun onDestroy() {
        scanner.stopCamera()
        super.onDestroy()
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}