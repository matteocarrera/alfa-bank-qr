package com.example.cloudCards.Fragments

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
import com.example.cloudCards.Database.AppDatabase
import com.example.cloudCards.Database.FirestoreInstance
import com.example.cloudCards.Entities.User
import com.example.cloudCards.R
import com.example.cloudCards.Utils.DataUtils.Companion.userToMap
import com.example.cloudCards.Utils.ImageUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
        "Мобильный номер (другой)",
        "email (другой)",
        "Должность",
        "Компания",
        "Адрес",
        "Адрес (другой)",
        "Номер карты 1",
        "Номер карты 2",
        "VK",
        "Telegram",
        "Facebook",
        "Instagram",
        "Twitter"
    )
    private var additionalFields = ArrayList<EditText>()
    private var availableFields = ArrayList<String>()

    private lateinit var db: AppDatabase
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
            surname,
            name,
            patronymic,
            company,
            job_title,
            mobile,
            mobile_second,
            email,
            email_second,
            address,
            address_second,
            card_number,
            card_number_second,
            website,
            vk,
            telegram,
            facebook,
            instagram,
            twitter,
            notes
        )

        additionalFields = arrayListOf(
            company, job_title, mobile_second, email_second, address, address_second,
            card_number, card_number_second, vk, telegram, facebook, instagram, twitter
        )

        setToolbar(this)

        setUserData(this)

        initializeFields(this)

        add_field.setOnClickListener { setFieldsDialog(this) }

        delete_all_field.setOnClickListener {
            surname.setText("")
            name.setText("")
            patronymic.setText("")
            company.setText("")
            job_title.setText("")
            mobile.setText("")
            mobile_second.setText("")
            email.setText("")
            email_second.setText("")
            address.setText("")
            address_second.setText("")
            card_number.setText("")
            card_number_second.setText("")
            website.setText("")
            vk.setText("")
            telegram.setText("")
            facebook.setText("")
            instagram.setText("")
            twitter.setText("")
            notes.setText("")
        }

        change_photo.setOnClickListener {
            CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(requireContext(), this)
        }

        for (i in additionalFields.indices) {
            additionalFields[i].setOnTouchListener { _, event ->
                deleteField(
                    this, additionalFields[i],
                    event
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) photo.setImageURI(result.uri)
            uri = try {
                result.uri.toString()
            } catch (e: Exception) {
                "Exception while getting result"
            }
        }
    }

    companion object {
        private fun setUserData(editProfileFragment: EditProfileFragment) {
            val user = editProfileFragment.db.userDao().getOwnerUser()
            ImageUtils.getImageFromFirebase(user.photo, editProfileFragment.photo)
            editProfileFragment.surname.setText(user.surname)
            editProfileFragment.name.setText(user.name)
            editProfileFragment.patronymic.setText(user.patronymic)
            editProfileFragment.company.setText(user.company)
            editProfileFragment.job_title.setText(user.jobTitle)
            editProfileFragment.mobile.setText(user.mobile)
            editProfileFragment.mobile_second.setText(user.mobileSecond)
            editProfileFragment.email.setText(user.email)
            editProfileFragment.email_second.setText(user.emailSecond)
            editProfileFragment.address.setText(user.address)
            editProfileFragment.address_second.setText(user.addressSecond)
            editProfileFragment.card_number.setText(user.cardNumber)
            editProfileFragment.card_number_second.setText(user.cardNumberSecond)
            editProfileFragment.website.setText(user.website)
            editProfileFragment.vk.setText(user.vk)
            editProfileFragment.telegram.setText(user.telegram)
            editProfileFragment.facebook.setText(user.facebook)
            editProfileFragment.twitter.setText(user.twitter)
            editProfileFragment.instagram.setText(user.instagram)
            editProfileFragment.notes.setText(user.notes)
            editProfileFragment.additionalFields.forEach {
                if (it.text.toString() == "") it.visibility = View.GONE
            }
        }

        private fun setToolbar(editProfileFragment: EditProfileFragment) {
            editProfileFragment.toolbar_edit_profile.setNavigationOnClickListener {
                editProfileFragment.parentFragmentManager.popBackStack()
            }
            editProfileFragment.toolbar_edit_profile.setOnMenuItemClickListener {
                if (it.itemId == R.id.save_user) saveUser(editProfileFragment)

                true
            }
        }

        // Узнаем для каждого поля его текущее состояние для конкретного профиля пользователя
        private fun initializeFields(editProfileFragment: EditProfileFragment) {
            for (i in editProfileFragment.additionalFields.indices) {
                checkForVisibility(
                    editProfileFragment,
                    editProfileFragment.additionalFields[i]
                )
            }
        }

        private fun getUserData(editProfileFragment: EditProfileFragment): User {
            return User(
                uuid = editProfileFragment.uuid,
                parentId = editProfileFragment.uuid,
                photo = editProfileFragment.uuid,
                name = editProfileFragment.name.text.toString(),
                surname = editProfileFragment.surname.text.toString(),
                patronymic = editProfileFragment.patronymic.text.toString(),
                company = editProfileFragment.company.text.toString(),
                jobTitle = editProfileFragment.job_title.text.toString(),
                mobile = editProfileFragment.mobile.text.toString(),
                mobileSecond = editProfileFragment.mobile_second.text.toString(),
                email = editProfileFragment.email.text.toString(),
                emailSecond = editProfileFragment.email_second.text.toString(),
                address = editProfileFragment.address.text.toString(),
                addressSecond = editProfileFragment.address_second.text.toString(),
                cardNumber = editProfileFragment.card_number.text.toString(),
                cardNumberSecond = editProfileFragment.card_number_second.text.toString(),
                website = editProfileFragment.website.text.toString(),
                vk = editProfileFragment.vk.text.toString(),
                telegram = editProfileFragment.telegram.text.toString(),
                facebook = editProfileFragment.facebook.text.toString(),
                instagram = editProfileFragment.instagram.text.toString(),
                twitter = editProfileFragment.twitter.text.toString(),
                notes = editProfileFragment.notes.text.toString()
            )
        }

        // Если поле пустое, то добавляем его в список тех, которые можно отображать при нажатии на кнопку "добавить поле"
        private fun checkForVisibility(
            editProfileFragment: EditProfileFragment,
            editText: EditText
        ) {
            if (editText.visibility == View.GONE &&
                !editProfileFragment.availableFields.contains(editText.hint.toString())
            ) editProfileFragment.availableFields.add(editText.hint.toString())
            else if (editText.visibility == View.VISIBLE) editProfileFragment.availableFields.remove(
                editText.hint.toString()
            )
        }

        private fun setFieldsDialog(editProfileFragment: EditProfileFragment) {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(editProfileFragment.requireContext())
            builder.setTitle("Добавить поле")

            val fieldsList = ArrayList<String>()
            fieldsList.clear()
            editProfileFragment.fieldsHints.forEach {
                if (editProfileFragment.availableFields.contains(it)) fieldsList.add(it)
            }

            val fields = fieldsList.toTypedArray()
            builder.setItems(fields) { _, item ->
                val selectedText = fields[item]
                editProfileFragment.allEditTexts.forEach {
                    if (it.hint.toString() == selectedText) it.visibility = View.VISIBLE
                }
                initializeFields(editProfileFragment)
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        // Сначала загружаем фото на сервер, если успешно, то только потом сохраняем данные пользователя
        @SuppressLint("ShowToast")
        private fun saveUser(editProfileFragment: EditProfileFragment) {
            editProfileFragment.progressbar.visibility = View.VISIBLE
            if (editProfileFragment.uri != "") {
                editProfileFragment.uuid = UUID.randomUUID().toString()
                editProfileFragment.mStorageRef = FirebaseStorage.getInstance().getReference(
                    editProfileFragment.uuid
                )
                editProfileFragment.mStorageRef.putFile(Uri.parse(editProfileFragment.uri))
                    .addOnSuccessListener {
                        Toast.makeText(
                            editProfileFragment.context,
                            "Profile photo has been downloaded!",
                            Toast.LENGTH_LONG
                        ).show()
                        saveUserData(editProfileFragment)
                    }
            } else {
                Toast.makeText(
                    editProfileFragment.context,
                    "Фото не выбрано",
                    Toast.LENGTH_LONG
                ).show()
                saveUserData(editProfileFragment)
            }
        }

        @SuppressLint("ShowToast")
        private fun saveUserData(editProfileFragment: EditProfileFragment) {
            var user = editProfileFragment.db.userDao().getOwnerUser()
            val userUUID = user.uuid
            user = getUserData(editProfileFragment)
            user.parentId = userUUID
            user.uuid = userUUID

            editProfileFragment.db.userDao().updateUser(user)

            val databaseRef =
                FirestoreInstance.getInstance().collection("users").document(user.uuid)
                    .collection("data").document(user.uuid)
            databaseRef.set(userToMap(user))
                .addOnSuccessListener {
                    Toast.makeText(
                        editProfileFragment.context,
                        "DocumentSnapshot successfully written!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        editProfileFragment.context,
                        "Error writing document: $e",
                        Toast.LENGTH_LONG
                    ).show()
                }

            editProfileFragment.parentFragmentManager.popBackStack()
        }

        // Функция для реализации возможности удаления доп. полей
        private fun deleteField(
            editProfileFragment: EditProfileFragment,
            editText: EditText,
            event: MotionEvent
        ): Boolean {
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    val imm =
                        editProfileFragment.requireActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        editProfileFragment.requireActivity().currentFocus?.windowToken,
                        0
                    )
                    editText.visibility = View.GONE
                    editText.text.clear()
                    initializeFields(editProfileFragment)
                    return true
                }
            }
            return false
        }
    }


}