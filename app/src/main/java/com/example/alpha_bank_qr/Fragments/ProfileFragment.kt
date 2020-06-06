package com.example.alpha_bank_qr.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Database.DBService
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataToListView(view)
    }

    private fun setDataToListView(view: View) {
        try {
            val user = DBService.getOwnerUser(view.context)
            val photoUUID = user.photo
            if (photoUUID != "") {
                ImageUtils.getImageFromFirebase(photoUUID, profile_photo)
            } else {
                profile_photo.visibility = View.GONE
                circle.visibility = View.VISIBLE
                letters.text = user.name.take(1) + user.surname.take(1)
            }
            val data = DataUtils.setUserData(user)

            val adapter = DataListAdapter(activity!!, data, R.layout.data_list_item)
            data_list.adapter = adapter
        } catch (e : Exception) {
            TODO("Кнопка ДОБАВИТЬ ПРОФИЛЬ")
            //add_profile.visibility = View.VISIBLE
        }
    }

}