package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.google.zxing.Result

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


        println(rawResult)
        onBackPressed()
        val intent = Intent(this, CardsActivity::class.java)
        startActivity(intent)
        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

}
