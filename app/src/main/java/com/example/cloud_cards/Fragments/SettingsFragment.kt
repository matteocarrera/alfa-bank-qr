package com.example.cloud_cards.Fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ImageUtils
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private lateinit var db : AppDatabase

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val toolbar = view.findViewById(R.id.toolbar) as MaterialToolbar
        toolbar.inflateMenu(R.menu.change_profile_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.change_user -> {
                    val tx: FragmentTransaction = parentFragmentManager.beginTransaction()
                    tx.replace(R.id.nav_host_fragment, EditProfileFragment()).addToBackStack(null).commit()
                }
            }
            true
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserInfo()
    }

    private fun getUserInfo() {
        db = AppDatabase.getInstance(requireContext())
        val ownerUser = db.userDao().getOwnerUser()
        if (ownerUser != null) {
            ImageUtils.getImageFromFirebase(ownerUser.photo, settings_profile_photo)
            settings_name.text = ownerUser.name.plus(" ").plus(ownerUser.surname)
            settings_mobile.text = ownerUser.mobile
            settings_email.text = ownerUser.email
        }
    }
}