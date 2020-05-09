package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.QRDatabaseHelper
import com.example.alpha_bank_qr.Utils.Json
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler{

    private var mScannerView: ZXingScannerView? = null

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
        val intent = Intent(this, CardsActivity::class.java)
        startActivity(intent)
    }

    override fun handleResult(rawResult: Result) {
        val user = Json.fromJson(rawResult.text)
        println("Notes = " + user.notes)

        val dbHelper = QRDatabaseHelper(this)
        dbHelper.addUser(user)

        onBackPressed()
        val intent = Intent(this, CardsActivity::class.java)
        startActivity(intent)
    }
}