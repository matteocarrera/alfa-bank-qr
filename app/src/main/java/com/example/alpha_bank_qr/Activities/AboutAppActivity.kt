package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.R
import kotlinx.android.synthetic.main.activity_about_app.*


class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppThemeRed)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        back.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
