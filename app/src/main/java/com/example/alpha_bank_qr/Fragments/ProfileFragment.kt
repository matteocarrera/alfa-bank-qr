package com.example.alpha_bank_qr.Fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.alpha_bank_qr.Adapters.DataListAdapter
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.DataUtils
import com.example.alpha_bank_qr.Utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        setToolbar(this)
        setDataToListView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    companion object {
        private fun setToolbar(profileFragment: ProfileFragment) {
            profileFragment.toolbar_profile.setOnMenuItemClickListener {
                if (it.itemId == R.id.edit) {
                    val tx: FragmentTransaction =
                        profileFragment.parentFragmentManager.beginTransaction()
                    tx.replace(R.id.nav_host_fragment, EditProfileFragment()).addToBackStack(null)
                        .commit()
                }
                true
            }
        }

        private fun setDataToListView(profileFragment: ProfileFragment) {
            val user = profileFragment.db.userDao().getOwnerUser()
            val photoUUID = user.photo
            if (photoUUID != "") {
                ImageUtils.getImageFromFirebase(photoUUID, profileFragment.profile_photo)
            } else {
                profileFragment.profile_photo.visibility = View.GONE
                profileFragment.circle.visibility = View.VISIBLE
                profileFragment.letters.text = user.name.take(1) + user.surname.take(1)
            }
            val data = DataUtils.setUserData(user)

            val adapter =
                DataListAdapter(profileFragment.requireActivity(), data, R.layout.data_list_item)
            profileFragment.data_list.adapter = adapter

        }
    }

}