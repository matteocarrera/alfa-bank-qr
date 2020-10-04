package com.example.alpha_bank_qr.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_bank_qr.Database.QRDatabaseHelper
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ImageUtils
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create_card.back
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class EditProfileActivity : AppCompatActivity() {

    /*  Описание списков, использующихся в классе
        allEditTexts - Список всех EditText на экране EditProfileActivity

        Используем для того, чтобы добавить доп. поля для заполнения профиля
        fieldsHints -      Список с именами полей на русском языке, которые являются дополнительными
        additionalFields - Список с самими полями, используем для того, чтобы скрывать/отображать
                           поля для пользователя в интерфейсе
        availableFields -  Поля, доступные на данный момент для выбора из списка

        Общая логика такова:
        В onCreate мы проверяем, пустые ли доп. поля или нет (используем метод initializeFields).
        Те поля, которые оказываются пустыми, добавляем в лист availableFields, причем вносим туда
        описание EditText, а не сам объект. После чего, в методе setFieldsDialog мы проверяем,
        содержится ли элемент availableFields в fieldsHints (данная проверка нужна для того, чтобы
        выставить поля в нужном нам списке, заданом нами ниже) и выводим их далее в выпадающем
        списке для выбора пользователем.
     */

    private var allEditTexts = ArrayList<EditText>()
    private val fieldsHints = arrayOf(
        "Мобильный номер (другой)", "email (другой)", "Должность", "Компания",
        "Сбербанк (расчетный счет)", "ВТБ (расчетный счет)", "Альфа-Банк (расчетный счет)",
        "VK", "Facebook", "Instagram", "Twitter")
    private var additionalFields = ArrayList<EditText>()
    private var availableFields = ArrayList<String>()

    private lateinit var mStorageRef: StorageReference
    private var uuid = ""
    private var uri = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        allEditTexts = arrayListOf(
            surname, name, patronymic, company, job_title, mobile, mobile_second, email, email_second,
            address, address_second, sberbank, vtb, alfabank, vk, facebook, instagram, twitter, notes
        )

        additionalFields = arrayListOf(
            company, job_title, mobile_second, email_second, address, address_second,
            sberbank, vtb, alfabank, vk, facebook, instagram, twitter)

        setDataToEdittext()

        initializeFields()

        back.setOnClickListener {
            finish()
        }

        add_field.setOnClickListener { setFieldsDialog() }

        save.setOnClickListener { saveUser() }

        change_photo.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        }

        for (i in additionalFields.indices) {
            additionalFields[i].setOnTouchListener { _, event -> deleteField(additionalFields[i], event) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) photo.setImageURI(result.uri)
            uri = try {
                result.uri.toString()
            } catch (e : Exception) {
                ""
            }
        }
    }

    // Узнаем для каждого поля его текущее состояние для конкретного профиля пользователя
    private fun initializeFields() {
        for (i in additionalFields.indices) {
            checkForVisibility(additionalFields[i])
        }
    }

    // Если поле пустое, то добавляем его в список тех, которые можно отображать при нажатии на кнопку "добавить поле"
    private fun checkForVisibility(editText: EditText) {
        if (editText.visibility == View.GONE &&
            !availableFields.contains(editText.hint.toString())) availableFields.add(editText.hint.toString())
        else if (editText.visibility == View.VISIBLE) availableFields.remove(editText.hint.toString())
    }

    // Функция для реализации возможности удаления доп. полей
    private fun deleteField(editText: EditText, event: MotionEvent): Boolean {
        val DRAWABLE_RIGHT = 2
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
                editText.visibility = View.GONE
                editText.text.clear()
                initializeFields()
                return true
            }
        }
        return false
    }

    private fun setFieldsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Добавить поле")

        val fieldsList = ArrayList<String>()
        fieldsList.clear()
        fieldsHints.forEach {
            if (availableFields.contains(it)) fieldsList.add(it)
        }

        val fields = fieldsList.toTypedArray()
        builder.setItems(fields) { _, item ->
            val selectedText = fields[item]
            allEditTexts.forEach {
                if (it.hint.toString() == selectedText) it.visibility = View.VISIBLE
            }
            initializeFields()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setDataToEdittext() {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()
        if (cursor!!.count != 0) {
            cursor.moveToFirst()

            uuid = cursor.getString(cursor.getColumnIndex("photo"))
            ImageUtils.getImageFromFirebase(uuid, photo)

            for (i in 0 until QRDatabaseHelper.allUserColumns.size) {
                allEditTexts[i].setText(cursor.getString(cursor.getColumnIndex(QRDatabaseHelper.allUserColumns[i])))
            }
        }
        additionalFields.forEach {
            if (it.text.toString() == "") it.visibility = View.GONE
        }
    }

    // Сначала загружаем фото на сервер, если успешно, то только потом сохраняем данные пользователя
    private fun saveUser() {
        progressbar.visibility = View.VISIBLE
        if (uri != "") {
            uuid = UUID.randomUUID().toString()
            mStorageRef = FirebaseStorage.getInstance().getReference(uuid)
            mStorageRef.putFile(Uri.parse(uri)).addOnSuccessListener {
                saveUserData()
            }
        } else {
            saveUserData()
        }
    }

    // Сохраняем текстовые данные в БД
    private fun saveUserData() {
        val dbHelper = QRDatabaseHelper(this)
        val cursor = dbHelper.getOwnerUser()

        when {
            name.text.toString() == "" -> ProgramUtils.setError(this, "Введите имя!")
            surname.text.toString() == "" -> ProgramUtils.setError(this, "Введите фамилию!")
            mobile.text.toString() == "" -> ProgramUtils.setError(this, "Введите мобильный номер!")
            else -> {
                if (cursor!!.count != 0) {
                    // Сохраняем уже в существующий профиль с обновлением данных
                    cursor.moveToFirst()
                    val user = getUserData()
                    //user.id = cursor.getInt(cursor.getColumnIndex("id"))
                    dbHelper.updateUser(user)

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Обновление данных")
                    builder.setMessage("Обновить данные в существующих визитках?\nВНИМАНИЕ!\nДанные, которые Вы удалили, будут также удалены в визитках!")
                    builder.setPositiveButton("Да"){dialog, which ->
                        Toast.makeText(applicationContext,"Данные в Ваших визитках успешно обновлены!",Toast.LENGTH_SHORT).show()
                        DataUtils.updateMyCardsData(this, getUserData())
                        //ProgramUtils.goToActivityAnimated(this, ProfileActivity::class.java)
                        finish()
                    }
                    builder.setNegativeButton("Нет"){dialog,which ->
                        //ProgramUtils.goToActivityAnimated(this, ProfileActivity::class.java)
                        finish()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                } else {
                    // Создаем новый профиль для основного пользователя
                    val user = getUserData()
                    dbHelper.addUser(user)
                    //ProgramUtils.goToActivityAnimated(this, ProfileActivity::class.java)
                    finish()
                }
            }
        }
    }

    private fun getUserData() : User {
        /* return User(
            uuid,
            true,
            false,
            name.text.toString(),
            surname.text.toString(),
            patronymic.text.toString(),
            company.text.toString(),
            job_title.text.toString(),
            mobile.text.toString(),
            mobile_second.text.toString(),
            email.text.toString(),
            email_second.text.toString(),
            address.text.toString(),
            address_second.text.toString(),
            sberbank.text.toString(),
            vtb.text.toString(),
            alfabank.text.toString(),
            vk.text.toString(),
            facebook.text.toString(),
            instagram.text.toString(),
            twitter.text.toString(),
            notes.text.toString()) */
    }
}