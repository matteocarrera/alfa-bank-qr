package com.example.alpha_bank_qr.Fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alpha_bank_qr.Activities.CardsActivity
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

class CameraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Dexter.withContext(view.context)
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
                    Toast.makeText(view.context, "Необходим доступ к камере!", Toast.LENGTH_SHORT).show()
                    //goToActivity(CardsActivity::class.java)
                }

                // Обрабатываем результат сканирования QR
                override fun handleResult(rawResult: Result?) {
                    if (rawResult != null) {
                        /*val user = Json.fromJson(rawResult.text)

                        // Проверяем по QR, существует ли уже такая визитка или нет
                        val flag = DataUtils.checkCardForExistence(view.context, user)
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
                        startActivity(intent)*/
                    }
                }
            }).check()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scanner.stopCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
    }

    override fun onStop() {
        super.onStop()
        scanner.stopCamera()
    }
}