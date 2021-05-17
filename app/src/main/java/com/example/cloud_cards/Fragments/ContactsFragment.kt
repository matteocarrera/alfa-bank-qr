package com.example.cloud_cards.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud_cards.Activities.CameraActivity
import com.example.cloud_cards.Adapters.ContactsAdapter
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Entities.UserBoolean
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.DataUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_qr.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment() {

    private lateinit var db: AppDatabase
    private var contactList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        val toolbar = view.findViewById(R.id.contacts_toolbar) as MaterialToolbar
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort -> {
                    Toast.makeText(requireContext(), "SORT", Toast.LENGTH_SHORT).show()
                }
                R.id.search -> {
                    Toast.makeText(requireContext(), "SEARCH", Toast.LENGTH_SHORT).show()
                }
                R.id.camera -> {
                    val intent = Intent(context, CameraActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        toolbar.setNavigationOnClickListener {
            Toast.makeText(requireContext(), "CHANGE", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        contactList.clear()
        progress_bar.visibility = View.VISIBLE

        val ownerUser = db.userDao().getOwnerUser()
        val idPairs = ArrayList(db.idPairDao().getAllContactsPairs(ownerUser?.parentId))

        Thread {
            idPairs.forEach { idPair ->
                FirebaseFirestore.getInstance()
                    .collection("users").document(idPair.parentUuid)
                    .collection("cards").document(idPair.uuid)
                    .get().addOnSuccessListener { document ->
                        val businessCardUser = Gson().fromJson(Gson().toJson(document.data).toString(), UserBoolean::class.java)
                        FirebaseFirestore.getInstance()
                            .collection("users").document(idPair.parentUuid)
                            .collection("data").document(idPair.parentUuid)
                            .get().addOnSuccessListener { secondDocument ->
                                val mainUser = Gson().fromJson(Gson().toJson(secondDocument.data).toString(), User::class.java)
                                val currentUser = DataUtils.getUserFromTemplate(mainUser, businessCardUser)

                                contactList.add(currentUser)

                                if (contactList.size == idPairs.size) {
                                    contactList.sortBy { it.surname }
                                    contact_list.apply {
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = ContactsAdapter(contactList, this@ContactsFragment)
                                    }
                                    progress_bar.visibility = View.GONE
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Error", "get failed with ", exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Error", "get failed with ", exception)
                    }
            }
        }.start()
    }
}