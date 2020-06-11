package com.example.alpha_bank_qr.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.util.*
import kotlin.collections.ArrayList

class EditProfileFragment : Fragment() {

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
        "Мобильный номер (другой)", "email (другой)", "Должность", "Компания", "Адрес", "Адрес (другой)",
        "Номер карты 1", "Номер карты 2", "VK", "Telegram", "Facebook", "Instagram", "Twitter")
    private var additionalFields = ArrayList<EditText>()
    private var availableFields = ArrayList<String>()

    private lateinit var db : AppDatabase
    private lateinit var mStorageRef: StorageReference
    private var uuid = ""
    private var uri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        allEditTexts = arrayListOf(
            surname, name, patronymic, company, job_title, mobile, mobile_second, email, email_second,
            address, address_second, card_number, card_number_second, website, vk, telegram, facebook, instagram, twitter, notes
        )

        additionalFields = arrayListOf(
            company, job_title, mobile_second, email_second, address, address_second,
            card_number, card_number_second, vk, telegram, facebook, instagram, twitter)

        setToolbar()

        setUserData()

        initializeFields()

        add_field.setOnClickListener { setFieldsDialog() }

        change_photo.setOnClickListener {
            CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(requireContext(), this)
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

    private fun setToolbar() {
        toolbar_edit_profile.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        toolbar_edit_profile.setOnMenuItemClickListener {
            if (it.itemId == R.id.save_user) saveUser()
            true
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
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
                editText.visibility = View.GONE
                editText.text.clear()
                initializeFields()
                return true
            }
        }
        return false
    }

    private fun setUserData() {
        val user = db.userDao().getOwnerUser()
        if (user != null) {
            surname.setText(user.surname)
            name.setText(user.name)
            patronymic.setText(user.patronymic)
            company.setText(user.company)
            job_title.setText(user.jobTitle)
            mobile.setText(user.mobile)
            mobile_second.setText(user.mobileSecond)
            email.setText(user.email)
            email_second.setText(user.emailSecond)
            address.setText(user.address)
            address_second.setText(user.addressSecond)
            card_number.setText(user.cardNumber)
            card_number_second.setText(user.cardNumberSecond)
            website.setText(user.website)
            vk.setText(user.vk)
            telegram.setText(user.telegram)
            facebook.setText(user.facebook)
            twitter.setText(user.twitter)
            instagram.setText(user.instagram)
            notes.setText(user.notes)
        }
        additionalFields.forEach {
            if (it.text.toString() == "") it.visibility = View.GONE
        }
    }

    private fun getUserData() : User {
        return User(
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
            card_number.text.toString(),
            card_number_second.text.toString(),
            website.text.toString(),
            vk.text.toString(),
            telegram.text.toString(),
            facebook.text.toString(),
            instagram.text.toString(),
            twitter.text.toString(),
            notes.text.toString()
        )
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

    private fun saveUserData() {
        var user = db.userDao().getOwnerUser()
        if (user == null) {
            val ownerUser = getUserData()
            val userUUID = UUID.randomUUID().toString()
            ownerUser.id = userUUID
            db.userDao().insertUser(ownerUser)

            val databaseRef = FirebaseDatabase.getInstance().getReference(userUUID)
            databaseRef.setValue(Gson().toJson(ownerUser))
        } else {
            val userUUID = user.id
            user = getUserData()
            user.id = userUUID

            db.userDao().updateUser(user)

            val databaseRef = FirebaseDatabase.getInstance().getReference(user.id)
            databaseRef.setValue(Gson().toJson(user))
        }
        parentFragmentManager.popBackStack()
    }

    private fun setFieldsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
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


}