package com.example.alpha_bank_qr.Activities

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.R
import kotlinx.android.synthetic.main.activity_create_card.back
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {

    private val fieldList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initializeFields()
        back.setOnClickListener { goToActivity(ProfileActivity::class.java) }
        add_field.setOnClickListener { setFieldsDialog() }

        company.setOnTouchListener { _, event -> deleteField(company, event) }
        job_title.setOnTouchListener { _, event -> deleteField(job_title, event) }
        mobile_second.setOnTouchListener { _, event -> deleteField(mobile_second, event) }
        email_second.setOnTouchListener { _, event -> deleteField(email_second, event) }
        address.setOnTouchListener { _, event -> deleteField(address, event) }
        address_second.setOnTouchListener { _, event -> deleteField(address_second, event) }
        vk.setOnTouchListener { _, event -> deleteField(vk, event) }
        facebook.setOnTouchListener { _, event -> deleteField(facebook, event) }
        twitter.setOnTouchListener { _, event -> deleteField(twitter, event) }
    }

    private fun initializeFields() {
        checkForVisibility(company)
        checkForVisibility(job_title)
        checkForVisibility(mobile_second)
        checkForVisibility(email_second)
        checkForVisibility(address)
        checkForVisibility(address_second)
        checkForVisibility(vk)
        checkForVisibility(facebook)
        checkForVisibility(twitter)
        fieldList.sort()
    }

    private fun deleteField(editText: EditText, event: MotionEvent): Boolean {
        val DRAWABLE_RIGHT = 2
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                editText.visibility = View.GONE
                editText.text.clear()
                initializeFields()
                changeFieldVisibility(editText.hint.toString(), View.GONE)
                return true
            }
        }
        return false
    }

    private fun checkForVisibility(editText: EditText) {
        if (editText.visibility == View.GONE && !fieldList.contains(editText.hint.toString())) fieldList.add(editText.hint.toString())
        else if (editText.visibility == View.VISIBLE) fieldList.remove(editText.hint.toString())
    }

    private fun changeFieldVisibility(name : String, visibility : Int) {
        when (name) {
            "Компания" -> company.visibility = visibility
            "Должность" -> job_title.visibility = visibility
            "Мобильный номер (другой)" -> mobile_second.visibility = visibility
            "Адрес" -> address.visibility = visibility
            "Адрес (другой)" -> address_second.visibility = visibility
            "vk" -> vk.visibility = visibility
            "facebook" -> facebook.visibility = visibility
            "twitter" -> twitter.visibility = visibility
        }
    }

    private fun setFieldsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Добавить поле")

        val fields = fieldList.toTypedArray()
        builder.setItems(fields) { _, item ->
            val selectedText = fields[item]
            changeFieldVisibility(selectedText, View.VISIBLE)
            initializeFields()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun goToActivity(cls : Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
