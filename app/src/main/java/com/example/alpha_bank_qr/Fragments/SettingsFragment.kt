package com.example.alpha_bank_qr.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        val user = this.db.userDao().getOwnerUser()
        ImageUtils.getImageFromFirebase(user.photo, this.settings_photo)
        this.nameSurname.text = user.name + " " + user.surname
        this.phoneNumber.text = user.mobile
        this.email.text = user.email

        this.setting_toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.redact_profile) {
                val fragment = EditProfileFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            true
        }
    }
}