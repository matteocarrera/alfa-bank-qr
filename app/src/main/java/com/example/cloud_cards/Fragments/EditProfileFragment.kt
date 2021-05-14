package com.example.cloud_cards.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ImageUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.ByteArrayOutputStream
import java.util.*

class EditProfileFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var mStorageRef: StorageReference
    private var uuid = ""
    private var parentId = ""
    private var photoUuid = ""
    private var photoWasChanged = false
    private var ownerUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById(R.id.toolbar_edit_profile) as MaterialToolbar
        toolbar.inflateMenu(R.menu.edit_profile_menu)
        toolbar_edit_profile.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save_user -> {
                    saveUser()
                }
            }
            true
        }

        db = AppDatabase.getInstance(requireContext())
        ownerUser = db.userDao().getOwnerUser()

        setUserData()

        change_photo.setOnClickListener {
            CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(requireContext(), this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                photo.setImageURI(result.uriContent)
                Log.d("TAG", "saving photo")
                photoWasChanged = true
            }
        }
    }

    private fun setUserData() {
        val user = db.userDao().getOwnerUser()
        if (user != null) {
            ImageUtils.getImageFromFirebase(user.photo, photo)
            surnameField.setText(user.surname)
            nameField.setText(user.name)
            patronymicField.setText(user.patronymic)
            companyField.setText(user.company)
            jobTitleField.setText(user.jobTitle)
            mobileField.setText(user.mobile)
            mobileSecondField.setText(user.mobileSecond)
            emailField.setText(user.email)
            emailSecondField.setText(user.emailSecond)
            addressField.setText(user.address)
            addressSecondField.setText(user.addressSecond)
            websiteField.setText(user.website)
            vkField.setText(user.vk)
            telegramField.setText(user.telegram)
            facebookField.setText(user.facebook)
            twitterField.setText(user.twitter)
            instagramField.setText(user.instagram)
            notesField.setText(user.notes)
        }
    }

    private fun getUserData() : User {
        val user = User()
        user.parentId = parentId
        user.uuid = uuid
        user.photo = photoUuid
        user.name = nameField.text.toString()
        user.surname = surnameField.text.toString()
        user.patronymic = patronymicField.text.toString()
        user.company = companyField.text.toString()
        user.jobTitle = jobTitleField.text.toString()
        user.mobile = mobileField.text.toString()
        user.mobileSecond = mobileSecondField.text.toString()
        user.email = emailField.text.toString()
        user.emailSecond = emailSecondField.text.toString()
        user.address = addressField.text.toString()
        user.addressSecond = addressSecondField.text.toString()
        user.website = websiteField.text.toString()
        user.vk = vkField.text.toString()
        user.telegram = telegramField.text.toString()
        user.facebook = facebookField.text.toString()
        user.instagram = instagramField.text.toString()
        user.twitter = twitterField.text.toString()
        user.notes = notesField.text.toString()
        return user
    }

    // Сначала загружаем фото на сервер, если успешно, то только потом сохраняем данные пользователя
    private fun saveUser() {
        if (nameField.text.toString().isEmpty() ||
            surnameField.text.toString().isEmpty() ||
            mobileField.text.toString().isEmpty() ||
            emailField.text.toString().isEmpty()) {

            Toast.makeText(requireContext(),
                "Обязательные поля: имя, фамилия, мобильный номер и email - не заполнены!",
                Toast.LENGTH_SHORT).show()
            return
        }

        progressbar.visibility = View.VISIBLE

        val oldPhotoId = ownerUser?.photo

        if (photoWasChanged) {
            if (!oldPhotoId.isNullOrEmpty()) {
                mStorageRef = FirebaseStorage.getInstance().getReference(oldPhotoId)
                mStorageRef.delete()
            }
            photoUuid = UUID.randomUUID().toString()
            mStorageRef = FirebaseStorage.getInstance().getReference(photoUuid)

            val bitmap = (photo.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data = baos.toByteArray()

            mStorageRef.putBytes(data).addOnSuccessListener {
                saveUserData()
            }
        } else {
            photoUuid = if (!oldPhotoId.isNullOrEmpty()) oldPhotoId else ""
            saveUserData()
        }
    }

    private fun saveUserData() {
        val user: User
        if (ownerUser == null) {
            uuid = UUID.randomUUID().toString()
            parentId = uuid
            user = getUserData()
            db.userDao().insertUser(user)
        } else {
            uuid = ownerUser!!.uuid
            parentId = ownerUser!!.parentId
            user = getUserData()
            db.userDao().updateUser(user)
        }

        FirebaseFirestore.getInstance()
            .collection("users").document(uuid)
            .collection("data").document(uuid)
            .set(user)
        parentFragmentManager.popBackStack()
    }
}