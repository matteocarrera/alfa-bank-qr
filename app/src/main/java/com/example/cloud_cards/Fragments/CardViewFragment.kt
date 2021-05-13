package com.example.cloud_cards.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cloud_cards.Adapters.DataListAdapter
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.DataUtils
import com.example.cloud_cards.Utils.ImageUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_card_view.*
import kotlinx.android.synthetic.main.fragment_card_view.circle
import kotlinx.android.synthetic.main.fragment_card_view.letters
import kotlinx.android.synthetic.main.fragment_card_view.profile_photo

class CardViewFragment : Fragment() {
    private var userId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_view, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireArguments().getString("ID").toString()
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()

        val databaseRef = FirebaseDatabase.getInstance().getReference(userId)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val jsonUser = dataSnapshot.value.toString()
                val userFromDB = Gson().fromJson(jsonUser, User::class.java)

                val photoUUID = userFromDB.photo
                if (photoUUID != "") {
                    ImageUtils.getImageFromFirebase(photoUUID, profile_photo)
                } else {
                    profile_photo.visibility = View.GONE
                    circle.visibility = View.VISIBLE
                    letters.text = userFromDB.name.take(1) + userFromDB.surname.take(1)
                }
                val data = DataUtils.setUserData(userFromDB)

                val adapter = DataListAdapter(requireActivity(), data, R.layout.data_list_item)
                data_list.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Ошибка считывания: " + databaseError.code)
            }
        })
    } */

    private fun setToolbar() {
        toolbar_card_view.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userId : String) : CardViewFragment {
            val args = Bundle()
            args.putString("ID", userId)
            val fragment = CardViewFragment()
            fragment.arguments = args
            return fragment
        }

    }
}