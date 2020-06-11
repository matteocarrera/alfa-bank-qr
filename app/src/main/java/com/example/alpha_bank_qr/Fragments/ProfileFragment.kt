package com.example.alpha_bank_qr.Fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Database.DBService
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var db : AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        setToolbar()
        setDataToListView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    private fun setToolbar() {
        toolbar_profile.setOnMenuItemClickListener {
            if (it.itemId == R.id.edit) {
                val tx: FragmentTransaction = parentFragmentManager.beginTransaction()
                tx.replace(R.id.nav_host_fragment, EditProfileFragment()).addToBackStack(null).commit()
            }
            true
        }
    }

    private fun setDataToListView() {
        val user = db.userDao().getOwnerUser()
        if (user == null) {
            profile_is_empty.visibility = View.VISIBLE
        } else {
            val photoUUID = user.photo
            if (photoUUID != "") {
                ImageUtils.getImageFromFirebase(photoUUID, profile_photo)
            } else {
                profile_photo.visibility = View.GONE
                circle.visibility = View.VISIBLE
                letters.text = user.name.take(1) + user.surname.take(1)
            }
            val data = DataUtils.setUserData(user)

            val adapter = DataListAdapter(requireActivity(), data, R.layout.data_list_item)
            data_list.adapter = adapter
        }

    }

}