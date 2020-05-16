package com.example.alpha_bank_qr.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.alpha_bank_qr.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        back.setOnClickListener {
            finish()
        }
    }
}
