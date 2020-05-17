package com.example.alpha_bank_qr.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alpha_bank_qr.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        back.setOnClickListener {
            finish()
        }

        setSwitchersStates()

        camera_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        gallery_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }

        call_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 3)
        }

        contacts_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), 4)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        setSwitchersStates()
    }

    private fun setSwitchersStates() {
        checkPermission(arrayListOf(Manifest.permission.CAMERA), camera_switch)
        checkPermission(arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), gallery_switch)
        checkPermission(arrayListOf(Manifest.permission.CALL_PHONE), call_switch)
        checkPermission(arrayListOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS),contacts_switch)
    }

    private fun checkPermission(permissions : ArrayList<String>, switch: Switch) {
        val permissionAnswers = ArrayList<Boolean>()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                permissionAnswers.add(false)
            } else { permissionAnswers.add(true) }
        }
        switch.isChecked = !permissionAnswers.containsAll(listOf(false))
        switch.isEnabled = !switch.isChecked
    }
}
