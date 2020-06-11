package com.example.alpha_bank_qr.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha_bank_qr.Adapters.ContactsAdapter
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.R
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment() {

    private lateinit var db : AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        val users = db.userDao().getScannedUsers()

        contact_list.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = ContactsAdapter(users)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactsFragment()
    }
}